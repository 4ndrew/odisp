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

/** �������� ��������� �������� ODISP.
 * @author (C) 2004 <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @version $Id: ResourceManager.java,v 1.8 2004/02/20 13:56:21 valeks Exp $
 */
public class StandartResourceManager implements ResourceManager {
  /** ������ �������� �� �������. */
  private RequestQueue resourceQueue = new RequestQueue();
  /** ������ �� ��������� ��������. */
  private Dispatcher dispatcher;
  /** ������. */
  private Logger log = Logger.getLogger("com.novel.odisp.ResourceManager");
  /** ������ ��������. */
  private Map resources = new HashMap();

  /** ������ � ��������.
   * @return ������ ��������
   */
  public final Map getResources() {
    return resources;
  }

  /** ������������ �������� ��������� ��������.
   * @param className ��� ������������ ������
   * @param mult ���������� ����������� ��������
   * @param param �������� ��������
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
      // ���� ��������� �� ��� �������
      re.setMaxUsage(realMult);
    }
    resources.put(className, re);
    logMessage += " ok.";
    log.config(logMessage);
  }

  /** �������� ���������� �������.
   * @param name ��� ���������� �������
   * @param code ��� ������
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
  /** ����������� ��������� ��������.
   * @param newDispatcher ������ �� ��������� ��������� �������� ��������� ��������
   */
  public StandartResourceManager(final Dispatcher newDispatcher) {
    dispatcher = newDispatcher;
    log.setLevel(java.util.logging.Level.ALL);
  }

  /** ��������� ������� �� ������ �������.
   * @param msg ��������� � �������
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
      synchronized (re) {
	// ������������ ������� ����. ���������������� ��������� �� ��� ������ � �������
	if (re.isAvailable()) {
	  log.fine(className + " resource is in free pool.");
	  // ��������� ������� �� ���������
	  Resource res = re.acquireResource(msg.getOrigin());
	  // ��������������� � �������� ���������
	  ODResourceAcquiredMessage m = new ODResourceAcquiredMessage(msg.getOrigin(), msg.getId());
	  m.setResourceName(className);
	  m.setResource(res);
	  dispatcher.send(m);
	  if (willBlockState) {
	    log.fine("acquitentance of resource " + className + " by " + msg.getOrigin() + " will be blocking");
	    // ���������� � ��������� ������� ���������� �������
	    re.setBlockState(res, willBlockState);
	    dispatcher.getObjectManager().setBlockedState(msg.getOrigin(),
							  dispatcher.getObjectManager().getBlockedState(msg.getOrigin()) + 1);
	  }
	} else {
	  log.finer(className + " resource busy -- putting request to queue");
	  resourceQueue.addRequest(className, new ResourceRequest(msg.getOrigin(), msg.getId(), willBlockState));
	}
      }
    }
  }

  public List statRequest() {
    List result = new ArrayList();
    String res = "";
    Iterator it = resources.keySet().iterator();
    while (it.hasNext()) {
      String resname = (String) it.next();
      res = ((ResourceEntry) resources.get(resname)).toString();
      result.add(res);
    }
    result.add(resourceQueue.toString());
    return result;
  }

  /** ��������� ������� �� ������������� �������.
   * @param msg ��������� � �������������
   */
  public final void releaseRequest(final Message msg) {
    if (msg.getFieldsCount() != 2) {
      return;
    }
    // ������ ���������
    String className = (String) msg.getField(0);
    Resource res = (Resource) msg.getField(1);
    synchronized (resourceQueue) {
      ResourceRequest rr = resourceQueue.nextRequest(className);
      if (rr != null) {
	log.fine(className + " has request in its queue.");
	ResourceEntry re = (ResourceEntry) resources.get(className);
	synchronized (re) {
	  // � ������� ���������������� � ������ � ������ � �������
	  if (re.isBlockState(res)) {
	    // ������ ���������� ���� ��� ����
	    log.fine("releasing block for object " + msg.getOrigin());
	    dispatcher.getObjectManager().setBlockedState(msg.getOrigin(),
							  dispatcher.getObjectManager().getBlockedState(msg.getOrigin()) - 1);
	  }
	  re.setUsedBy(res, rr.getObjectName());
	  if (rr.isBlockState()) {
	    // ���������� ������ ������� ���������� �������
	    log.finer("setting block for object " + rr.getObjectName());
	    re.setBlockState(res, rr.isBlockState());
	    dispatcher.getObjectManager().setBlockedState(msg.getOrigin(),
							  dispatcher.getObjectManager().getBlockedState(msg.getOrigin()) + 1);
	  }
	}
	// ���������� � ������� ��������� � ������� �������
	ODResourceAcquiredMessage m = new ODResourceAcquiredMessage(rr.getObjectName(), rr.getMsgId());
	m.setResourceName(className);
	m.setResource(res);
	dispatcher.send(m);
      } else {
	log.fine(className + " no requests in queue");
	// ���� ������ �� ��������� ���-�� ��� -- ������ ����������� ���
	ResourceEntry re = (ResourceEntry) resources.get(className);
	synchronized (re) {
	  // ���������� ������ �� ���� ������
	  if (re.isBlockState(res)) {
	    log.fine("releasing block for object " + msg.getOrigin());
	    dispatcher.getObjectManager().setBlockedState(msg.getOrigin(),
							  dispatcher.getObjectManager().getBlockedState(msg.getOrigin()) - 1);
	  }
	  re.releaseResource(res);
	}
      }
    }
  }

  /** ������ � ������� �� ������. */
  private class ResourceRequest {
    /** ��� ������. */
    private String object;
    /** ������ � ����� �������.
     * @return ��� �������
     */
    public String getObjectName() {
      return object;
    }
    /** ����� ��������� �������. */
    private int msgId;
    /** ������ � ������ ���������
     * @return ����� ���������
     */
    public int getMsgId() {
      return msgId;
    }
    /** ��������� ���������� ��� �������. */
    private boolean willBlockState;
    /** ������ � ��������� ����������.
     * @return ��������� ����������
     */
    public boolean isBlockState() {
      return willBlockState;
    }
    /** ����������� ����� ������.
     * @param newObject ��� �������
     * @param newMsgId ����� ���������
     * @param newWillBlockState ��������� ����������
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

  /** ����� ��������������� ������� �������� � ��������. */
  private class RequestQueue {
    private Map tempReqs = new HashMap();
    /** ������ ��������. */
    private Map resourceRequests = new HashMap();
    /** ���������� ������ ������� � �������
     * @param className ��� ������ �������
     * @param rr ������ � �������
     */
    public final void addRequest(final String className, final ResourceRequest rr) {
      List rrl;
      if (!tempReqs.containsKey(className)) {
	rrl = new ArrayList();
	tempReqs.put(className, rrl);
      } else {
	rrl = (List) tempReqs.get(className);
      }
      rrl.add(rr);
    }

    /** ������� ���������� ������� �� ������ �������.
     * @param className ��� �������
     * @return ���������� � ������� ��� null � ������ ���� �������� ���.
     */
    public final ResourceRequest nextRequest(final String className) {
      synchronized (tempReqs) {
	// ���������� �������� �� ��������� �������
	Iterator it = tempReqs.keySet().iterator();
	while (it.hasNext()) {
	  String key = (String) it.next();
	  List rrl = (List) tempReqs.get(key);
	  if (resourceRequests.containsKey(key)) {
	    ((List) resourceRequests.get(key)).addAll(rrl);
	  } else {
	    resourceRequests.put(key, rrl);
	  }
	}
	tempReqs.clear();
      }
      ResourceRequest result = null;
      if (resourceRequests.containsKey(className) 
	  && ((List) resourceRequests.get(className)).size() != 0) {
	List rrl = (List) resourceRequests.get(className);
	synchronized (rrl) {
	  try {
	    result = (ResourceRequest) rrl.get(0);
	    rrl.remove(0);
	  } catch (IndexOutOfBoundsException e) { /* �������� ���. */}
	}
      }
      return result;
    }

    public final String toString() {
      String result = "Acquire queue stats:\n";
      Iterator it = resourceRequests.keySet().iterator();
      while (it.hasNext()) {
	String className = (String) it.next();
	List rrl = (List) resourceRequests.get(className);
	result += "  resource " + className + " has " + rrl.size() + " requests from: ";
	Iterator rit = rrl.iterator();
	while (rit.hasNext()) {
	  ResourceRequest rr = (ResourceRequest) rit.next();
	  result+= rr.getObjectName() + (rr.isBlockState() ? "(blocking) " : " ");
	}
	result += "\n";
      }
      return result;
    }
  } // RequestQueue
} // ResourceManager
