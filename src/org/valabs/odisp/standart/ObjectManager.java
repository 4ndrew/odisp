package org.valabs.odisp.standart;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
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
import org.valabs.stdmsg.ODObjectLoadedMessage;
import org.valabs.stdmsg.ODShutdownMessage;

/**
 * �������� �������� ODISP.
 * 
 * @author (C) 2004 <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev </a>
 * @version $Id: ObjectManager.java,v 1.50 2005/02/17 12:29:10 valeks Exp $
 */

class ObjectManager implements org.valabs.odisp.common.ObjectManager {

  /** ��������� ��������. */
  private Dispatcher dispatcher;

  /** ������� �������. */
  private DispatcherSnapshot ds = new DispatcherSnapshot();

  /** ��������� ���������� ���������. */
  private DefferedMessages messages = new DefferedMessages();

  /** ������ ��������. */
  private Map objects = new HashMap();

  /** ������. */
  private Logger log = Logger.getLogger(ObjectManager.class.getName());

  /** ������ �������� ���������. */
  private Map provided = new HashMap();

  /** ��� ����� �������. */
  private List senderPool = new ArrayList();

  /** ������������ ���������� ����� ����������� ��� ������� ����������. */
  public static final int SENDER_POOL_SIZE = 5;

  /** ��������� ��� ���������. */
  private List messageStorage = new ArrayList();

  /** ����� ����������� ���� hints. */
  private Hints hints = null;

  /**
   * ���������� ������� ��� ���������� ����������� �������.
   * 
   * @param service �������� �������
   * @param objectName �������� �������
   */
  public void addProvider(final String service, final String objectName) {
    if (!provided.containsKey(service)) {
      provided.put(service, new ArrayList());
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
    if (provided.containsKey(service)) {
      return Collections.unmodifiableList(Collections.synchronizedList((List) provided.get(service)));
    } else {
      return null;
    }
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
    Map resourceList = new HashMap(dispatcher.getResourceManager().getResources());
    Iterator it = resourceList.keySet().iterator();
    while (it.hasNext()) {
      String objectName = (String) it.next();
      if (!hasProviders(objectName)) {
        // ������� ��������� ������������ ������� � ����������� ������
        addProvider(objectName, objectName);
        log.finest("added resource provider " + objectName);
        hints.addNewHint(objectName);
      }
    }

    Map localObjects = hints.getHintedOrder(objects);
    int statToLoadCount = objects.size();
    while (statToLoadCount != 0) {
      int loaded = 0;
      it = localObjects.keySet().iterator();
      while (it.hasNext()) {
        String objectName = (String) it.next();
        ObjectEntry oe = (ObjectEntry) objects.get(objectName);
        if (oe.isLoaded()) {
          continue;
        }
        log.finest("trying to load object " + objectName);
        // �������� �� �������������� ���� ������������
        int totalDependencies = oe.getDepends().size();
        Iterator dit = oe.getDepends().iterator();
        while (dit.hasNext()) {
          String dependency = (String) dit.next();
          if (hasProviders(dependency)) {
            totalDependencies--;
          } else {
            log.finest("dependency not met: " + dependency);
          }

        }

        // ��� ������� ����������� �������������
        if (totalDependencies == 0) {
          // ��������� � �������� ���������� ��� ��������� ��������
          Iterator pit = oe.getProvides().iterator();
          while (pit.hasNext()) {
            String providing = (String) pit.next();
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
          if (ds.hasSnapshot()) {
            log.config("Restoring state from snapshot for " + objectName);
            oe.getObject().importState(ds.getObjectSnapshot(objectName));
          }
          // ����������� ����������� ������� � ��������
          Message m = dispatcher.getNewMessage();
          ODObjectLoadedMessage.setup(m, objectName, UUID.getNullUUID());
          m.setDestination(objectName);
          oe.getObject().handleMessage0(m);
          // ����� ������������ ���������
          flushDefferedMessages(objectName);
          // ������ � hints ���� � ������ �������������
          if (oe.isIntoHints()) {
            hints.addNewHint(oe.getObject().getClass().getName());
          }
          statToLoadCount--;
          loaded++;
        }
      }
      if (loaded == 0) {
        log.warning("Some of the objects failed to load. There is something wrong with dependencies.");
        break;
      }
    }
    if (statToLoadCount == 0) {
      hints.storeHints();
      ds.clearSnapshot();
    }
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
      ODObject load = (ODObject) Class.forName(cName).newInstance();
      load.setDispatcher(new SecureDispatcher(dispatcher, load.getObjectName()));
      load.setConfiguration(configuration);
      ObjectEntry oe = new ObjectEntry(cName, load.getDepends(), load.getProviding());
      oe.setObject(load);
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
      ObjectEntry oe = (ObjectEntry) objects.get(objectName);
      Set provides = oe.getProvides();
      Iterator it = objects.keySet().iterator();
      Set dependingObjs = new HashSet();

      // ����� ��������� ��������
      while (it.hasNext()) {
        String depObjectName = (String) it.next();
        Set depends = ((ObjectEntry) objects.get(depObjectName)).getDepends();
        Iterator pit = provides.iterator();
        while (pit.hasNext()) {
          String element = (String) pit.next();
          if (depends.contains(element)) {
            dependingObjs.add(depObjectName);
          }
        }
      }

      // �������� �� ������ ��������
      it = provides.iterator();
      while (it.hasNext()) {
        String element = (String) it.next();
        removeProvider(element, objectName);
      }

      // �������� ��������� ��������
      it = dependingObjs.iterator();
      while (it.hasNext()) {
        String className = (String) it.next();
        if (objects.containsKey(className)) {
          log.finest("removing " + objectName + "'s dependency " + className);
          unloadObject(className, code);
        }
      }

      // ������������ ���������� ������
      oe.getObject().cleanUp(code);

      // ���������� ������ �������, � ������ ���� ���������� ���������� ����������
      if (code == ODShutdownMessage.SHUTDOWN_RESTART) {
        if (ds == null) {
          ds = new DispatcherSnapshot();
        }
        ds.addObjectSnapshot(objectName, oe.getObject().exportState());
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
    for (int i = 0; i < SENDER_POOL_SIZE; i++) {
      senderPool.add(new Sender(this));
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
  public final void send(Message message) {
    if (message == null || message.getAction().length() == 0 || !message.isCorrect()) { return; }

    // �������� �������� ���������
    Iterator it;
    Set recipients = new HashSet();
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
        String objectName = (String) it.next();
        actualMessage = message.cloneMessage();
        actualMessage.setDestination(objectName);
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
    if (!objects.containsKey(objectName)) { return; }
    List toFlush = messages.flush(objectName);
    Iterator it = toFlush.iterator();
    while (it.hasNext()) {
      sendToObject(objectName, (Message) it.next());
    }
    loadPending();
  }

  /** ��������� ���������� ��������� ��� ���������. */
  final SendRecord getNextPendingMessage() {
    SendRecord toSend = null;
    synchronized (messageStorage) {
      if (messageStorage.size() > 0) {
        toSend = (SendRecord) messageStorage.get(0);
        messageStorage.remove(0);
      }
      if (ds != null) {
        // XXX: �������� -- ds ��������� ������ ����� ������� ���� ��������
        // �� � ����� ������� ��� ����� ������������ ��������� ����� ODObjectLoaded!
        ds.setMessageQueue(messageStorage);
      }
    }
    return toSend;
  }

  public final void signalException(Exception e) {
    dispatcher.getExceptionHandler().signalException(e);
  }

  class Hints {

    private List oldHints = new ArrayList();
    private List newHints = new ArrayList();

    public void addNewHint(String object) {
      newHints.add(object);
    }
    
    public Hints() {
      try {
        BufferedReader in = new BufferedReader(new FileReader("hints"));
        String s = in.readLine();
        while (s != null) {
          oldHints.add(s);
          s = in.readLine();
        }
      } catch (IOException e) {
      }
    }

    public Map getHintedOrder(Map objects) {
      Map localObjects;
      if (oldHints.size() > 0) {
        localObjects = new TreeMap(new HintsOrderComparator(oldHints));
      } else {
        localObjects = new HashMap();
      }
      synchronized (objects) {
        localObjects.putAll(objects);
      }
      return localObjects;
    }

    /**
     * 
     */
    public void storeHints() {
      Iterator it;
      // ������� ������� ������� ��������.
      String msg = "\n============================================\n";
      if (newHints.size() > 0) {
        msg += "Result hints file:\n";
        /** @todo. HACK ���� hints ������� � ������� �������, ��� �� ���� ���. */
        try {
          File hintsFile = new File("hints");
          hintsFile.createNewFile();
          PrintStream out = new PrintStream(new FileOutputStream(hintsFile));
          it = newHints.iterator();
          while (it.hasNext()) {
            String elt = (String) it.next();
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
