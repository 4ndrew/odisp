package com.novel.odisp;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.novel.odisp.common.Dispatcher;
import com.novel.odisp.common.Message;
import com.novel.odisp.common.ODObject;
import com.novel.odisp.common.ObjectManager;
import com.novel.stdmsg.ODCleanupMessage;
import com.novel.stdmsg.ODObjectLoadedMessage;

/** Менеджер объектов ODISP.
 * @author (C) 2004 <a href="mailto:valeks@valeks.novel.local">Valentin A. Alekseev</a>
 * @version $Id: ObjectManager.java,v 1.18 2004/05/11 13:10:49 valeks Exp $
 */

public class StandartObjectManager implements ObjectManager {
  /** Диспетчер объектов. */
  private Dispatcher dispatcher;
  /** Хранилище отложенных сообщений. */
  private DefferedMessages messages = new DefferedMessages();
  /** Список объектов. */
  private Map objects = new HashMap();
  /** Журнал. */
  private Logger log = Logger.getLogger("com.novel.odisp.StandartObjectManager");
  /** Список сервисов менеджера. */
  private Map provided = new HashMap();
  /** Пул нитей отсылки. */
  private List senderPool = new ArrayList();
  /** Максимальное количество нитей создаваемых для отсылки изначально. */
  public static final int SENDER_POOL_SIZE = 5;
  /** Общее число объектов. */
  private int objCount = 0;

  /** Добавление объекта как провайдера конкретного сервиса.
   * @param service название сервиса
   * @param objectName название объекта
   */
  public void addProvider(final String service, final String objectName) {
    if (!provided.containsKey(service)) {
      provided.put(service, new ArrayList());
    }
    ((List) provided.get(service)).add(objectName);
  }

  /** Удаление провайдера конкретного сервиса.
   * В случае если у сервиса не остается ни одного провайдера -- он автоматически будет удален.
   * @param service название сервиса
   * @param objectName название объекта
   */
  public void removeProvider(final String service, final String objectName) {
    if (provided.containsKey(service)) {
      ((List) provided.get(service)).remove(objectName);
      if (((List) provided.get(service)).size() == 0) {
	provided.remove(service);
      }
    }
  }

  /** Проверка на существование провайдеров сервиса.
   * @param service название сервиса
   * @return флаг присутствия сервиса
   */
  private boolean hasProviders(final String service) {
    return provided.containsKey(service);
  }

  /** Получить список объектов-провайдеров сервиса.
   * @param service имя сервиса
   * @return немодифицируемый thread-safe список объектов
   */
  private List getProviders(final String service) {
    if (provided.containsKey(service)) {
      return Collections.unmodifiableList(Collections.synchronizedList((List) provided.get(service)));
    } else {
      return null;
    }
  }

  /** Получить список сервисов диспетчера.
   * @return немодифицируемый список сервисов
   */
  public List getProviding() {
    return new ArrayList(Collections.unmodifiableSet(provided.keySet()));
  }

  /** Попытка подгрузки объектов в следствии изменения списка сервисов менеджера. */
  public final void loadPending() {
    // resources
    Map resourceList = new HashMap(dispatcher.getResourceManager().getResources());
    Iterator it = resourceList.keySet().iterator();
    while (it.hasNext()) {
      String objectName = (String) it.next();
      log.fine("added resource provider " + objectName);
      if (!hasProviders(objectName)) {
	// ресурсы считаются провайдерами сервиса с собственным именем
	addProvider(objectName, objectName);
      }
    }
    int loaded = 0;
    synchronized (objects) {
      it = objects.keySet().iterator();
      while (it.hasNext()) {
	String objectName = (String) it.next();
	ObjectEntry oe = (ObjectEntry) objects.get(objectName);
	if (oe.isLoaded()) {
	  continue;
	}
	log.config("trying to load object " + objectName);
	int numRequested = oe.getDepends().length;
	for (int i = 0; i < oe.getDepends().length; i++) {
	  if (hasProviders(oe.getDepends()[i])) {
	    numRequested--;
	  } else {
	    log.finer("dependency not met: " + oe.getDepends()[i]);
	  }
	}
	if (numRequested == 0) {
	  oe.setLoaded(true);
          flushDefferedMessages(oe.getObject().getObjectName());
	  for (int i = 0; i < oe.getProvides().length; i++) {
	    if (!hasProviders(oe.getProvides()[i])) {
	      log.fine("added as provider of " + oe.getProvides()[i]);
	      addProvider(oe.getProvides()[i], oe.getObject().getObjectName());
	    }
	  }
	  log.config(" ok. loaded = " + objectName);
	  Message m = new ODObjectLoadedMessage(objectName);
	  oe.getObject().addMessage(m);
	  loaded++;
	}
      }
    }
    if (loaded > 0) {
      loadPending();
    }
  }

  /** Динамическая загрузка объекта (с учётом зависимостей).
   * @param cName имя загружаемого класса
   * @param configuration список параметров загрузки
   */
  public final void loadObject(final String cName, final Map configuration) {
    log.config("loading object " + cName);
    try {
      Object[] params = new Object[1];
      params[0] = new Integer(objCount++);
      Class[] dParams = new Class[1];
      dParams[0] = params[0].getClass();
      ODObject load =
	(ODObject) Class.forName(cName).getConstructor(dParams).newInstance(params);
      load.setDispatcher(dispatcher);
      load.setConfiguration(configuration);
      synchronized (objects) {
	ObjectEntry oe =
	  new ObjectEntry(cName, 0, load.getDepends(), load.getProviding());
	oe.setObject(load);
	oe.setLoaded(false);
	objects.put(load.getObjectName(), oe);
      }
    } catch (InvocationTargetException e) {
      log.warning(" failed: " + e);
    } catch (NoSuchMethodException e) {
      log.warning(" failed: " + e);
    } catch (ClassNotFoundException e) {
      log.warning(" failed: " + e);
    } catch (InstantiationException e) {
      log.warning(" failed: " + e);
    } catch (IllegalAccessException e) {
      log.warning(" failed: " + e);
    } catch (IllegalArgumentException e) {
      log.warning(" failed: " + e);
    }
  }

  /** Принудительная выгрузка объекта и вызов сборщика.
   * мусора, так же учитываются зависимости:
   * <ul>
   * <li> Составление списка зависимых объектов
   * <li> Удаление зависимых объектов
   * <li> Удаление самого объекта
   * </ul>
   * @param objectName внутреннее имя объекта для удаления.
   * @param code код выхода (при code != 0 зависимые объекты
   * не удаляются).
   */
  public synchronized final void unloadObject(final String objectName, final int code) {
    if (objects.containsKey(objectName)) {
      ObjectEntry oe = (ObjectEntry) objects.get(objectName);
      String[] provides = oe.getProvides();
      Iterator it = objects.keySet().iterator();
      List dependingObjs = new ArrayList();

      while (it.hasNext()) {
	String className = (String) it.next();
	String[] depends = ((ObjectEntry) objects.get(className)).getDepends();
	for (int i = 0; i < provides.length; i++) {
	  for (int j = 0; j < depends.length; j++) {
	    if (provides[i].equals(depends[j])
		&& !dependingObjs.contains(className)) {
	      dependingObjs.add(className);
	    }
	  }
	  // не забываем удалить объект из списков провайдеров
	  removeProvider(provides[i], objectName);
	}
      }
      if (code == 0) {
	it = dependingObjs.iterator();
	while (it.hasNext()) {
	  String className = (String) it.next();
	  if (objects.containsKey(className)) {
	    log.fine("removing " + objectName + "'s dependency " + className);
	    unloadObject(className, code);
	  }
	}
      }
      ODObject obj = oe.getObject();
      ODCleanupMessage m = new ODCleanupMessage(objectName, 0);
      m.setReason(code);
      dispatcher.send(m);
      objects.remove(objectName);
      log.config("\tobject " + objectName + " unloaded");
    }
  }

  /** Доступ к списку объектов. 
   * @return список объектов
   */
  public final Map getObjects() {
    return objects;
  }

  /** Констурктор менеджера.
   * @param newDispatcher диспетчер для которого производится управление ресурсами
   */
  public StandartObjectManager(final Dispatcher newDispatcher) {
    dispatcher = newDispatcher;
    for (int i = 0; i < SENDER_POOL_SIZE; i++) {
      senderPool.add(new Sender());
    }
  }

  /** Послать сообщение конкретному объекту.
   * @param objectName имя объекта
   * @param message сообщение
   */
  private void sendToObject(final String objectName, final Message message) {
    ObjectEntry oe = null;
    // исключить модификацию списка дескрипторов объектов
    synchronized (objects) {
      oe = (ObjectEntry) objects.get(objectName);
    }
    if(oe == null) {
      return;
    }
    ODObject objToSendTo = null;
    // исключить модификацию дескриптора состояние объекта
    synchronized (oe) {
      if (oe.isBlockedState() || !oe.isLoaded()) {
	log.finest("deffered message " + message.getAction() + " for " + objectName
		   + " (loaded=" + oe.isLoaded() + ")");
	messages.addMessage(objectName, message);
	return;
      }
      objToSendTo = oe.getObject();
    }
    /* Выбор первой наименее загруженной нити отсылки.
       Возможно в дальнейшем потребуется какой ни будь адаптивный алгоритм, который будет
       расширять размер пула нитей в зависимости от нагрузки системы.
     */
    Sender victim = null;
    int leastLoad = Integer.MAX_VALUE;
    int avgLoad = 0;
    for (int i = 0; i < senderPool.size(); i++) {
      Sender tmp = (Sender) senderPool.get(i);
      avgLoad += tmp.getCounter();
      if (tmp.getCounter() < leastLoad) {
	leastLoad = tmp.getCounter();
	victim = tmp;
      }
    }
    victim.send(message, objToSendTo);
    /*
      Адаптивный алгоритм работы:
      при превышении лимита в 10-15 сообщений для минимума производится запуск пары дополнительных
      нитей с перераспределением нагрузки между ними. Когда средняя нагрузка
      опускается ниже 5 - убираем по 1 нити.
     */
    avgLoad = avgLoad / senderPool.size();
    if (leastLoad > 15) {
      senderPool.add(new Sender());
      senderPool.add(new Sender());
      log.fine("Least load exceed 15 - throttling.");
    } else if (avgLoad < 5 && senderPool.size() > SENDER_POOL_SIZE) {
      Sender s = (Sender) senderPool.get(0);
      s.quit();
      senderPool.remove(0);
      s = null;
      log.fine("Average load less than 5 - slowing down.");
    }
  }

  /** Посылка сообщения всем объектам менеджера.
   * @param message сообщение
   */
  public final void send(Message message) {
    if (message == null
	|| message.getAction().length() == 0
	|| !message.isCorrect()) {
      return;
    }
    // Получатели, ибо использовать глобольный итератор без глобальной
    // блокировки объекта по меньшей мере - наивно ;-))) 
    List recipients = null;
    // Посылка производится сервису.
    boolean serviceMatch = true;
    // в случае если получатель смахивает на имя сервиса
    // -- разослать только провайдерам, а не всем подряд
    if (hasProviders(message.getDestination())) {
      List providers = new ArrayList(getProviders(message.getDestination()));
      if (providers != null) {
	recipients = providers;
      }
    }
    if (recipients == null){
      serviceMatch = false;
      recipients = new ArrayList();
      synchronized (objects) {
        Iterator it = objects.keySet().iterator();
        while (it.hasNext()) {
          recipients.add(it.next());
        }
      }
    }
    Iterator it = recipients.iterator();
    while (it.hasNext()) {
      String objectName = (String) it.next();
      if (serviceMatch) {
        message.setDestination(objectName);
      }
      sendToObject(objectName, message);
    }
  }

  /** Установка статуса блокировки объекта по ресурсу.
   * @param objName имя объекта
   * @param state новый уровень блокировки
   */
  public final void setBlockedState(final String objName, final int state) {
    if (!objects.containsKey(objName)) {
      return;
    }
    ObjectEntry oe = (ObjectEntry) objects.get(objName);
    oe.setBlockedState(state);
    if (oe.getBlockedState() == 0) {
      flushDefferedMessages(objName);
    }
  }
  /** Получить статус блокировки объекта.
   * @param objName имя объекта
   * @return значения уровня блокировки
   */
  public final int getBlockedState(final String objName) {
    assert !objects.containsKey(objName);
    return ((ObjectEntry) objects.get(objName)).getBlockedState();
  }
  /** Сброс записанных сообщений при снятии блокировки с объекта.
   * @param objectName имя объекта
   */
  private void flushDefferedMessages(final String objectName) {
    if (!objects.containsKey(objectName)) {
		return;
    }
    ObjectEntry oe = (ObjectEntry) objects.get(objectName);
    if (!oe.isLoaded()) {
		return;
    }
    ODObject objectRef = oe.getObject();
    objectRef.addMessages(messages.flush(objectName));
    synchronized (objectRef) {
      objectRef.notify();
    }
    loadPending();
  }
} // StandartObjectManager
