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
 * @version $Id: ResourceManager.java,v 1.6 2004/02/17 11:00:26 valeks Exp $
 */
public class StandartResourceManager implements ResourceManager {
  /** ������ �������� �� �������. */
  private Map resourceRequests = new HashMap();
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
      if (re.isAvailable()) {
	log.fine(className + " resource is in free pool.");
	// ��������� ������� �� ���������
	Resource res = re.acquireResource();
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
	if (!resourceRequests.containsKey(className)) {
	  log.fine("created request queue for " + className);
	  resourceRequests.put(className, new ArrayList());
	}
	List rrl = (List) resourceRequests.get(className);
	rrl.add(new ResourceRequest(msg.getOrigin(), msg.getId(), willBlockState));
      }
    }
  }

  /** ��������� ������� �� ������������� �������.
   * @param msg ��������� � �������������
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
      // ���� ������ �� ��������� ���-�� ��� -- ������ ����������� ���
      ResourceEntry re = (ResourceEntry) resources.get(className);
      if (re.isBlockState(res)) {
	log.fine("releasing block for object " + msg.getOrigin());
	dispatcher.getObjectManager().setBlockedState(msg.getOrigin(),
						      dispatcher.getObjectManager().getBlockedState(msg.getOrigin()) - 1);
      }
      re.releaseResource(res);
    } else {
      log.fine(className + " requests in queue");
      // ���������� ������� (FIFO) -- ����� ��������� ��������
      List rrl = (List) resourceRequests.get(className);
      ResourceRequest rr = (ResourceRequest) rrl.get(0);
      ResourceEntry re = (ResourceEntry) resources.get(className);
      if (re.isBlockState(res)) {
	log.fine("releasing block for object " + msg.getOrigin());
	dispatcher.getObjectManager().setBlockedState(msg.getOrigin(),
						      dispatcher.getObjectManager().getBlockedState(msg.getOrigin()) - 1);
      }
      // ���������� � ������� ��������� � ������� �������
      ODResourceAcquiredMessage m = new ODResourceAcquiredMessage(rr.getObjectName(), rr.getMsgId());
      m.setResourceName(className);
      m.setResource(res);
      dispatcher.send(m);
      if (rr.isBlockState()) {
	log.finer("setting block for object " + rr.getObjectName());
	// ���������� ������ ������� ���������� �������
	re.setBlockState(res, rr.isBlockState());
	dispatcher.getObjectManager().setBlockedState(msg.getOrigin(),
						      dispatcher.getObjectManager().getBlockedState(msg.getOrigin()) + 1);
      }
      // �������� ������� �� ������ �������
      rrl.remove(0);
      log.finer("request removed from head of a queue");
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
} // ResourceManager
