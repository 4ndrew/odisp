package com.novel.odisp;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.lang.reflect.InvocationTargetException;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.logging.Logger;

import com.novel.odisp.common.Message;
import com.novel.odisp.common.ODObject;
import com.novel.odisp.common.Resource;
import com.novel.odisp.common.ProxyResource;
import com.novel.odisp.common.Dispatcher;
import com.novel.odisp.common.CallbackODObject; // для объекта stddispatcher
import com.novel.odisp.common.MessageHandler; // --''--
import com.novel.stdmsg.*;

/** Стандартный диспетчер ODISP.
 * Стандартный диспетчер реализует пересылку сообщений между объектами ядра
 * и управление ресурсными объектами.
 * @author Валентин А. Алексеев
 * @author (C) 2003, НПП "Новел-ИЛ"
 * @version $Id: Dispatcher.java,v 1.26 2004/02/13 00:11:29 valeks Exp $
 */
public class StandartDispatcher implements Dispatcher {
  /** Интерфейс к службе сообщений. */
  private static Logger log = Logger.getLogger("com.novel.odisp");
  /** Карта объектов управляемых диспетчером. */
  private Map objects = new HashMap();
  /** Карта ресурсов диспетчера. */
  private Map resources = new HashMap();
  /** Список реализованных возможностей диспетчера. */
  private List provided = new ArrayList();
  /** Список требуемых возможностей. */
  private List requested = new ArrayList();
  /** Хранилище отложенных сообщений. */
  private DefferedMessages messages = new DefferedMessages();
  /** Кол-во объектов системы. */
  private int objCount = 0;
  /** Попытка подгрузки ожидающих классов при смене таблицы сервисов. */
  private void loadPending() {
    // resources
    Iterator it = resources.keySet().iterator();
    while (it.hasNext()) {
      String objectName = (String) it.next();
      ResourceEntry re = (ResourceEntry) resources.get(objectName);
      if (re.isLoaded()) {
	continue;
      }
      re.setLoaded(true);
      log.fine("added resource provider " + objectName);
      provided.add(objectName.substring(0, objectName.lastIndexOf(":")));
    }
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
	if (provided.contains(oe.getDepends()[i])) {
	  numRequested--;
	} else {
	  log.finer("dependency not met: " + oe.getDepends()[i]);
	}
      }
      if (numRequested == 0) {
	oe.getObject().start();
	oe.setLoaded(true);
	for (int i = 0; i < oe.getProvides().length; i++) {
	  if (!provided.contains(oe.getProvides()[i])) {
	    log.fine("added provider of " + oe.getProvides()[i]);
	    provided.add(oe.getProvides()[i]);
	  }
	}
	log.config(" ok. loaded = " + objectName);
	Message m = new ODObjectLoadedMessage(objectName);
	oe.getObject().addMessage(m);
      }
    }
  }
  /** Динамическая загрузка ресурсных объектов.
      @param className имя загружаемого класса
      @param mult количество загружаемых объектов
      @param param параметр загрузки
  */
  private void loadResource(final String className, final int mult, final String param) {
    String logMessage = "loading resource " + className;
    for (int i = 0; i < mult; i++) {
      try {
	Resource r = (Resource) Class.forName(className).newInstance();
	ResourceEntry re = new ResourceEntry(className);
	re.setResource(r);
	log.fine("r instanceof ProxyResource " + (r instanceof ProxyResource)
		 + " r.className:" + r.getClass().getName());
	if (r instanceof ProxyResource) {
	  ((ProxyResource) r).setResource(param);
	  resources.put(param + ":" + i, re);
	} else {
	  resources.put(className + ":" + i, re);
	}
	logMessage += "+";
      } catch (ClassNotFoundException e) {
	log.warning(" failed: " + e);
      } catch (InstantiationException e) {
	log.warning(" failed: " + e);
      } catch (IllegalAccessException e) {
	log.warning(" failed: " + e);
      }
    }
    logMessage += " ok.";
    log.config(logMessage);
  }
  /** Выгрузка ресурсного объекта.
      @param name имя ресурсного объекта
      @param code код выхода
  */
  private void unloadResource(final String name, final int code) {
    if (resources.containsKey(name)) {
      ResourceEntry res = (ResourceEntry) resources.get(name);
      List dependingObjs = new ArrayList();
      Iterator it = objects.keySet().iterator();
      while (it.hasNext()) {
	String className = (String) it.next();
	String[] depends = ((ObjectEntry) objects.get(className)).getDepends();
	for (int i = 0; i < depends.length; i++) {
	  if (depends[i].equals(name.substring(0, name.length()
					       - name.indexOf(":")))
	      && !dependingObjs.contains(name)) {
	    dependingObjs.add(className);
	  }
	}
      }
      if (code == 0) {
	it = dependingObjs.iterator();
	while (it.hasNext()) {
	  unloadObject((String) it.next(), code);
	}
      }
      res.getResource().cleanUp(code);
      resources.remove(name);
    }
  }
  /** Динамическая загрузка объекта (с учётом зависимостей).
   * @param cName имя загружаемого класса
   */
  private void loadObject(final String cName) {
    log.config("loading object " + cName);
    try {
      Object[] params = new Object[1];
      params[0] = new Integer(objCount++);
      Class[] dParams = new Class[1];
      dParams[0] = params[0].getClass();
      ODObject load =
	(ODObject) Class.forName(cName).getConstructor(dParams).newInstance(params);
      load.setDispatcher(this);
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
  private void unloadObject(final String objectName, final int code) {
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
      sendMessage(m);
      obj.interrupt();
      objects.remove(objectName);
      log.config("\tobject " + objectName + " unloaded");
    }
  }
  /** Интерфейс для объектов ядра для отсылки сообщений.
   * Реализует multicast рассылку сообщений
   * @param message сообщение для отсылки
   * @deprecated необходимо использовать send(Message)
   */
  public final void sendMessage(final Message message) {
    send(message);
  }
  /** Интерфейс для объектов ядра для отсылки сообщений.
   * @param messageList список сообщений для отсылки
   * @deprecated необходимо использовать send(Message[])
   */
  public final void sendMessages(final Message[] messageList) {
    send(messageList);
  }

  public final void send(Message message) {
    if (message == null || 
	message.getAction().length() == 0 || 
	!message.isCorrect()) {
      return;
    }
    synchronized (objects) {
      Iterator it = objects.keySet().iterator();
      while (it.hasNext()) {
	String className = (String) it.next();
	ObjectEntry oe = (ObjectEntry) objects.get(className);
	if (oe.isBlockedState() || !oe.isLoaded()) {
	  log.finer("deffered message for " + className
		    + " (loaded=" + oe.isLoaded() + ")");
	  messages.addMessage(className, message);
	  continue;
	}
	ODObject objToSendTo = oe.getObject();
	objToSendTo.addMessage(message);
	synchronized (objToSendTo) {
	  objToSendTo.notify();
	}
      }
    }
  }

  public final void send(Message[] messageList) {
    if (messageList == null || messageList.length == 0) {
      return;
    }
    for (int i = 0; i < messageList.length; i++) {
      Message message = messageList[i];
      send(message);
    }
  }

  public final void send(List messageList) {
    Iterator it = messageList.iterator();
    while (it.hasNext()) {
      send((Message) it.next());
    }
  }

  /** Интерфейс создания нового сообщения для сокрытия конкретной реализации
   * сообщений.
   * @param action действие которое несет сообщение
   * @param destination адресат сообщения
   * @param origin отправитель сообщения
   * @param inReplyTo идентификатор сообщения на которое производится ответ
   * @return Message созданное сообщение
   */
  public final Message getNewMessage(final String action,
				     final String destination,
				     final String origin,
				     final int inReplyTo) {
    return new StandartMessage(action, destination, origin, inReplyTo);
  }

  /** Создать новое пустое сообщение.
   * @return ссылка на сообщение
   */
  public final Message getNewMessage() {
    return new StandartMessage();
  }
  /** Установка статуса блокировки объекта по ресурсу.
   * @param objName имя объекта
   * @param state новый уровень блокировки
   */
  private void setBlockedState(final String objName, final int state) {
    if (!objects.containsKey(objName)) {
      return;
    }
    ((ObjectEntry) objects.get(objName)).setBlockedState(state);
  }
  /** Получить статус блокировки объекта.
   * @param objName имя объекта
   * @return значения уровня блокировки
   */
  private int getBlockedState(final String objName) {
    if (!objects.containsKey(objName)) {
      return 0;
    }
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
  /** Конструктор загружающий первоначальный набор объектов.
   * на основе списка
   * @param objs список объектов для загрузки
   */
  public StandartDispatcher(final List objs) {
    log.info(toString() + " starting up...");
    StandartDispatcherHandler stdh
      = new StandartDispatcherHandler(new Integer(0));
    ObjectEntry oe
      = new ObjectEntry(stdh.getClass().getName(), 0, stdh.getDepends(), stdh.getProviding());
    oe.setObject(stdh);
    objects.put("stddispatcher", oe);
    loadPending();
    Iterator it = objs.iterator();
    Pattern p = Pattern.compile("(o:|(r:)(\\d+:)?)([^:]+)(:(.*))?");
    //                            type    mult     class  param
    while (it.hasNext()) {
      int mult = 1;
      String param = "";
      String className = (String) it.next();
      Matcher m = p.matcher(className);
      m.find();
      String parsedLine = "";
      for (int i = 0; i != m.groupCount(); i++) {
	parsedLine += i + "='" + m.group(i) + "' ";
      }
      log.finest(parsedLine);
      if (m.groupCount() == 6) {
	if (m.group(1).equals("o:")) {
			loadObject(m.group(4));
	}
	if (m.group(1).startsWith("r:")) {
	  if (m.group(3) != null) {
	    int len = m.group(3).length() - 1;
	    mult = new Integer(m.group(3).substring(0, len)).intValue();
	  }
	  if (m.group(5) != null) {
                            param = m.group(5).substring(1);
	  }
	  loadResource(m.group(4), mult, param);
	}
	loadPending();
      }
    }
  }
  /** Выводит сообщение об ошибке в случае некорректных параметров. */
  public static void usage() {
    log.severe("Usage: java com.novel.odisp.StandartDispatcher <config>");
    System.exit(0);
  }
  /** Точка входа в StandartDispatcher.
   * @param args по 0 должно содержать имя файла с перечислением классов,
   * которые необходимо загрузить
   */
  public static void main(final String[] args) {
    log.setLevel(java.util.logging.Level.ALL);
    if (args.length != 1) {
		usage();
    } else {
      try {
	BufferedReader cfg = new BufferedReader(new FileReader(args[0]));
	List objs = new ArrayList();
	String s;
	while ((s = cfg.readLine()) != null) {
	  if (!s.startsWith("#")) {
	    objs.add(s);
	  }
	}
	new StandartDispatcher(objs);
      } catch (FileNotFoundException e) {
	log.severe("[e] configuration file " + args[0] + " not found.");
      } catch (IOException e) {
	log.severe("[e] unable to read configuration file.");
      }
    }
  }

  /** Обработчик сообщений диспетчера. */
  private class StandartDispatcherHandler extends CallbackODObject {
    private int msgId = 0;
    /** Карта запросов к ресурсам. */
    private Map resourceRequests = new HashMap();
    /** Имя объекта. */
    private String name = "stddispatcher";
    /** Вернуть список сервисов.
     * @return список сервисов
     */
    public final String[] getProviding() {
      String[] res = {
	"stddispatcher"
      };
      return res;
    }
    /** Вернуть список зависимостей.
     * @return список зависимостей
     */
    public final String[] getDepends() {
      String[] res = {};
      return res;
    }
    /** Зарегистрировать обработчики сообщений. */
    protected final void registerHandlers() {
      addHandler("unload_object", new MessageHandler() {
	  public final void messageReceived(final Message msg) {
	    if (msg.getFieldsCount() != 1) {
	      return;
	    }
	    String objname = (String) msg.getField(0);
	    unloadObject(objname, 1);
	    objects.remove(objname);
	  }
	});
      addHandler("load_object", new MessageHandler() {
	  public final void messageReceived(final Message msg) {
	    if (msg.getFieldsCount() != 1) {
	      return;
	    }
	    String objname = (String) msg.getField(0);
	    loadObject(objname);
	    loadPending();
	  }
	});
      addHandler("od_shutdown", new MessageHandler() {
	  public final void messageReceived(final Message msg) {
	    int exitCode = 0;
	    log.info(toString() + " shutting down...");
	    if (msg.getFieldsCount() == 1) {
	      exitCode = ((Integer) msg.getField(0)).intValue();
	    }
	    unloadObject("stddispatcher", exitCode);
	  }
	});
      addHandler("od_acquire", new MessageHandler() {
	  public final void messageReceived(final Message msg) {
	    if (msg.getFieldsCount() > 0) {
	      String className = (String) msg.getField(0);
	      boolean willBlockState = false;
	      if (msg.getFieldsCount() == 2) {
		willBlockState = ((Boolean) msg.getField(1)).booleanValue();
	      }
	      Iterator it = resources.keySet().iterator();
	      boolean found = false;
	      while (it.hasNext()) { // first hit
		String curClassName = (String) it.next();
		if (Pattern.matches(className + ":\\d+", curClassName)
		    && ((ResourceEntry) resources.get(curClassName)).isLoaded()) {
		  ODResourceAcquiredMessage m
		    = new ODResourceAcquiredMessage(msg.getOrigin(), msg.getId());
		  m.setResourceName(curClassName);
		  m.setResource(((ResourceEntry) resources.get(curClassName)).getResource());
		  send(m);
		  logger.fine((msgId++) + "" + msg.getOrigin() + " acquired " + curClassName);
		  if (willBlockState) {
		    setBlockedState(msg.getOrigin(), getBlockedState(msg.getOrigin()) + 1);
		  }
		  resources.remove(curClassName);
		  found = true;
		  break;
		}
	      }
	      // allow concurent request for the resource
	      if (!found) {
		/* we maintin list of ODISP objects that require some
		 * specific resource each resource has corresponding
		 * queue of objects that wanted to acquire it
		 */
		if (!resourceRequests.containsKey(className)) {
		  resourceRequests.put(className, new ArrayList());
		}
		String wb = "";
		if (willBlockState) {
		  wb = "!";
		}
		((List) resourceRequests.get(className)).add(msg.getOrigin() + wb);
	      }
	    }
	  }
	});
      addHandler("od_release", new MessageHandler() {
	  public final void messageReceived(final Message msg) {
	    if (msg.getFieldsCount() != 2) {
	      return;
	    }
	    String className = (String) msg.getField(0);
	    Resource res = (Resource) msg.getField(1);
	    logger.fine((msgId++) + "" + msg.getOrigin() + " released " + className);
	    // now we should check if there are any objects
	    // sitting in resourceRequest queues
	    if (resourceRequests.containsKey(className)) {
	      List resQueue = ((List) resourceRequests.get(className));
	      /* construct od_acquire message and send it to the first
	       * object that is on the queue object's name may
	       * contain ! modifier if acquiring should made blocking
	       */
	      String odObjectName = (String) resQueue.get(0);
	      if (odObjectName.endsWith("!")) {
		odObjectName = odObjectName.substring(0, odObjectName.length() - 1);
		setBlockedState(odObjectName, getBlockedState(odObjectName) + 1);
	      }
	      ODResourceAcquiredMessage m = new ODResourceAcquiredMessage(odObjectName, msg.getId());
	      m.setResourceName(className);
	      m.setResource(res);
	      send(m);
	      logger.fine((msgId++) + "" + msg.getOrigin() + " acquired " + className);
	    } else {
	      resources.put(className, new ResourceEntry(className.substring(0, className.length() - className.indexOf(":"))));
	    }
	    // decrease blocking state counter in case acquire was blocking
	    setBlockedState(msg.getOrigin(), getBlockedState(msg.getOrigin()) - 1);
	  }
	});
      addHandler("od_list_objects", new MessageHandler() {
	  public final void messageReceived(final Message msg) {
	    Message m = getNewMessage("object_list", msg.getOrigin(), "stddispatcher", msg.getId());
	    m.addField(new ArrayList(objects.keySet()));
	    sendMessage(m);
	  }
	});
      addHandler("od_list_resources", new MessageHandler() {
	  public final void messageReceived(final Message msg) {
	    Message m = getNewMessage("resource_list", msg.getOrigin(), "stddispatcher", msg.getId());
	    m.addField(new ArrayList(resources.keySet()));
	    sendMessage(m);
	  }
	});
      addHandler("od_remove_dep", new MessageHandler() {
	  public final void messageReceived(final Message msg) {
	    if (msg.getFieldsCount() != 1) {
	      return;
	    }
	    ObjectEntry oe = (ObjectEntry) objects.get(msg.getOrigin());
	    oe.removeDepend((String) msg.getField(0));
	  }
	});
    }
    /** Точка выхода из объекта.
     * @param type признак выхода
     * @return код возврата
     */
    public final int cleanUp(final int type) {
      return 0;
    }
    /** Конструктор объекта.
     * @param id порядковый номер объекта
     */
    public StandartDispatcherHandler(final Integer id) {
      super("stddispatcher");
    }
  } // StandartDispatcherHandler
} // StandartDispatcher
