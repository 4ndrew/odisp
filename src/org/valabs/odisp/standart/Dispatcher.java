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

/** Стандартный диспетчер ODISP.
 * Стандартный диспетчер реализует пересылку сообщений между объектами ядра
 * и управление ресурсными объектами.
 * @author Валентин А. Алексеев
 * @author (C) 2003, НПП "Новел-ИЛ"
 * @version $Id: Dispatcher.java,v 1.19 2003/11/30 16:32:57 valeks Exp $
 */
public class StandartDispatcher implements Dispatcher {
  /** Интерфейс к службе сообщений*/
  private static Logger log = Logger.getLogger("com.novel.odisp");
  /** Карта объектов управляемых диспетчером */
  private Map objects = new HashMap();
  /** Карта ресурсов диспетчера */
  private Map resources = new HashMap();
  /** Список реализованных возможностей диспетчера */
  private List provided = new ArrayList();
  /** Список требуемых возможностей */
  private List requested = new ArrayList();
  /** Хранилище отложенных сообщений*/
  private DefferedMessages messages = new DefferedMessages();
  /** Кол-во объектов системы */
  private int objCount = 0;
  /** Попытка подгрузки ожидающих классов при смене таблицы сервисов */
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
      int requested = oe.getDepends().length;
      for (int i = 0; i < oe.depends.length; i++) {
	if (provided.contains(oe.getDepends()[i])) {
	  requested--;
	} else {
	  log.finer("dependency not met: " + oe.getDepends()[i]);
	}
      }
      if (requested == 0) {
	oe.getObject().start();
	oe.setLoaded(true);
	for (int i = 0; i < oe.getProvides().length; i++) {
	  if (!provided.contains(oe.getProvides()[i])) {
	    log.fine("added provider of " + oe.getProvides()[i]);
	    provided.add(oe.getProvides()[i]);
	  }
	}
	log.config(" ok. loaded = " + objectName);
	Message m = getNewMessage("od_object_loaded", objectName, "stddispatcher", 0);
	oe.getObject().addMessage(m);
      }
    }
  }
  /** Динамическая загрузка ресурсных объектов
      @param className имя загружаемого класса
      @param mult количество загружаемых объектов
      @param param параметр загрузки
  */
  private void loadResource(String className, int mult, String param) {
    String logMessage = "loading resource " + className;
    for (int i = 0; i < mult; i++) {
      try {
	Resource r = (Resource) Class.forName(className).newInstance();
	ResourceEntry re = new ResourceEntry(className);
	re.setResource(r);
	log.fine("r instanceof ProxyResource " + (r instanceof ProxyResource) + " r.className:" + r.getClass().getName());
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
  /** Выгрузка ресурсного объекта
      @param roName имя ресурсного объекта
      @param code код выхода
  */
  private void unloadResource(String roName, int code) {
    if (resources.containsKey(roName)) {
      ResourceEntry res = (ResourceEntry) resources.get(roName);
      List dependingObjs = new ArrayList();
      Iterator it = objects.keySet().iterator();
      while (it.hasNext()) {
	String className = (String) it.next();
	String[] depends = ((ObjectEntry) objects.get(className)).depends;
	for (int i = 0; i < depends.length; i++) {
	  if (depends[i].equals(roName.substring(0, roName.length() - roName.indexOf(":")))
	      && !dependingObjs.contains(roName)) {
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
      resources.remove(roName);
    }
  }
  /** Динамическая загрузка объекта (с учётом зависимостей)
   * @param className имя загружаемого класса
   */
  private void loadObject(String className) {
    log.config("loading object " + className);
    try {
      Object params[] = new Object[1];
      params[0] = new Integer(objCount++);
      Class declParams[] = new Class[1];
      declParams[0] = params[0].getClass();
      ODObject load = (ODObject) Class.forName(className).getConstructor(declParams).newInstance(params);
      load.setDispatcher(this);
      synchronized (objects) {
	ObjectEntry oe = new ObjectEntry(className, false, load.getDepends(), load.getProviding());
	oe.object = load;
	oe.loaded = false;
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
  /** Принудительная выгрузка объекта и вызов сборщика
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
  private void unloadObject(String objectName, int code) {
    if (objects.containsKey(objectName)) {
      ObjectEntry oe = (ObjectEntry) objects.get(objectName);
      String[] provides = oe.provides;
      Iterator it = objects.keySet().iterator();
      List dependingObjs = new ArrayList();
      
      while (it.hasNext()) {
	String className = (String) it.next();
	String depends[] = ((ObjectEntry) objects.get(className)).depends;
	for (int i = 0; i < provides.length; i++) {
	  for (int j = 0; j < depends.length; j++) {
	    if (provides[i].equals(depends[j]) && !dependingObjs.contains(className)) {
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
      ODObject obj = oe.object;
      Message m = getNewMessage("od_cleanup", objectName, "stddispatcher", 0);
      m.addField(new Integer(code));
      sendMessage(m);
      obj.interrupt();
      objects.remove(objectName);
      log.config("\tobject " + objectName + " unloaded");
    }
  }
  /** Интерфейс для объектов ядра для отсылки сообщений.
   * Реализует multicast рассылку сообщений
   * @param message сообщение для отсылки
   */
  public void sendMessage(Message message) {
    if (message.getAction().length() == 0) {
      return;
    }
    synchronized (objects) {
      Iterator it = objects.keySet().iterator();
      while (it.hasNext()) {
	String className = (String) it.next();
	ObjectEntry oe = (ObjectEntry) objects.get(className);
	if (oe.blockedState || !oe.loaded) {
	  log.finer("deffered message for " + className + " (loaded=" + oe.loaded + ")");
	  messages.addMessage(className, message);
	  continue;
	}
	ODObject objToSendTo = oe.object;
	objToSendTo.addMessage(message);
	synchronized (objToSendTo) {
	  objToSendTo.notify();
	}
      }
    }
  }
  /** Интерфейс для объектов ядра для отсылки сообщений.
   * Реализует multicast рассылку нескольких сообщений
   * @param messageList список сообщений для отсылки
   */
  public void sendMessages(Message[] messageList) {
    if (messageList.length == 0) {
      return;
    }
    for (int i = 0; i < messageList.length; i++) {
      Message message = messageList[i];
      if (message.getAction().length() == 0) {
	continue;
      }
      synchronized (objects) {
	Iterator it = objects.keySet().iterator();
	while (it.hasNext()) {
	  String className = (String) it.next();
	  ObjectEntry oe = (ObjectEntry) objects.get(className);
	  if (oe.blockedState || !oe.loaded) {
	    log.finer("deffered message for " + className + " (loaded=" + oe.loaded + ")");
	    messages.addMessage(className, message);
	    continue;
	  }
	  ODObject objToSendTo = oe.object;
	  objToSendTo.addMessage(message);
	  synchronized (objToSendTo) {
	    objToSendTo.notify();
	  }
	}
      }
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
  public Message getNewMessage(String action, String destination, String origin, int inReplyTo) {
    return new StandartMessage(action, destination, origin, inReplyTo);
  }
  public Message getNewMessage() {
    return new StandartMessage();
  }
  /** Установка статуса блокировки объекта по ресурсу 
   * @param objName имя объекта
   * @param state новый уровень блокировки
   */
  private void setBlockedState(String objName, int state) {
    if (!objects.containsKey(objName)) {
      return;
    }
    ((ObjectEntry) objects.get(objName)).setBlockedState(state);
  }
  private int getBlockedState(String objName) {
    if (!objects.containsKey(objName)) {
      return 0;
    }
    return ((ObjectEntry) objects.get(objName)).getBlockedState();
  }
  /** Сброс записанных сообщений при снятии блокировки с объекта 
   * @param objectName имя объекта
   */
  private void flushDefferedMessages(String objectName) {
    if (!objects.containsKey(objectName)) {
		return;
    }
    ObjectEntry oe = (ObjectEntry) objects.get(objectName);
    if (!oe.isLoaded()) {
		return;
    }
    ODObject objectRef = oe.object;
    objectRef.addMessages(messages.flush(objectName));
    synchronized (objectRef) {
      objectRef.notify();
    }
    loadPending();
  }
  /** Конструктор загружающий первоначальный набор объектов
   * на основе списка
   * @param objs список объектов для загрузки
   */
  public StandartDispatcher(List objs) {
    log.info(toString() + " starting up...");
    StandartDispatcherHandler stdh = new StandartDispatcherHandler(new Integer(0));
    ObjectEntry oe = new ObjectEntry(stdh.getClass().getName(), false, stdh.getDepends(), stdh.getProviding());
    oe.object = stdh;
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
	    mult = new Integer(m.group(3).substring(0, m.group(3).length() - 1)).intValue();
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
  /** Выводит сообщение об ошибке в случае некорректных параметров */
  public static void usage() {
    log.severe("Usage: java com.novel.odisp.StandartDispatcher <file-with-list-of-ODobjects-to-load>");
    System.exit(0);
  }
  /** Точка входа в StandartDispatcher.
   * @param args по 0 должно содержать имя файла с перечислением классов, которые необходимо загрузить
   */
  public static void main(String args[]) {
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

  /** Запись об объекте в таблице объектов */
  private class ObjectEntry {
    /** Определяет загружен ли объект*/
    private boolean loaded;
    /** Проверка загрузки объекта 
     * @return состояние загрузки
     */
    public boolean isLoaded() {
      return loaded;
    }
    /** Установка признака загрузки 
     * @param newLoaded новое значение состояния
     */
    public void setLoaded(boolean newLoaded) {
      loaded = newLoaded;
    }
    
    /** Имя класса для объекта*/
    private String className;
    /** Вернуть имя класса для объекта
     * @return имя класса
     */
    public String getClassName() {
      return className;
    }
    /** Установить имя класса для объекта
     * @param newClassName новое имя класса
     */
    public void setClassName(String newClassName) {
      className = newClassName;
    }
    /** Состояние блокировки */
    private int blockedState;
    /** Вернуть состояние блокировки объекта
     * @return состояние блокировки
     */
    public int getBlockedState() {
      return blockedState;
    }

    public boolean isBlockedState() {
      return (blockedState > 0);
    }
    /** Установить состояние блокировки 
     * @param newBlockedState новое состояние блокировки
     */
    public void setBlockedState(int newBlockedState) {
      if (newBlockedState < 0) {
	blockedState = 0;
      } else {
	blockedState = newBlockedState;
      }
    }
    /** Ссылка на объект */
    private ODObject object;
    /** Вернуть ссылку на объект 
     * @return ссылка на объект
     */
    public ODObject getObject() {
      return object;
    }
    /** Список зависимостей*/
    private String[] depends;
    /** Вернуть список зависимостей 
     * @return список зависимостей
    */
    public String[] getDepends() {
      return depends;
    }
    /** Убрать определенную зависимость из списка
     * @param toRemove зависимость
     */
    public void removeDepend(String toRemove) {
      String[] newDeps = new String[depends.length - 1];
      for (int i = 0; i < depends.length; i++) {
	if (!depends[i].equals(toRemove)) {
	  newDeps[i] = new String(depends[i]);
	}
      }
      depends = newDeps;
    }
    /** Список сервисов */
    private String[] provides;
    /** Вернуть список сервисов 
     * @return список сервисов
     */
    public String[] getProvides() {
      return provides;
    }
    /** Конструктор класса 
     * @param cn имя класса
     * @param bs изначальное состояние блокировки
     * @param depends список зависимостей
     * @param provides список сервисов
     */
    public ObjectEntry(String cn, boolean bs, String[] depends, String[] provides) {
      className = cn;
      blockedState = bs;
      this.depends = depends;
      for (int i = 0; i < depends.length; i++) {
	log.fine("object " + cn + " depends on " + depends[i]);
      }
      this.provides = provides;
    }
  }
  /** Запись о ресурсе в таблице ресурсов */
  private class ResourceEntry {
    /** Состояние загрузки */
    private boolean loaded;
    /** Вернуть состояние загрузки
     * @return состояние загрузки
     */
    public boolean isLoaded() {
      return loaded;
    }
    /** Установить состояние загрузки
     * @param newLoaded новое значение состояния
     */
    public void setLoaded(boolean newLoaded) {
      this.loaded = newLoaded;
    }
    
    /** Имя класса ресурса */
    private String className;
    /** Ссылка на ресурс */
    private Resource resource;
    /** Вернуть ссылку на ресурс
     * @return ссылка на ресурс
     */
    public Resource getResource() {
      return resource;
    }
    /** Установить ссылку на ресурс 
     * @param newResource новое значение ссылки
     */
    public void setResource(Resource newResource) {
      resource = newResource;
    }
    /** Конструктор класса
     * @param cn имя класса ресурса
     */
    public ResourceEntry(String cn) {
      loaded = false;
      className = cn;
    }
  }
  /** Коллекция отложенных сообщений */
  private class DefferedMessages {
    /** Карта очередей сообщений */
    private Map queues = new HashMap();
    /** Добавление сообщения в определенную очередь 
     * @param objName имя объекта (очереди)
     * @param m сообщение для добавления
     */
    public void addMessage(String objName, Message m) {
      if (!queues.containsKey(objName)) {
	List messages = new ArrayList();
	messages.add(m);
	queues.put(objName, messages);
      } else {
	((List) queues.get(objName)).add(m);
      }
    }
    /** Возвращает список сохраненных для объекта сообщений
     * @param objectName имя объекта (очереди)
     * @return список сообщений из очереди
     */
    public List flush(String objectName) {
      if (queues.containsKey(objectName)) {
	List res = new ArrayList((List) queues.get(objectName));
	queues.remove(objectName);
	return res;
      } else {
	return new ArrayList();
      }
    }
  }
  /** Обработчик сообщений диспетчера */
  private class StandartDispatcherHandler extends CallbackODObject {
    Map resourceRequests = new HashMap();
    /** Имя объекта */
    private String name = "stddispatcher";
    /** Вернуть список сервисов
     * @return список сервисов
     */
    public String[] getProviding() {
      String res[] = new String[1];
      res[0] = "stddispatcher";
      return res;
    }
    /** Зарегистрировать обработчики сообщений */
    protected void registerHandlers() {
      addHandler("unload_object", new MessageHandler() {
	  public void messageReceived(Message msg) {
	    if (msg.getFieldsCount() != 1) {
	      return;
	    }
	    String name = (String) msg.getField(0);
	    unloadObject(name, 1);
	    objects.remove(name);
	  }
	});
      addHandler("load_object", new MessageHandler() {
	  public void messageReceived(Message msg) {
	    if (msg.getFieldsCount() != 1) {
	      return;
	    }
	    String name = (String) msg.getField(0);
	    loadObject(name);
	    loadPending();
	  }
	});
      addHandler("od_shutdown", new MessageHandler(){
	  public void messageReceived(Message msg) {
	    int exitCode = 0;
	    log.info(toString() + " shutting down...");
	    if (msg.getFieldsCount() == 1) {
	      exitCode = ((Integer) msg.getField(0)).intValue();
	    }
	    unloadObject("stddispatcher", exitCode);
	  }
	});
      addHandler("od_acquire", new MessageHandler() {
	  public void messageReceived(Message msg) {
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
		if (Pattern.matches(className + ":\\d+", curClassName) && ((ResourceEntry) resources.get(curClassName)).loaded) {
		  Message m = getNewMessage("resource_acquired", msg.getOrigin(), "stddispatcher", msg.getId());
		  m.addField(curClassName);
		  m.addField(((ResourceEntry) resources.get(curClassName)).resource);
		  resources.remove(curClassName);
		  sendMessage(m);
		  if (willBlockState) {
		    setBlockedState(msg.getOrigin(), getBlockedState(msg.getOrigin()) + 1);
		  }
		  found = true;
		  break;
		}
	      }
	      // allow concurent request for the resource
	      if (!found) {
		/* we maintin list of ODISP objects that require some specific resource
		 * each resource has corresponding queue of objects that wanted to acquire it
		 */
		if (!resourceRequests.containsKey(className)) {
		  resourceRequests.put(className, new ArrayList());
		}
		((List) resourceRequests.get(className)).add(msg.getOrigin() + (willBlockState ? "!" : ""));
	      }
	    }
	  }
	});
      addHandler("od_release", new MessageHandler() {
	  public void messageReceived(Message msg) {
	    if (msg.getFieldsCount() != 2) {
	      return;
	    }
	    String className = (String) msg.getField(0);
	    Resource res = (Resource) msg.getField(1);
	    // now we should check if there are any objects sitting in resourceRequest queues
	    if (resourceRequests.containsKey(className)) {
	      List resQueue = ((List) resourceRequests.get(className));
	      /* construct od_acquire message and send it to the first object that is on the queue
	       * object's name may contain ! modifier if acquiring should made blocking
	       */
	      String odObjectName = (String) resQueue.get(0);
	      if (odObjectName.endsWith("!")) {
		odObjectName = odObjectName.substring(0, odObjectName.length() - 1);
		setBlockingState(odObjectName, getBlockingState(odObjectName) + 1);
	      }
	      Message m = getNewMessage("resource_acquired", odObjectName, "stddispatcher", msg.getId());
	      m.addField(className);
	      m.addField(res);
	      sendMessage(m);
	    } else {
	      resources.put(className, new ResourceEntry(className.substring(0, className.length() - className.indexOf(":"))));
	    }
	    // decrease blocking state counter in case acquire was blocking
	    setBlockedState(msg.getOrigin(), getBlockedState(msg.getOrigin()) - 1); 
	  }
	});
      addHandler("od_list_objects", new MessageHandler() {
	  public void messageReceived(Message msg) {
	    Message m = getNewMessage("object_list", msg.getOrigin(), "stddispatcher", msg.getId());
	    m.addField(new ArrayList(objects.keySet()));
	    sendMessage(m);
	  }
	});
      addHandler("od_list_resources", new MessageHandler() {
	  public void messageReceived(Message msg) {
	    Message m = getNewMessage("resource_list", msg.getOrigin(), "stddispatcher", msg.getId());
	    m.addField(new ArrayList(resources.keySet()));
	    sendMessage(m);
	  }
	});
      addHandler("od_remove_dep", new MessageHandler() {
	  public void messageReceived(Message msg) {
	    if (msg.getFieldsCount() != 1) {
	      return;
	    }
	    ObjectEntry oe = (ObjectEntry) objects.get(msg.getOrigin());
	    oe.removeDepend((String) msg.getField(0));
	  }
	});
    }
    /** Точка выхода из объекта 
     * @param type признак выхода
     * @return код возврата
     */
    public int cleanUp(int type) {
      return 0;
    }
    /** Конструктор объекта 
     * @param id порядковый номер объекта
     */
    public StandartDispatcherHandler(Integer id) {
      super("stddispatcher");
    }
  }
}
