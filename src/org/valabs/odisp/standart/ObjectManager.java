package com.novel.odisp;

import com.novel.odisp.common.ObjectManager;

import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Logger;
import com.novel.odisp.common.Dispatcher;
import com.novel.odisp.common.ODObject;
import com.novel.odisp.common.Message;
import com.novel.stdmsg.ODObjectLoadedMessage;
import com.novel.stdmsg.ODCleanupMessage;
import java.lang.reflect.InvocationTargetException;

/** �������� �������� ODISP.
 * @author (C) 2004 <a href="mailto:valeks@valeks.novel.local">Valentin A. Alekseev</a>
 * @version $Id: ObjectManager.java,v 1.8 2004/03/18 11:20:12 valeks Exp $
 */

public class StandartObjectManager implements ObjectManager {
  /** ��������� ��������. */
  private Dispatcher dispatcher;
  /** ��������� ���������� ���������. */
  private DefferedMessages messages = new DefferedMessages();
  /** ������ ��������. */
  private Map objects = new HashMap();
  /** ������. */
  private Logger log = Logger.getLogger("com.novel.odisp.StandartObjectManager");
  /** ������ �������� ���������. */
  private List provided = new ArrayList();
  /** ����� ����� ��������. */
  private int objCount = 0;
  /** ���������� �������� ��������� ��������. */
  private int countPending = 0;
  /** ������� ��������� �������� � ��������� ��������� ������ �������� ���������. */
  public final void loadPending() {
    if (countPending == 0) {
      // ��������� ��������� �������� ������ � ��� ������ ���� ���-�� ������� ��������
      return;
    }
    // resources
    Map resourceList = new HashMap(dispatcher.getResourceManager().getResources());
    Iterator it = resourceList.keySet().iterator();
    while (it.hasNext()) {
      String objectName = (String) it.next();
      log.fine("added resource provider " + objectName);
      provided.add(objectName);
    }
    synchronized (objects) {
      it = objects.keySet().iterator();
      while (it.hasNext()) {
	String objectName = (String) it.next();
	ObjectEntry oe = (ObjectEntry) objects.get(objectName);
	if (oe.isLoaded()) {
	  continue;
	}
	log.config("trying to load object " + objectName);
	int numRequested = oe.getDepends().length;
	for (int i = 0; i < oe.getDepends().length; i++) {
	  if (provided.contains(oe.getDepends()[i])) {
	    numRequested--;
	  } else {
	    log.finer("dependency not met: " + oe.getDepends()[i]);
	  }
	}
	if (numRequested == 0) {
	  oe.getObject().start();
	  oe.setLoaded(true);
          flushDefferedMessages(oe.getObject().getName());
	  for (int i = 0; i < oe.getProvides().length; i++) {
	    if (!provided.contains(oe.getProvides()[i])) {
	      log.fine("added provider of " + oe.getProvides()[i]);
	      provided.add(oe.getProvides()[i]);
	    }
	  }
	  log.config(" ok. loaded = " + objectName);
	  Message m = new ODObjectLoadedMessage(objectName);
	  oe.getObject().addMessage(m);
	  countPending--;
	}
      }
    }
  }

  /** ������������ �������� ������� (� �ޣ��� ������������).
   * @param cName ��� ������������ ������
   */
  public final void loadObject(final String cName) {
    log.config("loading object " + cName);
    try {
      Object[] params = new Object[1];
      params[0] = new Integer(objCount++);
      Class[] dParams = new Class[1];
      dParams[0] = params[0].getClass();
      ODObject load =
	(ODObject) Class.forName(cName).getConstructor(dParams).newInstance(params);
      load.setDispatcher(dispatcher);
      synchronized (objects) {
	ObjectEntry oe =
	  new ObjectEntry(cName, 0, load.getDepends(), load.getProviding());
	oe.setObject(load);
	oe.setLoaded(false);
	objects.put(load.getObjectName(), oe);
      }
    } catch (InvocationTargetException e) {
      log.warning(" failed: " + e);
    } catch (NoSuchMethodException e) {
      log.warning(" failed: " + e);
    } catch (ClassNotFoundException e) {
      log.warning(" failed: " + e);
    } catch (InstantiationException e) {
      log.warning(" failed: " + e);
    } catch (IllegalAccessException e) {
      log.warning(" failed: " + e);
    } catch (IllegalArgumentException e) {
      log.warning(" failed: " + e);
    }
    countPending++;
  }

  /** �������������� �������� ������� � ����� ��������.
   * ������, ��� �� ����������� �����������:
   * <ul>
   * <li> ����������� ������ ��������� ��������
   * <li> �������� ��������� ��������
   * <li> �������� ������ �������
   * </ul>
   * @param objectName ���������� ��� ������� ��� ��������.
   * @param code ��� ������ (��� code != 0 ��������� �������
   * �� ���������).
   */
  public final void unloadObject(final String objectName, final int code) {
    if (objects.containsKey(objectName)) {
      ObjectEntry oe = (ObjectEntry) objects.get(objectName);
      String[] provides = oe.getProvides();
      Iterator it = objects.keySet().iterator();
      List dependingObjs = new ArrayList();

      while (it.hasNext()) {
	String className = (String) it.next();
	String[] depends = ((ObjectEntry) objects.get(className)).getDepends();
	for (int i = 0; i < provides.length; i++) {
	  for (int j = 0; j < depends.length; j++) {
	    if (provides[i].equals(depends[j])
		&& !dependingObjs.contains(className)) {
	      dependingObjs.add(className);
	    }
	  }
	}
      }
      if (code == 0) {
	it = dependingObjs.iterator();
	while (it.hasNext()) {
	  String className = (String) it.next();
	  if (objects.containsKey(className)) {
	    log.fine("removing " + objectName + "'s dependency " + className);
	    unloadObject(className, code);
	  }
	}
      }
      ODObject obj = oe.getObject();
      ODCleanupMessage m = new ODCleanupMessage(objectName, 0);
      m.setReason(code);
      dispatcher.send(m);
      obj.interrupt();
      objects.remove(objectName);
      log.config("\tobject " + objectName + " unloaded");
    }
  }

  /** ������ � ������ ��������. 
   * @return ������ ��������
   */
  public final Map getObjects() {
    return objects;
  }

  /** ����������� ���������.
   * @param newDispatcher ��������� ��� �������� ������������ ���������� ���������
   */
  public StandartObjectManager(final Dispatcher newDispatcher) {
    dispatcher = newDispatcher;
  }

  /** ������� ��������� ���� �������� ���������.
   * @param message ���������
   */
  public final void send(Message message) {
    Map localObjects;
    synchronized (objects) {
      localObjects = new HashMap(objects);
    }
    if (message == null
	|| message.getAction().length() == 0
	|| !message.isCorrect()) {
      return;
    }
    Iterator it = localObjects.keySet().iterator();
    while (it.hasNext()) {
      String className = (String) it.next();
      ObjectEntry oe = (ObjectEntry) localObjects.get(className);
      if (oe.isBlockedState() || !oe.isLoaded()) {
	log.finest("deffered message " + message.getAction() + " for " + className
		  + " (loaded=" + oe.isLoaded() + ")");
	messages.addMessage(className, message);
	continue;
      }
      ODObject objToSendTo = oe.getObject();
      objToSendTo.addMessage(message);
      synchronized (objToSendTo) {
	objToSendTo.notify();
      }
    }
  }
  /** ��������� ������� ���������� ������� �� �������.
   * @param objName ��� �������
   * @param state ����� ������� ����������
   */
  public final void setBlockedState(final String objName, final int state) {
    if (!objects.containsKey(objName)) {
      return;
    }
    ObjectEntry oe = (ObjectEntry) objects.get(objName);
    oe.setBlockedState(state);
    if (oe.getBlockedState() == 0) {
      flushDefferedMessages(objName);
    }
  }
  /** �������� ������ ���������� �������.
   * @param objName ��� �������
   * @return �������� ������ ����������
   */
  public final int getBlockedState(final String objName) {
    assert !objects.containsKey(objName);
    return ((ObjectEntry) objects.get(objName)).getBlockedState();
  }
  /** ����� ���������� ��������� ��� ������ ���������� � �������.
   * @param objectName ��� �������
   */
  private void flushDefferedMessages(final String objectName) {
    if (!objects.containsKey(objectName)) {
		return;
    }
    ObjectEntry oe = (ObjectEntry) objects.get(objectName);
    if (!oe.isLoaded()) {
		return;
    }
    ODObject objectRef = oe.getObject();
    objectRef.addMessages(messages.flush(objectName));
    synchronized (objectRef) {
      objectRef.notify();
    }
    loadPending();
  }
} // StandartObjectManager
