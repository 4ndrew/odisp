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
 * @version $Id: ResourceManager.java,v 1.33 2005/01/11 20:47:14 valeks Exp $
 */
class ResourceManager implements org.valabs.odisp.common.ResourceManager {
  /** Ссылка на диспетчер объектов. */
  private Dispatcher dispatcher;
  /** Нить обработки запросов. */
  private DataThread dataThread = null;
	/** Менеджер загрузки классов. */
	private ClassLoader loader;
  /** Журнал. */
  private Logger log = Logger.getLogger(ResourceManager.class.getName());

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
  public final void loadResource(final String className, int mult, final Map config) {
    String logMessage = mult + " loading resource ";
    ResourceEntry re = new ResourceEntry(className);
    re.setMaxUsage(mult);
    if (mult == ResourceEntry.MULT_SHARE) {
      logMessage += "shared ";
      mult = 1;
    }
    logMessage += className;
    for (int i = 0; i < mult; i++) {
      try {
        loader = ReloadableClassLoader.getInstance("../build/");
        Resource r = (Resource) loader.loadClass(className).newInstance();
        r.setConfiguration(config);
        re.addResource(r);
        logMessage += "+";
      } catch (ClassNotFoundException e) {
        log.warning(" failed: " + e);
        return;
      } catch (InstantiationException e) {
        log.warning(" failed: " + e);
        return;
      } catch (IllegalAccessException e) {
        log.warning(" failed: " + e);
        return;
      }
    }
    synchronized (dataThread) {
      dataThread.getResources().put(className, re);
    }
    logMessage += " ok";
    log.config(logMessage);
  }

  /** Выгрузка ресурсного объекта.
   * @param className имя ресурсного объекта
   * @param code код выхода
   */
  public final void unloadResource(final String className, final int code) {
    dataThread.addRequest(new UnloadResourceRequest(className, code));
  }

  /** Конструктор менеджера ресурсов.
   * @param newDispatcher ссылка на диспетчер ресурсами которого управляет менеджер
   */
  public ResourceManager(final Dispatcher newDispatcher) {
    log.setLevel(java.util.logging.Level.ALL);
    dispatcher = newDispatcher;
    dataThread = new DataThread(newDispatcher);
		loader = ReloadableClassLoader.getInstance("../build/"); // XXX: Make modifyable
  }

  public List statRequest() {
    List result = new ArrayList();
    Iterator it = dataThread.getResources().keySet().iterator();
    while (it.hasNext()) {
      String name = (String) it.next();
      ResourceEntry re = (ResourceEntry) dataThread.getResources().get(name);
      result.add(re.toString());
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

  /** Запрос на выгрузку ресурса. */
  private class UnloadResourceRequest implements RequestListEntry {
    /** Имя класса ресурса для выгрузки. */
    private String className = null;
    /** Код выгрузки. */
    private int code = 0;
    /** Инициализация нового запроса на загрузку ресурса. */
    public UnloadResourceRequest(final String nclassName, final int ncode) {
      className = nclassName;
      code = ncode;
    }
    /** Выполнение запроса на заданной нити данных.
     * На данный момент в случае если в очереди имеются запросы к выгружаемому ресурсу они там и останутся.
     * Необходимо выбрать из двух стратегий: либо не выполнять запрос на выгрузку пока запросы на захват не будут
     * удовлетворены или удалять подобные ресурсы из очереди автоматически.
     * Фактическая выгрузка зависимых объектов происходит только в том случае если код выгрузки равен 0.
     * @param dt нить данных
     */
    public final boolean performAction(final DataThread dt) {
      if (dt.getResources().containsKey(className)) {
	ResourceEntry res = (ResourceEntry) dt.getResources().get(className);
	List dependingObjs = new ArrayList();
	Iterator it = dt.getDispatcher().getObjectManager().getObjects().keySet().iterator(); // TODO: убрать этот ужас
	while (it.hasNext()) {
	  String oclassName = (String) it.next();
	  String[] depends = ((ObjectEntry) dt.getDispatcher().getObjectManager().getObjects().get(oclassName)).getDepends();
	  for (int i = 0; i < depends.length; i++) {
	    if (depends[i].equals(className) && !dependingObjs.contains(className)) {
	      dependingObjs.add(oclassName);
	    }
	  }
	}
	if (code == 0) {
	  it = dependingObjs.iterator();
	  while (it.hasNext()) {
	    dt.getDispatcher().getObjectManager().unloadObject((String) it.next(), code);
	  }
	}
	res.cleanUp(code);
	dt.getResources().remove(className);
      }
      return true;
    }
    /** Описание класса для статистики. */
    public final String toString() {
      return "UnloadResource on " + className;
    }
  } // UnloadResourceRequest

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
  public Resource resourceAcquire(String className) {
    Resource result = null;
    while (result == null) {
      result = resourceTryAcquire(className);
      try {
        Thread.sleep(10);
      } catch (InterruptedException e) {
        dispatcher.getExceptionHandler().signalException(e);
      }
    }
    System.err.println("Resource type: " + result.getClass() + " asked to acquire " + className);
    return result;
  }

  /**
   * @see org.valabs.odisp.common.ResourceManager#resourceTryAcquire(java.lang.String)
   */
  public Resource resourceTryAcquire(String className) {
    synchronized (dataThread) {
      Map resources = dataThread.getResources();
      if (resources.containsKey(className)) {
        ResourceEntry re = (ResourceEntry) resources.get(className);
        if (re.isAvailable()) {
          Resource result = re.acquireResource("whom-ever"); // XXX
          return result;
        }
      }
    }
    return null;
  }

  /**
   * @see org.valabs.odisp.common.ResourceManager#releaseResource(java.lang.String, org.valabs.odisp.common.Resource)
   */
  public void releaseResource(String className, Resource resource) {
    synchronized (dataThread) {
      Map resources = dataThread.getResources();
      if (resources.containsKey(className)) {
        ResourceEntry re = (ResourceEntry) resources.get(className);
        re.releaseResource(resource);
      }
    }
  }
} // StandartResourceManager
