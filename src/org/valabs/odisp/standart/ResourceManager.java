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
 * @version $Id: ResourceManager.java,v 1.3 2004/02/13 15:16:03 valeks Exp $
 */
public class StandartResourceManager implements ResourceManager {
  /** Список ресурсов. */
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
      res.getResource().cleanUp(code);
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
    Iterator it = getResources().keySet().iterator();
    boolean found = false;
    while (it.hasNext()) { // first hit
      String curClassName = (String) it.next();
      if (Pattern.matches(className + ":\\d+", curClassName)
	  && ((ResourceEntry) resources.get(curClassName)).isLoaded()) {
	ODResourceAcquiredMessage m
	  = new ODResourceAcquiredMessage(msg.getOrigin(), msg.getId());
	m.setResourceName(curClassName);
	m.setResource(((ResourceEntry) resources.get(curClassName)).getResource());
	dispatcher.send(m);
	if (willBlockState) {
	  dispatcher.getObjectManager().setBlockedState(msg.getOrigin(),
							dispatcher.getObjectManager().getBlockedState(msg.getOrigin()) + 1);
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

  /** Обработка запроса на высвобождение ресурса.
   * @param msg сообщение о высвобождении
   */
  public final void releaseRequest(final Message msg) {
    if (msg.getFieldsCount() != 2) {
      return;
    }
    String className = (String) msg.getField(0);
    Resource res = (Resource) msg.getField(1);
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
	dispatcher.getObjectManager().setBlockedState(odObjectName, dispatcher.getObjectManager().getBlockedState(odObjectName) + 1);
      }
      ODResourceAcquiredMessage m = new ODResourceAcquiredMessage(odObjectName, msg.getId());
      m.setResourceName(className);
      m.setResource(res);
      dispatcher.send(m);
    } else {
      resources.put(className, new ResourceEntry(className.substring(0, className.length() - className.indexOf(":"))));
    }
    // decrease blocking state counter in case acquire was blocking
    dispatcher.getObjectManager().setBlockedState(msg.getOrigin(), dispatcher.getObjectManager().getBlockedState(msg.getOrigin()) - 1);
  }
} // ResourceManager
