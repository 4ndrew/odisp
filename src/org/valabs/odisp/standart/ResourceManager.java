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

/** �������� ��������� �������� ODISP.
 * @author (C) 2004 <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @version $Id: ResourceManager.java,v 1.33 2005/01/11 20:47:14 valeks Exp $
 */
class ResourceManager implements org.valabs.odisp.common.ResourceManager {
  /** ������ �� ��������� ��������. */
  private Dispatcher dispatcher;
  /** ���� ��������� ��������. */
  private DataThread dataThread = null;
	/** �������� �������� �������. */
	private ClassLoader loader;
  /** ������. */
  private Logger log = Logger.getLogger(ResourceManager.class.getName());

  /** ������ � ��������.
   * @return ������ ��������
   */
  public final Map getResources() {
    return dataThread.getResources();
  }

  /** ������������ �������� ��������� ��������.
   * @param className ��� ������������ ������
   * @param mult ���������� ����������� ��������
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

  /** �������� ���������� �������.
   * @param className ��� ���������� �������
   * @param code ��� ������
   */
  public final void unloadResource(final String className, final int code) {
    dataThread.addRequest(new UnloadResourceRequest(className, code));
  }

  /** ����������� ��������� ��������.
   * @param newDispatcher ������ �� ��������� ��������� �������� ��������� ��������
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


  /** ��������� ����������� ������ ����������� DataThread. */
  private interface RequestListEntry {
    /** ���������� �������.
     * @param data ���� ������ �� ������� ����������� ������
     * @return ���� ����������� �������� �������
     */
    public boolean performAction(final DataThread data);
    /** ��������� �������� ������. */
    public String toString();
  } // RequestListEntry

  /** ������ �� �������� �������. */
  private class UnloadResourceRequest implements RequestListEntry {
    /** ��� ������ ������� ��� ��������. */
    private String className = null;
    /** ��� ��������. */
    private int code = 0;
    /** ������������� ������ ������� �� �������� �������. */
    public UnloadResourceRequest(final String nclassName, final int ncode) {
      className = nclassName;
      code = ncode;
    }
    /** ���������� ������� �� �������� ���� ������.
     * �� ������ ������ � ������ ���� � ������� ������� ������� � ������������ ������� ��� ��� � ���������.
     * ���������� ������� �� ���� ���������: ���� �� ��������� ������ �� �������� ���� ������� �� ������ �� �����
     * ������������� ��� ������� �������� ������� �� ������� �������������.
     * ����������� �������� ��������� �������� ���������� ������ � ��� ������ ���� ��� �������� ����� 0.
     * @param dt ���� ������
     */
    public final boolean performAction(final DataThread dt) {
      if (dt.getResources().containsKey(className)) {
	ResourceEntry res = (ResourceEntry) dt.getResources().get(className);
	List dependingObjs = new ArrayList();
	Iterator it = dt.getDispatcher().getObjectManager().getObjects().keySet().iterator(); // TODO: ������ ���� ����
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
    /** �������� ������ ��� ����������. */
    public final String toString() {
      return "UnloadResource on " + className;
    }
  } // UnloadResourceRequest

  /** ��������� ���� ��������� �������� �� ������-������������� � ����� DataThread,
   * ������� ����������� ������������ ��������� � ������ ��������.
   */
  private class DataThread extends Thread {
    /** ������ ��������. */
    private Map resources = new HashMap();
    /** ������ � ������ ��������. */
    public Map getResources() {
      return resources;
    }
    /** ��������� ��������. */
    private List requestList = new ArrayList();
    /** ������ �� ������ � ������ ��������. */
    public List getRequestList () {
      return Collections.unmodifiableList(requestList);
    }
    /** ������ �� ����������. */
    private Dispatcher dispatcher = null;
    /** ������ ��� �������� � ����������. */
    public Dispatcher getDispatcher() {
      return dispatcher;
    }
    /** ���������� ������� � �������.
     * @param req ������
     */
    public void addRequest(RequestListEntry req) {
      synchronized (requestList) {
	requestList.add(req);
      }
      synchronized (this) {
        notify();
      }
    }
    /** ����������� ���������������� � ����������� ����.
     * @param nDispatcher ������ �� ���������
     */
    public DataThread(final Dispatcher nDispatcher) {
      super("Resource requests handler");
      dispatcher = nDispatcher;
      setDaemon(true);
      this.start();
    }

    /** �����, ������� ���������� ������������� � ��������� ������� �� �������. */
    public void run() {
      while (true) {
	try {
	  synchronized (this) {
	    wait(100);
	  }
	} catch (InterruptedException e) {}
	synchronized (requestList) {
	  /** TODO: ���������� �� ��������� ������� ���, ��� �� �� ������� ��������� �������� ������������ �������. */
          List localList = new ArrayList(requestList);
	  Iterator it = localList.iterator();
	  List toRemove = new ArrayList();
	  while (it.hasNext()) {
	    RequestListEntry rle = (RequestListEntry) it.next();
	    if(rle.performAction(this)) {
	      // ��������� � ������� �� �������� ������ ������������ �������
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
