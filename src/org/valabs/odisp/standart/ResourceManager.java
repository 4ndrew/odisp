package com.novel.odisp;

import com.novel.odisp.common.Resource;
import com.novel.odisp.common.ProxyResource;
import com.novel.odisp.common.Dispatcher;
import com.novel.odisp.common.ResourceManager;
import com.novel.odisp.common.Message;
import com.novel.stdmsg.ODResourceAcquiredMessage;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/** Менеджер ресурсных объектов ODISP.
 * @author (C) 2004 <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @version $Id: ResourceManager.java,v 1.6 2004/02/17 11:00:26 valeks Exp $
 */
public class StandartResourceManager implements ResourceManager {
  /** Список запросов на ресурсы. */
  private Map resourceRequests = new HashMap();
  /** Ссылка на диспетчер объектов. */
  private Dispatcher dispatcher;
  /** Журнал. */
  private Logger log = Logger.getLogger("com.novel.odisp.ResourceManager");
  /** Список ресурсов. */
  private Map resources = new HashMap();

  /** Доступ к ресурсам.
   * @return список ресурсов
   */
  public final Map getResources() {
    return resources;
  }

  /** Динамическая загрузка ресурсных объектов.
   * @param className имя загружаемого класса
   * @param mult количество загружаемых объектов
   * @param param параметр загрузки
   */
  public final void loadResource(final String className, int mult, final String param) {
    String logMessage = "loading resource ";
    ResourceEntry re = new ResourceEntry(className);
    re.setMaxUsage(mult);
    if (mult != ResourceEntry.MULT_SHARE) {
      logMessage+= "shared ";
      mult = 1;
    }
    logMessage+= className;
    int realMult = mult;
    for (int i = 0; i < mult; i++) {
      try {
	Resource r = (Resource) Class.forName(className).newInstance();
	re.addResource(r);
	logMessage += "+";
      } catch (ClassNotFoundException e) {
	log.warning(" failed: " + e);
	return;
      } catch (InstantiationException e) {
	log.warning(" failed: " + e);
	realMult--;
      } catch (IllegalAccessException e) {
	log.warning(" failed: " + e);
	return;
      }
    }
    if (realMult != mult) {
      // были загружены не все объекты
      re.setMaxUsage(realMult);
    }
    resources.put(className, re);
    logMessage += " ok.";
    log.config(logMessage);
  }

  /** Выгрузка ресурсного объекта.
   * @param name имя ресурсного объекта
   * @param code код выхода
   */
  public final void unloadResource(final String name, final int code) {
    if (resources.containsKey(name)) {
      ResourceEntry res = (ResourceEntry) resources.get(name);
      List dependingObjs = new ArrayList();
      Iterator it = dispatcher.getObjectManager().getObjects().keySet().iterator();
      while (it.hasNext()) {
	String className = (String) it.next();
	String[] depends = ((ObjectEntry) dispatcher.getObjectManager().getObjects().get(className)).getDepends();
	for (int i = 0; i < depends.length; i++) {
	  if (depends[i].equals(name) && !dependingObjs.contains(name)) {
	    dependingObjs.add(className);
	  }
	}
      }
      if (code == 0) {
	it = dependingObjs.iterator();
	while (it.hasNext()) {
	  dispatcher.getObjectManager().unloadObject((String) it.next(), code);
	}
      }
      res.cleanUp(code);
      resources.remove(name);
    }
  }
  /** Конструктор менеджера ресурсов.
   * @param newDispatcher ссылка на диспетчер ресурсами которого управляет менеджер
   */
  public StandartResourceManager(final Dispatcher newDispatcher) {
    dispatcher = newDispatcher;
    log.setLevel(java.util.logging.Level.ALL);
  }

  /** Обработка запроса на захват объекта.
   * @param msg сообщение о захвате
   */
  public final void acquireRequest(final Message msg) {
    String className = (String) msg.getField(0);
    boolean willBlockState = false;
    if (msg.getFieldsCount() == 2) {
      willBlockState = ((Boolean) msg.getField(1)).booleanValue();
    }
    log.fine("resource acquientance request from " + msg.getOrigin() + " to " + className);
    ResourceEntry re = (ResourceEntry) resources.get(className);
    if (re != null) {
      if (re.isAvailable()) {
	log.fine(className + " resource is in free pool.");
	// получение ресурса из хранилища
	Resource res = re.acquireResource();
	// конструирование и отправка сообщения
	ODResourceAcquiredMessage m = new ODResourceAcquiredMessage(msg.getOrigin(), msg.getId());
	m.setResourceName(className);
	m.setResource(res);
	dispatcher.send(m);
	if (willBlockState) {
	  log.fine("acquitentance of resource " + className + " by " + msg.getOrigin() + " will be blocking");
	  // сохранение и изменение статуса блокировки объекта
	  re.setBlockState(res, willBlockState);
	  dispatcher.getObjectManager().setBlockedState(msg.getOrigin(),
							dispatcher.getObjectManager().getBlockedState(msg.getOrigin()) + 1);
	}
      } else {
	log.finer(className + " resource busy -- putting request to queue");
	if (!resourceRequests.containsKey(className)) {
	  log.fine("created request queue for " + className);
	  resourceRequests.put(className, new ArrayList());
	}
	List rrl = (List) resourceRequests.get(className);
	rrl.add(new ResourceRequest(msg.getOrigin(), msg.getId(), willBlockState));
      }
    }
  }

  /** Обработка запроса на высвобождение ресурса.
   * @param msg сообщение о высвобождении
   */
  public final void releaseRequest(final Message msg) {
    if (msg.getFieldsCount() != 2) {
      return;
    }
    String className = (String) msg.getField(0);
    Resource res = (Resource) msg.getField(1);
    log.finest(msg.toString(true));
    if (!resourceRequests.containsKey(className)
	|| ((List) resourceRequests.get(className)).size() == 0) {
      log.fine(className + " no requests in queue");
      // если объект не требуется кем-то еще -- просто высвободить его
      ResourceEntry re = (ResourceEntry) resources.get(className);
      if (re.isBlockState(res)) {
	log.fine("releasing block for object " + msg.getOrigin());
	dispatcher.getObjectManager().setBlockedState(msg.getOrigin(),
						      dispatcher.getObjectManager().getBlockedState(msg.getOrigin()) - 1);
      }
      re.releaseResource(res);
    } else {
      log.fine(className + " requests in queue");
      // реализация очереди (FIFO) -- выбор головного элемента
      List rrl = (List) resourceRequests.get(className);
      ResourceRequest rr = (ResourceRequest) rrl.get(0);
      ResourceEntry re = (ResourceEntry) resources.get(className);
      if (re.isBlockState(res)) {
	log.fine("releasing block for object " + msg.getOrigin());
	dispatcher.getObjectManager().setBlockedState(msg.getOrigin(),
						      dispatcher.getObjectManager().getBlockedState(msg.getOrigin()) - 1);
      }
      // подготовка и посылка сообщения о захвате ресурса
      ODResourceAcquiredMessage m = new ODResourceAcquiredMessage(rr.getObjectName(), rr.getMsgId());
      m.setResourceName(className);
      m.setResource(res);
      dispatcher.send(m);
      if (rr.isBlockState()) {
	log.finer("setting block for object " + rr.getObjectName());
	// сохранение нового статуса блокировки объекта
	re.setBlockState(res, rr.isBlockState());
	dispatcher.getObjectManager().setBlockedState(msg.getOrigin(),
						      dispatcher.getObjectManager().getBlockedState(msg.getOrigin()) + 1);
      }
      // удаление запроса из головы очереди
      rrl.remove(0);
      log.finer("request removed from head of a queue");
    }
  }

  /** Запись о запросе на ресурс. */
  private class ResourceRequest {
    /** Имя объект. */
    private String object;
    /** Доступ к имени объекта.
     * @return имя объекта
     */
    public String getObjectName() {
      return object;
    }
    /** Номер сообщения запроса. */
    private int msgId;
    /** Доступ к номеру сообщения
     * @return номер сообщения
     */
    public int getMsgId() {
      return msgId;
    }
    /** Состояние блокировки при захвате. */
    private boolean willBlockState;
    /** Доступ к состоянию блокировки.
     * @return состояние блокировки
     */
    public boolean isBlockState() {
      return willBlockState;
    }
    /** Конструктор новой записи.
     * @param newObject имя объекта
     * @param newMsgId номер сообщения
     * @param newWillBlockState состояние блокировки
     */
    public ResourceRequest(final String newObject,
			   final int newMsgId,
			   final boolean newWillBlockState) {
      object = newObject;
      msgId = newMsgId;
      willBlockState = newWillBlockState;
      log.finer(newObject + " request is now on queue");
    }
  } // ResourceRequest
} // ResourceManager
