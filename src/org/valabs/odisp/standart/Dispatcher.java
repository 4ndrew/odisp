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
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.logging.Logger;

import com.novel.odisp.common.Message;
import com.novel.odisp.common.ODObject;
import com.novel.odisp.common.ResourceManager;
import com.novel.odisp.common.ObjectManager;
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
 * @version $Id: Dispatcher.java,v 1.27 2004/02/13 12:11:43 valeks Exp $
 */
public class StandartDispatcher implements Dispatcher {
  /** Журнал. */
  private static Logger log = Logger.getLogger("com.novel.odisp");
  /** Менеджер ресурсов. */
  private ResourceManager rman = new StandartResourceManager(this);
  /** Менеджер объектов. */
  private ObjectManager oman = new StandartObjectManager(this);
  /** Кол-во объектов системы. */
  private int objCount = 0;

  /** Доступ к менеджеру объектов. */
  public ObjectManager getObjectManager() {
    return oman;
  }

  /** Доступ к менеджеру ресурсов. */
  public ResourceManager getResourceManager() {
    return rman;
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

  /** Интерфейс для объектов ядра для отсылки сообщений.
   * @param message сообщение для посылки
   */
  public final void send(Message message) {
    oman.send(message);
  }

  /** Интерфейс для объектов ядра для отсылки сообщений.
   * @param messageList список сообщений для посылки
   */
  public final void send(Message[] messageList) {
    if (messageList == null || messageList.length == 0) {
      return;
    }
    for (int i = 0; i < messageList.length; i++) {
      Message message = messageList[i];
      send(message);
    }
  }

  /** Интерфейс для объектов ядра для отсылки сообщений.
   * @param messageList список сообщений для посылки
   */
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
    oman.getObjects().put("stddispatcher", oe);
    oman.loadPending();
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
	  oman.loadObject(m.group(4));
	}
	if (m.group(1).startsWith("r:")) {
	  if (m.group(3) != null) {
	    int len = m.group(3).length() - 1;
	    mult = new Integer(m.group(3).substring(0, len)).intValue();
	  }
	  if (m.group(5) != null) {
	    param = m.group(5).substring(1);
	  }
	  rman.loadResource(m.group(4), mult, param);
	}
	oman.loadPending();
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
	    oman.unloadObject(objname, 1);
	    oman.getObjects().remove(objname);
	  }
	});
      addHandler("load_object", new MessageHandler() {
	  public final void messageReceived(final Message msg) {
	    if (msg.getFieldsCount() != 1) {
	      return;
	    }
	    String objname = (String) msg.getField(0);
	    oman.loadObject(objname);
	    oman.loadPending();
	  }
	});
      addHandler("od_shutdown", new MessageHandler() {
	  public final void messageReceived(final Message msg) {
	    int exitCode = 0;
	    log.info(toString() + " shutting down...");
	    if (msg.getFieldsCount() == 1) {
	      exitCode = ((Integer) msg.getField(0)).intValue();
	    }
	    oman.unloadObject("stddispatcher", exitCode);
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
	      Iterator it = rman.getResources().keySet().iterator();
	      boolean found = false;
	      while (it.hasNext()) { // first hit
		String curClassName = (String) it.next();
		if (Pattern.matches(className + ":\\d+", curClassName)
		    && ((ResourceEntry) rman.getResources().get(curClassName)).isLoaded()) {
		  ODResourceAcquiredMessage m
		    = new ODResourceAcquiredMessage(msg.getOrigin(), msg.getId());
		  m.setResourceName(curClassName);
		  m.setResource(((ResourceEntry) rman.getResources().get(curClassName)).getResource());
		  send(m);
		  logger.fine((msgId++) + "" + msg.getOrigin() + " acquired " + curClassName);
		  if (willBlockState) {
		    oman.setBlockedState(msg.getOrigin(), oman.getBlockedState(msg.getOrigin()) + 1);
		  }
		  rman.getResources().remove(curClassName);
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
		oman.setBlockedState(odObjectName, oman.getBlockedState(odObjectName) + 1);
	      }
	      ODResourceAcquiredMessage m = new ODResourceAcquiredMessage(odObjectName, msg.getId());
	      m.setResourceName(className);
	      m.setResource(res);
	      send(m);
	      logger.fine((msgId++) + "" + msg.getOrigin() + " acquired " + className);
	    } else {
	      rman.getResources().put(className, new ResourceEntry(className.substring(0, className.length() - className.indexOf(":"))));
	    }
	    // decrease blocking state counter in case acquire was blocking
	    oman.setBlockedState(msg.getOrigin(), oman.getBlockedState(msg.getOrigin()) - 1);
	  }
	});
      addHandler("od_list_objects", new MessageHandler() {
	  public final void messageReceived(final Message msg) {
	    Message m = getNewMessage("object_list", msg.getOrigin(), "stddispatcher", msg.getId());
	    m.addField(new ArrayList(oman.getObjects().keySet()));
	    sendMessage(m);
	  }
	});
      addHandler("od_list_resources", new MessageHandler() {
	  public final void messageReceived(final Message msg) {
	    Message m = getNewMessage("resource_list", msg.getOrigin(), "stddispatcher", msg.getId());
	    m.addField(new ArrayList(rman.getResources().keySet()));
	    sendMessage(m);
	  }
	});
      addHandler("od_remove_dep", new MessageHandler() {
	  public final void messageReceived(final Message msg) {
	    if (msg.getFieldsCount() != 1) {
	      return;
	    }
	    ObjectEntry oe = (ObjectEntry) oman.getObjects().get(msg.getOrigin());
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
