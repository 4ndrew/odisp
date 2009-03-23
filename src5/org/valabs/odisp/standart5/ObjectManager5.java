/* ODISP -- Message Oriented Middleware
 * Copyright (C) 2003-2005 Valentin A. Alekseev
 * Copyright (C) 2003-2005 Andrew A. Porohin 
 * 
 * ODISP is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, version 2.1 of the License.
 * 
 * ODISP is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with ODISP.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.valabs.odisp.standart5;

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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.doomdark.uuid.UUID;
import org.valabs.odisp.common.Dispatcher;
import org.valabs.odisp.common.Message;
import org.valabs.odisp.common.ODObject;
import org.valabs.odisp.common.ObjectManager;
import org.valabs.odisp.common.WeakDependency;
import org.valabs.stdmsg.ODObjectLoadedMessage;
import org.valabs.stdmsg.ODShutdownMessage;
import org.valabs.tools.filter.Filter;
import org.valabs.tools.filter.FilteringIterator;


/** Менеджер объект с поддержкой Java 1.5.
 * @author (C) <a href="mailto:valeks@valabs.spb.ru">Алексеев Валентин А.</a>
 * @version $Id: ObjectManager5.java,v 1.5 2006/03/29 11:29:59 valeks Exp $
 */
class ObjectManager5 implements ObjectManager {
  /** Диспетчер объектов. */
  private final Dispatcher dispatcher;

  /** Снапшот системы. */
  private final DispatcherSnapshot snapshot = new DispatcherSnapshot();

  /** Хранилище отложенных сообщений. */
  private final DefferedMessages messages = new DefferedMessages();

  /** Список объектов. 
   * В среднем ИСМУС сейчас использует порядка 20 объектов (и сервер и клиент) и этот 
   * объем меняется редко (отсюда фактор 0.9). Фактически изменяется этот словарь только
   * основным потоком, по этому стоит порядок конкурентности 1. 
   */
  private final Map<String, ObjectEntry> objects = new ConcurrentHashMap<String, ObjectEntry>(20, (float) 0.9, 1);

  /** Журнал. */
  private final static Logger log = Logger.getLogger(ObjectManager.class.getName());

  /** Список сервисов менеджера. */
  private final Map<String, List<String> > provided = new HashMap<String, List<String>>();

  /** Количество потоков-обработчиков. */
  public static final int SENDER_POOL_SIZE = 5;
  
  private static final long startupTime = Calendar.getInstance().getTimeInMillis();
;
  
  /** Пул нитей отсылки.
   * Изначально создаётся SENDER_POOL_SIZE нитей. В случае если все нити заняты автоматически создаются дополнительные,
   * но в сумме не более 3 * SENDER_POOL_SIZE штук. Дополнительные нити в случае простоя уничтожаются через 50 секунд.
   */
  private final ExecutorService senderPool = new ThreadPoolExecutor(SENDER_POOL_SIZE,
      3 * SENDER_POOL_SIZE, 50L,
      TimeUnit.SECONDS,
      new LinkedBlockingQueue<Runnable>(),
      new ThreadFactory() {
    private int senderId = 0;

    public Thread newThread(Runnable arg0) {
      Thread t = new Thread(arg0);
      t.setDaemon(true);
      t.setName("Sender #" + (senderId++));
      return t;
    }
  });

  /** Ранее загруженный файл hints. */
  private Hints hints = null;

  /**
   * @see org.valabs.odisp.common.ObjectManager#addProvider(java.lang.String, java.lang.String)
   */
  @SuppressWarnings("deprecation")
  public void addProvider(String service, String objectName) {
    if (!provided.containsKey(service)) {
      provided.put(service, new ArrayList<String>(3));
    }
    provided.get(service).add(objectName);
  }

  /**
   * @see org.valabs.odisp.common.ObjectManager#getObjects()
   */
  public Map<String, ObjectEntry> getObjects() {
    return Collections.unmodifiableMap(objects);
  }

  /**
   * @see org.valabs.odisp.common.ObjectManager#getProviding()
   */
  public List<String> getProviding() {
    return new ArrayList<String>(Collections.unmodifiableSet(provided.keySet()));
  }

  /**
   * Динамическая загрузка объекта (с учётом зависимостей). Сохранение порядка в hints включено.
   * 
   * @param cName имя загружаемого класса
   * @param configuration список параметров загрузки
   */
  public final void loadObject(final String cName, final Map configuration) {
    if (cName.equals(DispatcherHandler5.class.getName())) {
      loadObject(cName, configuration, false);
      return;
    }
    loadObject(cName, configuration, true);
  }

  /**
   * Динамическая загрузка объекта (с учётом зависимостей).
   * 
   * @param cName имя загружаемого класса
   * @param configuration список параметров загрузки
   * @param intoHints сохранять ли запись в файл hints
   */
  public final void loadObject(final String cName, final Map configuration, final boolean intoHints) {
    log.config("loading object " + cName);
    try {
      final ODObject load = (ODObject) Class.forName(cName).newInstance();
      load.setDispatcher(new SecureDispatcher(dispatcher, load.getObjectName()));
      load.setConfiguration(configuration);
      final ObjectEntry oe = new ObjectEntry(cName, load);
      oe.setIntoHints(intoHints);
      objects.put(load.getObjectName(), oe);
    } catch (Exception e) {
      dispatcher.getExceptionHandler().signalException(e);
    }
  }


  /**
   * @see org.valabs.odisp.common.ObjectManager#loadPending()
   */
  public void loadPending() {
    // resources
    final Map<String, ResourceEntry> resourceList = new HashMap<String, ResourceEntry>(((ResourceManager5) dispatcher.getResourceManager()).getResources());
    for (String objectName : resourceList.keySet()) {
      if (!hasProviders(objectName)) {
        // ресурсы считаются провайдерами сервиса с собственным именем
        addProvider(objectName, objectName);
        log.finest("added resource provider " + objectName);
        hints.addNewHint(objectName);
      }
    }

    Iterator<String> commonIt;

    final Map<String, ObjectEntry> localObjects = new LinkedHashMap<String, ObjectEntry>(objects); // hints.getHintedOrder(objects);
    int statToLoadCount = objects.size();
    while (statToLoadCount != 0) {
      int loaded = 0;
      commonIt = localObjects.keySet().iterator();
      while (commonIt.hasNext()) {
        final String objectName = (String) commonIt.next();
        final ObjectEntry oe = localObjects.get(objectName);
        if (oe.isLoaded()) {
          commonIt.remove();
          continue;
        }
        log.finest("trying to load object " + objectName);
        // все условия зависимости удовлетворены
        if (provided.keySet().containsAll(oe.getDepends())) {
          if (oe.getWeakDepends().size() > 0) {
            int haveCounter = 0;
            int doneCounter = 0;
            for (String element : oe.getWeakDepends()) {
              if (objects.containsKey(element)) {
                haveCounter++;
              }
              if (provided.keySet().contains(element)) {
                doneCounter++;
              }
            }
            if (haveCounter > doneCounter) {
              // не все weak dependencies удовлетворены из имеющихся
              continue;
            }
          }
          // занесение в качестве провайдера для указанных сервисов
          for (String providing: oe.getProvides()) {
            log.finest("added as provider of " + providing);
            addProvider(providing, objectName);

          }

          // занесение в сервис RECIPIENT_ALL
          addProvider(Message.RECIPIENT_ALL, objectName);
          // если объект хочет получать все сообщения, то занести его в RECIPIENT_CATCHALL
          if (oe.getObject().getMatchAll()) {
            addProvider(Message.RECIPIENT_CATCHALL, objectName);
          }
          // пометка объекта как работающего
          oe.setLoaded(true);
          log.config(" ok. loaded = " + objectName);
          // восстановление данных из слепка если он был
          if (snapshot.hasSnapshot()) {
            log.config("Restoring state from snapshot for " + objectName);
            oe.getObject().importState(snapshot.getObjectSnapshot(objectName));
          }
          // официальное уведомление объекта о загрузке
          final Message m = dispatcher.getNewMessage();
          ODObjectLoadedMessage.setup(m, objectName, UUID.getNullUUID());
          m.setDestination(objectName);
          // список удовлетворённых weak dependencies.
          if (oe.getWeakDepends().size() > 0) {
            List<String> doneWeakDeps = new LinkedList<String>();
            for (String element : oe.getWeakDepends()) {
              if (objects.containsKey(element) || resourceList.containsKey(element)) {
                doneWeakDeps.add(element);
              }
            }
            ODObjectLoadedMessage.setWeakDependencies(m, doneWeakDeps);
          }
          oe.getObject().handleMessage0(m);
          // сброс накопившихся сообщений
          flushDefferedMessages(objectName);
          // запись в hints файл в случае необходимости
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
        
        final List<String> badObjects = new ArrayList<String>();
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
    Runtime.getRuntime().addShutdownHook(new Thread() {
      public void run() {
        log.info("System uptime: " + (Calendar.getInstance().getTimeInMillis() - startupTime));
      }
    });
  }

  /**
   * @see org.valabs.odisp.common.ObjectManager#removeProvider(java.lang.String, java.lang.String)
   */
  @SuppressWarnings("deprecation")
  public void removeProvider(String service, String objectName) {
    if (provided.containsKey(service)) {
      provided.get(service).remove(objectName);
      if (provided.get(service).size() == 0) {
        provided.remove(service);
      }
    }
  }
  
  /**
   * Проверка на существование провайдеров сервиса.
   * 
   * @param service название сервиса
   * @return флаг присутствия сервиса
   */
  private boolean hasProviders(final String service) {
    return provided.containsKey(service);
  }

  /**
   * Получить список объектов-провайдеров сервиса.
   * 
   * @param service имя сервиса
   * @return немодифицируемый thread-safe список объектов
   */
  private List<String> getProviders(final String service) {
    List<String> result = null;
    if (provided.containsKey(service)) {
      result = Collections.unmodifiableList(Collections.synchronizedList(provided.get(service)));
    }
    return result;
  }


  /**
   * Послать сообщение конкретному объекту.
   * 
   * @param objectName имя объекта
   * @param message сообщение
   */
  private void sendToObject(final String objectName, final Message message) {
    ObjectEntry oe = objects.get(objectName);
    if (oe == null) {
      return;
    }
    // исключить модификацию дескриптора состояние объекта
    synchronized (oe) {
      if (!oe.isLoaded()) {
        log.finest("deffered message " + message.getAction() + " for " + objectName);
        messages.addMessage(objectName, message);
        return;
      }
    }
    senderPool.execute(new Sender(oe.getObject(), message));
  }
  
  class Sender implements Runnable {
    private final Message toSend;
    private final ODObject objToSendTo;
    
    public Sender(ODObject _objToSendTo, Message _toSend) {
      objToSendTo = _objToSendTo;
      toSend = _toSend;
    }
    
    public void run() {
      objToSendTo.handleMessage0(toSend);
    }
  }

  /**
   * Посылка сообщения всем объектам менеджера.
   * 
   * @param message сообщение
   */
  public final void send(final Message message) {
    if (message == null || message.getAction() == null || message.getAction().length() == 0 || !message.isCorrect()) {
      return;
    }

    // рассылка реальным адресатам
    final Set<String> recipients = new HashSet<String>();
    if (getProviders(message.getDestination()) != null) {
      recipients.addAll(getProviders(message.getDestination()));
    }
    // тем, кто установил CATCH_ALL
    if (getProviders(Message.RECIPIENT_CATCHALL) != null) {
      recipients.addAll(getProviders(Message.RECIPIENT_CATCHALL));
    }
    
    if (recipients.size() > 0) {
      Message actualMessage;
      for (String objectName: recipients) {
//        actualMessage = message;
//        if (!(actualMessage instanceof COWStandardMessage)) {
//          actualMessage = new COWStandardMessage(message);
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
   * Сброс записанных сообщений при снятии блокировки с объекта.
   * 
   * @param objectName имя объекта
   */
  private void flushDefferedMessages(final String objectName) {
    if (objects.containsKey(objectName)) {
      for (Message m : messages.flush(objectName)) {
        sendToObject(objectName, m);
      }
    }
  }

  /**
   * @see org.valabs.odisp.common.ObjectManager#unloadObject(java.lang.String, int)
   */
  public void unloadObject(final String objectName, final int code) {
    if (objects.containsKey(objectName)) {
      final ObjectEntry oe = objects.get(objectName);
      final Set<String> provides = oe.getProvides();
      final Set<String> dependingObjs = new HashSet<String>();

      // поиск зависимых объектов:
      // среди всех объектов найти те, которые в списке зависимостей имеют данный объект
      for (ObjectEntry depoe : objects.values()) {
        final String depObjectName = depoe.getObject().getObjectName();
        final Set<String> depends = depoe.getDepends();
        for (String element : provides) {
          if (depends.contains(element) || depends.contains(WeakDependency.create(element))) {
            dependingObjs.add(depObjectName);
            break;
          }
        }
      }

      for (String element : provides) {
        removeProvider(element, objectName);
      }

      // выгрузка зависимых объектов
      for (String className : dependingObjs) {
        log.finest("removing " + objectName + "'s dependency " + className);
        unloadObject(className, code);
      }

      // сигнализация завершения работы
      oe.getObject().cleanUp(code);

      // сохранение слепка объекта, в случае если происходит перезапуск диспетчера
      if (code == ODShutdownMessage.SHUTDOWN_RESTART) {
        snapshot.addObjectSnapshot(objectName, oe.getObject().exportState());
      }

      // удаление объекта
      objects.remove(objectName);
      log.config("object " + objectName + " unloaded");
    }
  }
  
  ObjectManager5(Dispatcher disp) {
    dispatcher = disp;
    hints = new Hints();
  }
  
  class Hints {

    private final List<String> oldHints = new ArrayList<String>();
    private final List<String> newHints = new ArrayList<String>();

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

    public Map<String, ObjectEntry> getHintedOrder(final Map<String, ObjectEntry> currentObjects) {
      Map<String, ObjectEntry> localObjects;
      if (oldHints.size() > 0) {
        localObjects = new TreeMap<String, ObjectEntry>(new HintsOrderComparator(oldHints));
      } else {
        localObjects = new HashMap<String, ObjectEntry>();
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
      // вывести удачный порядок загрузки.
      StringBuffer msg = new StringBuffer("\n============================================\n");
      if (newHints.size() > 0) {
        msg.append("Result hints file:\n");
        /** @todo. HACK файл hints пишется в текущий каталог, что не есть гут. */
        try {
          final File hintsFile = new File("hints");
          hintsFile.createNewFile();
          final PrintStream out = new PrintStream(new FileOutputStream(hintsFile));
          for (String elt: newHints) {
            out.println(elt);
            msg.append("\t" + elt + "\n");
          }
        } catch (IOException e) {
          log.warning("Unable to write hints file.");
          dispatcher.getExceptionHandler().signalException(e);
        }
        msg.append("Total: ").append(newHints.size()).append("\n");
        msg.append("============================================\n");
        log.fine(msg.toString());
      }
    }

    class HintsOrderComparator implements Comparator<String> {
      private List<String> hints;
      
      public HintsOrderComparator(List<String> _hints) {
        hints = _hints;
      }
      
      public int compare(String _o1, String _o2) {
        String o1 = objects.get(_o1).getClassName();
        String o2 = objects.get(_o2).getClassName();
        if (o1.equals(o2)) { return 0; }
        if (o1.equals(DispatcherHandler5.class.getName())) { return 1; }
        if (o2.equals(DispatcherHandler5.class.getName())) { return -1; }
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
}
