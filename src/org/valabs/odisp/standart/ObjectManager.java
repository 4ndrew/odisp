package org.valabs.odisp.standart;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.doomdark.uuid.UUID;
import org.valabs.odisp.common.Dispatcher;
import org.valabs.odisp.common.Message;
import org.valabs.odisp.common.ODObject;
import org.valabs.odisp.common.WeakDependency;
import org.valabs.stdmsg.ODObjectLoadedMessage;
import org.valabs.stdmsg.ODShutdownMessage;

import com.novel.tools.filter.Filter;
import com.novel.tools.filter.FilteringIterator;
import com.sun.corba.se.spi.legacy.connection.GetEndPointInfoAgainException;

/**
 * �������� �������� ODISP.
 * 
 * @author (C) 2004 <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev </a>
 * @version $Id: ObjectManager.java,v 1.74 2007/01/24 14:21:31 dron Exp $
 */

class ObjectManager implements org.valabs.odisp.common.ObjectManager {

  /** ��������� ��������. */
  private final Dispatcher dispatcher;

  /** ������� �������. */
  private final DispatcherSnapshot snapshot = new DispatcherSnapshot();

  /** ��������� ���������� ���������. */
  private final DefferedMessages messages = new DefferedMessages();

  /** ������ ��������. */
  private final Map objects = new LinkedHashMap();

  /** ������. */
  private final static Logger log = Logger.getLogger(ObjectManager.class.getName());

  /** ������ �������� ���������. */
  private final Map provided = new HashMap();
  
  /** ������������ ���������� ����� ����������� ��� ������� ����������. */
  public static final int SENDER_POOL_SIZE = 5;

  /** ��� ����� �������. */
  private final Sender[] senderPool;

  /** ��������� ��� ���������. */
  private final List messageStorage = new LinkedList();
  
  /** ������������ ������ */

  /** ����� ����������� ���� hints. */
  private Hints hints = null;
  
  /** ����� ������ �������. */
  private long startupTime = 0;

  /**
   * ���������� ������� ��� ���������� ����������� �������.
   * 
   * @param service �������� �������
   * @param objectName �������� �������
   */
  public final void addProvider(final String service, final String objectName) {
    if (!provided.containsKey(service)) {
      provided.put(service, new ArrayList(3));
    }
    ((List) provided.get(service)).add(objectName);
  }
  
  /**
   * �������� ���������� ����������� �������. � ������ ���� � ������� �� �������� �� ������ ���������� -- ��
   * ������������� ����� ������.
   * 
   * @param service �������� �������
   * @param objectName �������� �������
   */
  public void removeProvider(final String service, final String objectName) {
    if (provided.containsKey(service)) {
      ((List) provided.get(service)).remove(objectName);
      if (((List) provided.get(service)).size() == 0) {
        provided.remove(service);
      }
    }
  }

  /**
   * �������� �� ������������� ����������� �������.
   * 
   * @param service �������� �������
   * @return ���� ����������� �������
   */
  private boolean hasProviders(final String service) {
    return provided.containsKey(service);
  }

  /**
   * �������� ������ ��������-����������� �������.
   * 
   * @param service ��� �������
   * @return ���������������� thread-safe ������ ��������
   */
  private List getProviders(final String service) {
    List result = null;
    if (provided.containsKey(service)) {
      result = Collections.unmodifiableList(Collections.synchronizedList((List) provided.get(service)));
    }
    return result;
  }

  /**
   * �������� ������ �������� ����������.
   * 
   * @return ���������������� ������ ��������
   */
  public List getProviding() {
    return new ArrayList(Collections.unmodifiableSet(provided.keySet()));
  }

  /** ������� ��������� �������� � ��������� ��������� ������ �������� ���������. */
  public final void loadPending() {
    // resources
    final Map resourceList = new HashMap(dispatcher.getResourceManager().getResources());
    Iterator commonIt = resourceList.keySet().iterator();
    while (commonIt.hasNext()) {
      final String objectName = (String) commonIt.next();
      if (!hasProviders(objectName)) {
        // ������� ��������� ������������ ������� � ����������� ������
        addProvider(objectName, objectName);
        log.finest("added resource provider " + objectName);
        hints.addNewHint(objectName);
      }
    }

    final Map localObjects = new LinkedHashMap(objects); // hints.getHintedOrder(objects);
    int statToLoadCount = objects.size();
    while (statToLoadCount != 0) {
      int loaded = 0;
      commonIt = localObjects.keySet().iterator();
      while (commonIt.hasNext()) {
        final String objectName = (String) commonIt.next();
        final ObjectEntry oe = (ObjectEntry) localObjects.get(objectName);
        if (oe.isLoaded()) {
          commonIt.remove();
          continue;
        }
        log.finest("trying to load object " + objectName);
        // ��� ������� ������� ����������� �������������
        if (provided.keySet().containsAll(oe.getDepends())) {
          // �������� ������ ������������
          if (oe.getWeakDepends().size() > 0) {
            int haveCounter = 0;
            int doneCounter = 0;
            Iterator it = oe.getWeakDepends().iterator();
            while (it.hasNext()) {
              String element = (String) it.next();
              if (objects.containsKey(element)) {
                haveCounter++;
              }
              if (provided.keySet().contains(element)) {
                doneCounter++;
              }
            }
            if (haveCounter > doneCounter) {
              // �� ��� weak dependencies ������������� �� ���������
              continue;
            }
          }
          // ��������� � �������� ���������� ��� ��������� ��������
          final Iterator provideIt = oe.getProvides().iterator();
          while (provideIt.hasNext()) {
            final String providing = (String) provideIt.next();
            log.finest("added as provider of " + providing);
            addProvider(providing, objectName);

          }

          // ��������� � ������ RECIPIENT_ALL
          addProvider(Message.RECIPIENT_ALL, objectName);
          // ���� ������ ����� �������� ��� ���������, �� ������� ��� � RECIPIENT_CATCHALL
          if (oe.getObject().getMatchAll()) {
            addProvider(Message.RECIPIENT_CATCHALL, objectName);
          }
          // ������� ������� ��� �����������
          oe.setLoaded(true);
          log.config(" ok. loaded = " + objectName);
          // �������������� ������ �� ������ ���� �� ���
          if (snapshot.hasSnapshot()) {
            log.config("Restoring state from snapshot for " + objectName);
            oe.getObject().importState(snapshot.getObjectSnapshot(objectName));
          }
          // ����������� ����������� ������� � ��������
          final Message m = dispatcher.getNewMessage();
          ODObjectLoadedMessage.setup(m, objectName, UUID.getNullUUID());
          m.setDestination(objectName);
          // ������ ���������ң���� weak dependencies.
          if (oe.getWeakDepends().size() > 0) {
            List doneWeakDeps = new LinkedList();
            Iterator it = oe.getWeakDepends().iterator();
            while (it.hasNext()) {
              String element = (String) it.next();
              if (objects.containsKey(element)) {
                doneWeakDeps.add(element);
              }
            }
            ODObjectLoadedMessage.setWeakDependencies(m, doneWeakDeps);
          }
          oe.getObject().handleMessage0(m);
          // ����� ������������ ���������
          flushDefferedMessages(objectName);
          // ������ � hints ���� � ������ �������������
          if (oe.isIntoHints()) {
            hints.addNewHint(oe.getObject().getClass().getName());
          }
          statToLoadCount--;
          loaded++;
          commonIt.remove();
        }
      }
      if (loaded == 0) {
        log.warning("Some of the objects failed to load. There is something wrong with dependencies.");
        
        final List badObjects = new ArrayList();
        final Iterator it = new FilteringIterator(objects.values().iterator(), new Filter() {
					public boolean accept(final Object o) {
						if (o instanceof ObjectEntry) {
							ObjectEntry elt = (ObjectEntry) o;
							return !elt.isLoaded();
						}
						return false;
					}
				});
        while (it.hasNext()) {
          final ObjectEntry el = (ObjectEntry) it.next();
          badObjects.add(el.getObject().getObjectName() + ",depends=" + el.getDepends());
        }
        log.fine("Objects failed to load: " + badObjects.toString());
        break;
      }
    }
    if (statToLoadCount == 0) {
      hints.storeHints();
      snapshot.clearSnapshot();
    }
    startupTime = Calendar.getInstance().getTimeInMillis();
    Runtime.getRuntime().addShutdownHook(new Thread() {
      public void run() {
        log.info("System uptime: " + (Calendar.getInstance().getTimeInMillis() - startupTime));
      }
    });
  }

  /**
   * ������������ �������� ������� (� �ޣ��� ������������). ���������� ������� � hints ��������.
   * 
   * @param cName ��� ������������ ������
   * @param configuration ������ ���������� ��������
   */
  public final void loadObject(final String cName, final Map configuration) {
    if (cName.equals(DispatcherHandler.class.getName())) {
      /** @todo. ������ ���. */
      loadObject(cName, configuration, false);
      return;
    }
    loadObject(cName, configuration, true);
  }

  /**
   * ������������ �������� ������� (� �ޣ��� ������������).
   * 
   * @param cName ��� ������������ ������
   * @param configuration ������ ���������� ��������
   * @param intoHints ��������� �� ������ � ���� hints
   */
  public final void loadObject(final String cName, final Map configuration, final boolean intoHints) {
    log.config("loading object " + cName);
    try {
      final ODObject load = (ODObject) Class.forName(cName).newInstance();
      load.setDispatcher(new SecureDispatcher(dispatcher, load.getObjectName()));
      load.setConfiguration(configuration);
      final ObjectEntry oe = new ObjectEntry(cName, load);
      oe.setLoaded(false);
      oe.setIntoHints(intoHints);
      synchronized (objects) {
        objects.put(load.getObjectName(), oe);
      }
    } catch (Exception e) {
      dispatcher.getExceptionHandler().signalException(e);
    }
  }

  /**
   * �������� ������� � �ޣ��� ���������.
   * <ul>
   * <li>����������� ������ ��������� ��������
   * <li>�������� ��������� ��������
   * <li>���������� ������� �������, � ������ ���� ���������� ������������ ����������
   * <li>�������� ������ �������
   * </ul>
   * 
   * @param objectName ���������� ��� ������� ��� ��������.
   * @param code ��� ������ (��� code != 0 ��������� ������� �� ���������).
   */
  public synchronized final void unloadObject(final String objectName, final int code) {
    if (objects.containsKey(objectName)) {
      final ObjectEntry oe = (ObjectEntry) objects.get(objectName);
      final Set provides = oe.getProvides();
      Iterator it = objects.keySet().iterator();
      final Set dependingObjs = new HashSet();

      // ����� ��������� ��������
      while (it.hasNext()) {
        final String depObjectName = (String) it.next();
        final Set depends = ((ObjectEntry) objects.get(depObjectName)).getDepends();
        final Iterator pit = provides.iterator();
        while (pit.hasNext()) {
          final String element = (String) pit.next();
          if (depends.contains(element) || depends.contains(WeakDependency.create(element))) {
            dependingObjs.add(depObjectName);
          }
        }
      }

      // �������� �� ������ ��������
      it = provides.iterator();
      while (it.hasNext()) {
        final String element = (String) it.next();
        removeProvider(element, objectName);
      }

      // �������� ��������� ��������
      it = dependingObjs.iterator();
      while (it.hasNext()) {
        final String className = (String) it.next();
        log.finest("removing " + objectName + "'s dependency " + className);
        unloadObject(className, code);
      }

      // ������������ ���������� ������ (����� ������ ������� �� ������
      // ��������� ����� ������ ���)
      try {
        oe.getObject().cleanUp(code);
      } catch (Exception e) {
        dispatcher.getExceptionHandler().signalException(e);
      }

      // ���������� ������ �������, � ������ ���� ���������� ���������� ����������
      if (code == ODShutdownMessage.SHUTDOWN_RESTART) {
        snapshot.addObjectSnapshot(objectName, oe.getObject().exportState());
      }

      // �������� �������
      objects.remove(objectName);
      log.config("object " + objectName + " unloaded");
    }
  }

  /**
   * ������ � ������ ��������.
   * 
   * @return ������ ��������
   */
  public final Map getObjects() {
    return objects;
  }

  /**
   * ����������� ���������.
   * 
   * @param newDispatcher ��������� ��� �������� ������������ ���������� ���������
   */
  public ObjectManager(final Dispatcher newDispatcher) {
    dispatcher = newDispatcher;
    log.setLevel(Level.FINE);
    senderPool = new Sender[SENDER_POOL_SIZE];
    for (int i = 0; i < SENDER_POOL_SIZE; i++) {
      senderPool[i] = new Sender(this);
    }
    hints = new Hints();
  }

  /**
   * ������� ��������� ����������� �������.
   * 
   * @param objectName ��� �������
   * @param message ���������
   */
  private void sendToObject(final String objectName, final Message message) {
    ObjectEntry oe = null;
    // ��������� ����������� ������ ������������ ��������
    synchronized (objects) {
      oe = (ObjectEntry) objects.get(objectName);
    }
    if (oe == null) { return; }
    ODObject objToSendTo = null;
    // ��������� ����������� ����������� ��������� �������
    synchronized (oe) {
      if (!oe.isLoaded()) {
        log.finest("deffered message " + message.getAction() + " for " + objectName);
        messages.addMessage(objectName, message);
        return;
      }
      objToSendTo = oe.getObject();
    }
    synchronized (messageStorage) {
      if (message.isOOB()) {
        log.finest("Sending OOB message " + message);
        messageStorage.add(0, new SendRecord(message, objToSendTo));
      } else {
        messageStorage.add(new SendRecord(message, objToSendTo));
      }
    }
  }

  /**
   * ������� ��������� ���� �������� ���������.
   * 
   * @param message ���������
   */
  public final void send(final Message message) {
    if (message == null || message.getAction() == null || message.getAction().length() == 0 || !message.isCorrect()) {
      // ����� ��� ���� ���� �������, ��� ��� ��������� ������� ��-�� ������������ ����� --- ���� �������� ��������
      log.warning("Invalid message " + message + " couldn't be delivered.");
      return;
    }

    // �������� �������� ���������
    Iterator it;
    final Set recipients = new HashSet();
    if (getProviders(message.getDestination()) != null) {
      recipients.addAll(getProviders(message.getDestination()));
    }
    if (getProviders(Message.RECIPIENT_CATCHALL) != null) {
      recipients.addAll(getProviders(Message.RECIPIENT_CATCHALL));
    }
    
    if (recipients.size() > 0) {
      it = recipients.iterator();
      Message actualMessage;
      while (it.hasNext()) {
        final String objectName = (String) it.next();
//        actualMessage = message;
//        if (!(actualMessage instanceof COWStandardMessage)) {
//        	actualMessage = new COWStandardMessage(message);
//        } else {
//          actualMessage = message.cloneMessage();
//        }
        actualMessage = message.cloneMessage();
        if (message.getDestination().equals(Message.RECIPIENT_ALL)) {
        	actualMessage.setDestination(objectName);
        }
        sendToObject(objectName, actualMessage);
      }
    }
  }

  /**
   * ����� ���������� ��������� ��� ������ ���������� � �������.
   * 
   * @param objectName ��� �������
   */
  private void flushDefferedMessages(final String objectName) {
    if (objects.containsKey(objectName)) {
      final Iterator it = messages.flush(objectName).iterator();
      while (it.hasNext()) {
        sendToObject(objectName, (Message) it.next());
      }
    }
  }

  /** ��������� ���������� ��������� ��� ���������. 
   * @return �������� ���������� ������� ��� �������
   */
  final SendRecord getNextPendingMessage() {
    SendRecord toSend = null;
    synchronized (messageStorage) {
      if (messageStorage.size() > 0) {
        // LinkedList � 5 ��� ������� ��� ������������ ��� ��� ����������
        toSend = (SendRecord) messageStorage.iterator().next();
        // LinkedList � 1905 ��� ������� ��� �������� ��� ArrayList
        messageStorage.remove(0);
      }
    }
    return toSend;
  }

  public final void signalException(final Exception e) {
    dispatcher.getExceptionHandler().signalException(e);
  }

  class Hints {

    private final List oldHints = new ArrayList();
    private final List newHints = new ArrayList();

    public void addNewHint(final String object) {
      newHints.add(object);
    }
    
    public Hints() {
      try {
        final BufferedReader sourceFile = new BufferedReader(new FileReader("hints"));
        String s = sourceFile.readLine();
        while (s != null) {
          oldHints.add(s);
          s = sourceFile.readLine();
        }
      } catch (IOException e) {
        log.throwing(Hints.class.getName(), "<init>", e);
      }
    }

    public Map getHintedOrder(final Map currentObjects) {
      Map localObjects;
      if (oldHints.size() > 0) {
        localObjects = new TreeMap(new HintsOrderComparator(oldHints));
      } else {
        localObjects = new HashMap();
      }
      synchronized (currentObjects) {
        localObjects.putAll(currentObjects);
      }
      return localObjects;
    }

    /**
     * 
     */
    public final void storeHints() {
      Iterator it;
      // ������� ������� ������� ��������.
      String msg = "\n============================================\n";
      if (newHints.size() > 0) {
        msg += "Result hints file:\n";
        /** @todo. HACK ���� hints ������� � ������� �������, ��� �� ���� ���. */
        try {
          final File hintsFile = new File("hints");
          hintsFile.createNewFile();
          final PrintStream out = new PrintStream(new FileOutputStream(hintsFile));
          it = newHints.iterator();
          while (it.hasNext()) {
            final String elt = (String) it.next();
            out.println(elt);
            msg += "\t" + elt + "\n";
          }
        } catch (IOException e) {
          log.warning("Unable to write hints file.");
          dispatcher.getExceptionHandler().signalException(e);
        }
        msg += "Total: " + newHints.size() + "\n";
        msg += "============================================\n";
        log.fine(msg);
      }
    }

    class HintsOrderComparator implements Comparator {
      private List hints;
      
      public HintsOrderComparator(List _hints) {
        hints = _hints;
      }
      
      public int compare(Object _o1, Object _o2) {
        String o1 = ((ObjectEntry) objects.get(_o1)).getClassName();
        String o2 = ((ObjectEntry) objects.get(_o2)).getClassName();
        if (o1.equals(o2)) { return 0; }
        if (o1.equals(DispatcherHandler.class.getName())) { return 1; }
        if (o2.equals(DispatcherHandler.class.getName())) { return -1; }
        if (hints != null) {
          if (hints.contains(o1) && hints.contains(o2)) {
            return (hints.indexOf(o1) < hints.indexOf(o2)) ? -1 : 1;
          } else if (hints.contains(o1)) {
            return 1;
          } else if (hints.contains(o2)) { return -1; }
        }
        return 0;
      }
    }
  }
} // StandartObjectManager
