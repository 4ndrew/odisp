package org.valabs.odisp.standart;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.valabs.odisp.common.Dispatcher;
import org.valabs.odisp.common.Resource;

/** Менеджер ресурсных объектов ODISP.
 * @author (C) 2004 <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @version $Id: ResourceManager.java,v 1.36 2005/02/27 12:37:31 valeks Exp $
 */
class ResourceManager implements org.valabs.odisp.common.ResourceManager {
  /** Ссылка на диспетчер объектов. */
  private final Dispatcher dispatcher;
  /** Нить обработки запросов. */
  private final DataThread dataThread;

  /** Журнал. */
  private static final Logger log = Logger.getLogger(ResourceManager.class.getName());

  /** Доступ к ресурсам.
   * @return список ресурсов
   */
  public final Map getResources() {
    return dataThread.getResources();
  }

  /** Динамическая загрузка ресурсных объектов.
   * @param className имя загружаемого класса
   * @param mult количество загружаемых объектов
   */
  public final void loadResource(final String className, final int mult, final Map config) {
    int realMult = mult;
    String logMessage = realMult + " loading resource ";
    final ResourceEntry re = new ResourceEntry(className);
    re.setMaxUsage(realMult);
    if (mult == ResourceEntry.MULT_SHARE) {
      logMessage += "shared ";
      realMult = 1;
    }
    logMessage += className;
    for (int i = 0; i < mult; i++) {
      try {
        final Resource r = (Resource) Class.forName(className).newInstance();
        r.setConfiguration(config);
        re.addResource(r);
        logMessage += "+";
        synchronized (dataThread) {
          dataThread.getResources().put(className, re);
        }
        logMessage += " ok";
        log.config(logMessage);
      } catch (ClassNotFoundException e) {
        log.warning(" failed: " + e);
      } catch (InstantiationException e) {
        log.warning(" failed: " + e);
      } catch (IllegalAccessException e) {
        log.warning(" failed: " + e);
      }
    }
  }

  /** Конструктор менеджера ресурсов.
   * @param newDispatcher ссылка на диспетчер ресурсами которого управляет менеджер
   */
  public ResourceManager(final Dispatcher newDispatcher) {
    log.setLevel(java.util.logging.Level.ALL);
    dispatcher = newDispatcher;
    dataThread = new DataThread(newDispatcher);
  }

  public List statRequest() {
    final List result = new ArrayList();
    Iterator it = dataThread.getResources().keySet().iterator();
    while (it.hasNext()) {
      result.add(dataThread.getResources().get(it.next()).toString());
    }
    it = dataThread.getRequestList().iterator();
    result.add("DataThread request queue...");
    while (it.hasNext()) {
      result.add(((RequestListEntry) it.next()).toString());
    }
    return result;
  }


  /** Интерфейс описывающий запрос выполняемый DataThread. */
  private interface RequestListEntry {
    /** Выполнение запроса.
     * @param data нить данных на которой выполняется запрос
     * @return флаг возможности удаления запроса
     */
    public boolean performAction(final DataThread data);
    /** Строковое описание класса. */
    public String toString();
  } // RequestListEntry

  /** Отдельная нить обработки запросов на захват-высвобождение в стиле DataThread,
   * которая гарантирует синхронность обращение к списку ресурсов.
   */
  private class DataThread extends Thread {
    /** Список ресурсов. */
    private Map resources = new HashMap();
    /** Доступ к списку ресурсов. */
    public Map getResources() {
      return resources;
    }
    /** Хранилище запросов. */
    private List requestList = new ArrayList();
    /** Доступ на чтение к списку запросов. */
    public List getRequestList () {
      return Collections.unmodifiableList(requestList);
    }
    /** Ссылка на диспетчера. */
    private Dispatcher dispatcher = null;
    /** Доступ для запросов к диспетчеру. */
    public Dispatcher getDispatcher() {
      return dispatcher;
    }
    /** Добавление запроса в очередь.
     * @param req запрос
     */
    public void addRequest(RequestListEntry req) {
      synchronized (requestList) {
	requestList.add(req);
      }
      synchronized (this) {
        notify();
      }
    }
    /** Конструктор инициализирующий и запускающий нить.
     * @param nDispatcher ссылка на диспетчер
     */
    public DataThread(final Dispatcher nDispatcher) {
      super("Resource requests handler");
      dispatcher = nDispatcher;
      setDaemon(true);
      this.start();
    }

    /** Метод, который циклически просматривает и выполняет запросы из очереди. */
    public void run() {
      while (true) {
	try {
	  synchronized (this) {
	    wait(100);
	  }
	} catch (InterruptedException e) {}
	synchronized (requestList) {
	  /** TODO: переделать на несколько наборов так, что бы не пытатся исполнять заведомо невыполнимые запросы. */
          List localList = new ArrayList(requestList);
	  Iterator it = localList.iterator();
	  List toRemove = new ArrayList();
	  while (it.hasNext()) {
	    RequestListEntry rle = (RequestListEntry) it.next();
	    if(rle.performAction(this)) {
	      // сохранять в очередь на удаление только отработавшие запросы
	      toRemove.add(rle);
	    }
	  }
	  requestList.removeAll(toRemove);
	}
      }
    }
  } // DataThread

  /**
   * @see org.valabs.odisp.common.ResourceManager#resourceAcquire(java.lang.String)
   */
  public final Resource resourceAcquire(final String className) {
    Resource result = null;
    while (result == null) {
      result = resourceTryAcquire(className);
      try {
        Thread.sleep(10);
      } catch (InterruptedException e) {
        dispatcher.getExceptionHandler().signalException(e);
      }
    }
    return result;
  }

  /**
   * @see org.valabs.odisp.common.ResourceManager#resourceTryAcquire(java.lang.String)
   */
  public Resource resourceTryAcquire(final String className) {
    Resource result = null;
    synchronized (dataThread) {
      final Map resources = dataThread.getResources();
      if (resources.containsKey(className)) {
        final ResourceEntry re = (ResourceEntry) resources.get(className);
        if (re.isAvailable()) {
          result = re.acquireResource("whom-ever"); // XXX
        }
      }
    }
    return result;
  }

  /**
   * @see org.valabs.odisp.common.ResourceManager#releaseResource(java.lang.String, org.valabs.odisp.common.Resource)
   */
  public void releaseResource(final String className, final Resource resource) {
    synchronized (dataThread) {
      final Map resources = dataThread.getResources();
      if (resources.containsKey(className)) {
        ((ResourceEntry) resources.get(className)).releaseResource(resource);
      }
    }
  }
} // StandartResourceManager
