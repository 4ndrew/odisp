package org.valabs.odisp.standart;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.valabs.odisp.common.Dispatcher;
import org.valabs.odisp.common.Message;
import org.valabs.odisp.common.Resource;
import org.valabs.stdmsg.ODAcquireMessage;
import org.valabs.stdmsg.ODReleaseMessage;
import org.valabs.stdmsg.ODResourceAcquiredMessage;

/** Менеджер ресурсных объектов ODISP.
 * @author (C) 2004 <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @version $Id: ResourceManager.java,v 1.27 2004/08/23 13:20:00 valeks Exp $
 */
class ResourceManager implements org.valabs.odisp.common.ResourceManager {
  /** Ссылка на диспетчер объектов. */
  //  private Dispatcher dispatcher;
  /** Нить обработки запросов. */
  private DataThread dataThread = null;
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
  public final void loadResource(final String className, final int mult, final Map config) {
    dataThread.addRequest(new LoadResourceRequest(className, mult, config));
  }

  /** Выгрузка ресурсного объекта.
   * @param className имя ресурсного объекта
   * @param code код выхода
   */
  public final void unloadResource(final String className, final int code) {
    dataThread.addRequest(new UnloadResourceRequest(className, code));
  }

  /** Обработка запроса на захват объекта.
   * @param msg сообщение о захвате
   */
  public final void acquireRequest(final Message msg) {
    String className = ODAcquireMessage.getResourceName(msg);
    log.fine("resource acquientance request from " + msg.getOrigin() + " to " + className);
    dataThread.addRequest(new AcquireResourceRequest(msg.getOrigin(), msg.getId(), className));
  }

  /** Обработка запроса на высвобождение ресурса.
   * @param msg сообщение о высвобождении
   */
  public final void releaseRequest(final Message msg) {
    if (msg.getFieldsCount() != 2) {
      return;
    }
    // разбор сообщения
    String className = ODReleaseMessage.getResourceName(msg);
    Resource res = ODReleaseMessage.getResource(msg);
    dataThread.addRequest(new ReleaseResourceRequest(msg.getOrigin(), className, res));
  }

  /** Конструктор менеджера ресурсов.
   * @param newDispatcher ссылка на диспетчер ресурсами которого управляет менеджер
   */
  public ResourceManager(final Dispatcher newDispatcher) {
    log.setLevel(java.util.logging.Level.ALL);
    dataThread = new DataThread(newDispatcher);
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

  /** Запрос на высвобождение ресурса. */
  private class ReleaseResourceRequest implements RequestListEntry {
    /** Имя класса. */
    private String className = null;
    /** Ссылка на ресурс. */
    private Resource resource = null;
    /** Имя объекта высвободившего ресурс. */
    private String origin = null;
    /** Создание нового запроса на высвобождение.
     * @param nclassName имя высвобождаемого ресурса
     * @param nresource ссылка на ресурс
     */
    public ReleaseResourceRequest(final String norigin,
				  final String nclassName,
				  final Resource nresource) {
      origin = norigin;
      className = nclassName;
      resource = nresource;
    }

    /** Выполнение действия на нити данных. */
    public final boolean performAction(final DataThread dt) {
      ResourceEntry re = (ResourceEntry) dt.getResources().get(className);
      re.releaseResource(resource);
      return true;
    }

    /** Описание класса для статистики. */
    public final String toString() {
      return "ReleaseResource from " + origin + " on " + className;
    }
  }

  /** Запрос на захват ресурса. */
  private class AcquireResourceRequest implements RequestListEntry {
    /** Имя ресурса. */
    private String className = null;
    /** Имя отправителя. */
    private String origin = null;
    /** Индекс сообщения инициировавшего запрос. */
    private int msgid = -1;
    /** Флаг попытки запроса. */
    private boolean checkOnly = false;
    /** Конструирование нового запроса.
     * @param nclassName имя ресурса
     * @param norigin отправитель запроса
     * @param nmsgid индекс сообщения
     */
    public AcquireResourceRequest(final String norigin, final int nmsgId,
				  final String nclassName) {
      className = nclassName;
      origin = norigin;
      msgid = nmsgId;
    }
    /** Выполнение запроса.
     * В случае если ресурс не найден запрос будет выброшен.
     * @param dt нить данных
     */
    public boolean performAction(final DataThread dt) {
      if (dt.getResources().containsKey(className)) {
	ResourceEntry re = (ResourceEntry) dt.getResources().get(className);
	if (re.isAvailable()) {
	  log.fine(className + " resource is in free pool.");
	  // получение ресурса из хранилища
	  Resource res = re.acquireResource(origin);
	  // конструирование и отправка сообщения
	  Message m = dt.getDispatcher().getNewMessage();
	  ODResourceAcquiredMessage.setup(m, origin, msgid);
	  ODResourceAcquiredMessage.setResourceName(m, className);
	  ODResourceAcquiredMessage.setResource(m, res);
	  dt.getDispatcher().send(m);
	} else {
	  checkOnly = true;
	  return false;
	}
      } else {
	// нет такого ресурса -- просто удалить запрос. TODO: сообщение об ошибке?
      }
      return true;
    }
    /** Описание класса для статистики. */
    public final String toString() {
      return "AcquireResource from " + origin + " on " + className;
    }
  }
  /** Запрос на загрузку ресурса. */
  private class LoadResourceRequest implements RequestListEntry {
    /** Имя класса ресурса. */
    private String className = null;
    /** Мультипликатор. */
    private int mult = ResourceEntry.MULT_SHARE;
    /** Списк параметров. */
    private Map configuration = null;
    /** Создание нового запроса на загрузку ресурса.
     * @param nclassName имя класса для загрузки
     * @param nmult множитель загрузки
     * @param config конфигурация ресурса
     */
    public LoadResourceRequest(final String nclassName, final int nmult, final Map config) {
      className = nclassName;
      mult = nmult;
      configuration = config;
    }
    /** Выполнение запроса.
     * @param dt нить данных на которой выполняется запрос
     */
    public final boolean performAction(final DataThread dt) {
      String logMessage = mult + " loading resource ";
      ResourceEntry re = new ResourceEntry(className);
      re.setMaxUsage(mult);
      if (mult == ResourceEntry.MULT_SHARE) {
	logMessage+= "shared ";
	mult = 1;
      }
      logMessage+= className;
      for (int i = 0; i < mult; i++) {
	try {
	  Resource r = (Resource) Class.forName(className).newInstance();
	  r.setConfiguration(configuration);
	  re.addResource(r);
	  logMessage += "+";
	} catch (ClassNotFoundException e) {
	  log.warning(" failed: " + e);
	  return true;
	} catch (InstantiationException e) {
	  log.warning(" failed: " + e);
	} catch (IllegalAccessException e) {
	  log.warning(" failed: " + e);
	  return true;
	}
      }
      dt.getResources().put(className, re);
      logMessage += " ok";
      log.config(logMessage);
      synchronized (dt.dispatcher) {
      	dt.dispatcher.notify();
      }
      return true;
    }
    /** Описание класса для статистики. */
    public final String toString() {
      return "LoadResource on " + className;
    }
  } // LoadResourceRequest

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
} // StandartResourceManager
