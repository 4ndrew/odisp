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

/** �������� ��������� �������� ODISP.
 * @author (C) 2004 <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @version $Id: ResourceManager.java,v 1.27 2004/08/23 13:20:00 valeks Exp $
 */
class ResourceManager implements org.valabs.odisp.common.ResourceManager {
  /** ������ �� ��������� ��������. */
  //  private Dispatcher dispatcher;
  /** ���� ��������� ��������. */
  private DataThread dataThread = null;
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
  public final void loadResource(final String className, final int mult, final Map config) {
    dataThread.addRequest(new LoadResourceRequest(className, mult, config));
  }

  /** �������� ���������� �������.
   * @param className ��� ���������� �������
   * @param code ��� ������
   */
  public final void unloadResource(final String className, final int code) {
    dataThread.addRequest(new UnloadResourceRequest(className, code));
  }

  /** ��������� ������� �� ������ �������.
   * @param msg ��������� � �������
   */
  public final void acquireRequest(final Message msg) {
    String className = ODAcquireMessage.getResourceName(msg);
    log.fine("resource acquientance request from " + msg.getOrigin() + " to " + className);
    dataThread.addRequest(new AcquireResourceRequest(msg.getOrigin(), msg.getId(), className));
  }

  /** ��������� ������� �� ������������� �������.
   * @param msg ��������� � �������������
   */
  public final void releaseRequest(final Message msg) {
    if (msg.getFieldsCount() != 2) {
      return;
    }
    // ������ ���������
    String className = ODReleaseMessage.getResourceName(msg);
    Resource res = ODReleaseMessage.getResource(msg);
    dataThread.addRequest(new ReleaseResourceRequest(msg.getOrigin(), className, res));
  }

  /** ����������� ��������� ��������.
   * @param newDispatcher ������ �� ��������� ��������� �������� ��������� ��������
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

  /** ������ �� ������������� �������. */
  private class ReleaseResourceRequest implements RequestListEntry {
    /** ��� ������. */
    private String className = null;
    /** ������ �� ������. */
    private Resource resource = null;
    /** ��� ������� �������������� ������. */
    private String origin = null;
    /** �������� ������ ������� �� �������������.
     * @param nclassName ��� ��������������� �������
     * @param nresource ������ �� ������
     */
    public ReleaseResourceRequest(final String norigin,
				  final String nclassName,
				  final Resource nresource) {
      origin = norigin;
      className = nclassName;
      resource = nresource;
    }

    /** ���������� �������� �� ���� ������. */
    public final boolean performAction(final DataThread dt) {
      ResourceEntry re = (ResourceEntry) dt.getResources().get(className);
      re.releaseResource(resource);
      return true;
    }

    /** �������� ������ ��� ����������. */
    public final String toString() {
      return "ReleaseResource from " + origin + " on " + className;
    }
  }

  /** ������ �� ������ �������. */
  private class AcquireResourceRequest implements RequestListEntry {
    /** ��� �������. */
    private String className = null;
    /** ��� �����������. */
    private String origin = null;
    /** ������ ��������� ��������������� ������. */
    private int msgid = -1;
    /** ���� ������� �������. */
    private boolean checkOnly = false;
    /** ��������������� ������ �������.
     * @param nclassName ��� �������
     * @param norigin ����������� �������
     * @param nmsgid ������ ���������
     */
    public AcquireResourceRequest(final String norigin, final int nmsgId,
				  final String nclassName) {
      className = nclassName;
      origin = norigin;
      msgid = nmsgId;
    }
    /** ���������� �������.
     * � ������ ���� ������ �� ������ ������ ����� ��������.
     * @param dt ���� ������
     */
    public boolean performAction(final DataThread dt) {
      if (dt.getResources().containsKey(className)) {
	ResourceEntry re = (ResourceEntry) dt.getResources().get(className);
	if (re.isAvailable()) {
	  log.fine(className + " resource is in free pool.");
	  // ��������� ������� �� ���������
	  Resource res = re.acquireResource(origin);
	  // ��������������� � �������� ���������
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
	// ��� ������ ������� -- ������ ������� ������. TODO: ��������� �� ������?
      }
      return true;
    }
    /** �������� ������ ��� ����������. */
    public final String toString() {
      return "AcquireResource from " + origin + " on " + className;
    }
  }
  /** ������ �� �������� �������. */
  private class LoadResourceRequest implements RequestListEntry {
    /** ��� ������ �������. */
    private String className = null;
    /** ��������������. */
    private int mult = ResourceEntry.MULT_SHARE;
    /** ����� ����������. */
    private Map configuration = null;
    /** �������� ������ ������� �� �������� �������.
     * @param nclassName ��� ������ ��� ��������
     * @param nmult ��������� ��������
     * @param config ������������ �������
     */
    public LoadResourceRequest(final String nclassName, final int nmult, final Map config) {
      className = nclassName;
      mult = nmult;
      configuration = config;
    }
    /** ���������� �������.
     * @param dt ���� ������ �� ������� ����������� ������
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
    /** �������� ������ ��� ����������. */
    public final String toString() {
      return "LoadResource on " + className;
    }
  } // LoadResourceRequest

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
} // StandartResourceManager
