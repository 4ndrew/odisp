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
 * @author (C) 2004 <a href="mailto:valeks@valeks.novel.local">Valentin A. Alekseev</a>
 * @version $Id: ResourceManager.java,v 1.4 2004/02/13 22:22:50 valeks Exp $
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
  public final void loadResource(final String className, final int mult, final String param) {
    String logMessage = "loading resource " + className;
    ResourceEntry re = new ResourceEntry(className);
    re.setMaxUsage(mult);
    for (int i = 0; i < mult; i++) {
      try {
	Resource r = (Resource) Class.forName(className).newInstance();
	re.addResource(r);
	logMessage += "+";
      } catch (ClassNotFoundException e) {
	log.warning(" failed: " + e);
      } catch (InstantiationException e) {
	log.warning(" failed: " + e);
      } catch (IllegalAccessException e) {
	log.warning(" failed: " + e);
      }
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
    ResourceEntry re = (ResourceEntry) resources.get(className);
    if (re != null) {
      if (re.isAvailable()) {
	// получение ресурса из хранилища
	Resource res = re.acquireResource();
	// конструирование и отправка сообщения
	ODResourceAcquiredMessage m = new ODResourceAcquiredMessage(msg.getOrigin(), msg.getId());
	m.setResourceName(className);
	m.setResource(res);
	dispatcher.send(m);
	if (willBlockState) {
	  // сохранение и изменение статуса блокировки объекта
	  re.setBlockState(res, willBlockState);
	  dispatcher.getObjectManager().setBlockedState(msg.getOrigin(),
							dispatcher.getObjectManager().getBlockedState(msg.getOrigin()) + 1);
	}
      } else {
	if (!resourceRequests.containsKey(className)) {
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
    if (!resourceRequests.containsKey(className)
	|| ((List) resourceRequests.get(className)).size() == 0) {
      // если объект не требуется кем-то еще -- просто высвободить его
      ResourceEntry re = (ResourceEntry) resources.get(className);
      re.releaseResource(res);
    } else {
      // реализация очереди (FIFO) -- выбор головного элемента
      List rrl = (List) resourceRequests.get(className);
      ResourceRequest re = (ResourceRequest) rrl.get(0);
      // подготовка и посылка сообщения о захвате ресурса
      ODResourceAcquiredMessage m = new ODResourceAcquiredMessage(re.getObjectName(), re.getMsgId());
      m.setResourceName(className);
      m.setResource(res);
      dispatcher.send(m);
      if (re.isBlockState()) {
	// сохранение нового статуса блокировки объекта
	((ResourceEntry) resources.get(className)).setBlockState(res, re.isBlockState());
	dispatcher.getObjectManager().setBlockedState(msg.getOrigin(),
						      dispatcher.getObjectManager().getBlockedState(msg.getOrigin()) + 1);
      }
      // удаление запроса из головы очереди
      rrl.remove(0);
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
    }
  } // ResourceRequest
} // ResourceManager
