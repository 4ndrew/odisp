package com.novel.odisp;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.lang.reflect.InvocationTargetException;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.logging.Logger;

import com.novel.odisp.common.Message;
import com.novel.odisp.common.ODObject;
import com.novel.odisp.common.Resource;
import com.novel.odisp.common.ProxyResource;
import com.novel.odisp.common.Dispatcher;
import com.novel.odisp.common.CallbackODObject; // ��� ������� stddispatcher
import com.novel.odisp.common.MessageHandler; // --''--
import com.novel.stdmsg.*;

/** ����������� ��������� ODISP.
 * ����������� ��������� ��������� ��������� ��������� ����� ��������� ����
 * � ���������� ���������� ���������.
 * @author �������� �. ��������
 * @author (C) 2003, ��� "�����-��"
 * @version $Id: Dispatcher.java,v 1.24 2004/01/16 14:31:57 valeks Exp $
 */
public class StandartDispatcher implements Dispatcher {
  /** ��������� � ������ ���������. */
  private static Logger log = Logger.getLogger("com.novel.odisp");
  /** ����� �������� ����������� �����������. */
  private Map objects = new HashMap();
  /** ����� �������� ����������. */
  private Map resources = new HashMap();
  /** ������ ������������� ������������ ����������. */
  private List provided = new ArrayList();
  /** ������ ��������� ������������. */
  private List requested = new ArrayList();
  /** ��������� ���������� ���������. */
  private DefferedMessages messages = new DefferedMessages();
  /** ���-�� �������� �������. */
  private int objCount = 0;
  /** ������� ��������� ��������� ������� ��� ����� ������� ��������. */
  private void loadPending() {
    // resources
    Iterator it = resources.keySet().iterator();
    while (it.hasNext()) {
      String objectName = (String) it.next();
      ResourceEntry re = (ResourceEntry) resources.get(objectName);
      if (re.isLoaded()) {
	continue;
      }
      re.setLoaded(true);
      log.fine("added resource provider " + objectName);
      provided.add(objectName.substring(0, objectName.lastIndexOf(":")));
    }
    it = objects.keySet().iterator();
    while (it.hasNext()) {
      String objectName = (String) it.next();
      ObjectEntry oe = (ObjectEntry) objects.get(objectName);
      if (oe.isLoaded()) {
	continue;
      }
      log.config("trying to load object " + objectName);
      int numRequested = oe.getDepends().length;
      for (int i = 0; i < oe.depends.length; i++) {
	if (provided.contains(oe.getDepends()[i])) {
	  numRequested--;
	} else {
	  log.finer("dependency not met: " + oe.getDepends()[i]);
	}
      }
      if (numRequested == 0) {
	oe.getObject().start();
	oe.setLoaded(true);
	for (int i = 0; i < oe.getProvides().length; i++) {
	  if (!provided.contains(oe.getProvides()[i])) {
	    log.fine("added provider of " + oe.getProvides()[i]);
	    provided.add(oe.getProvides()[i]);
	  }
	}
	log.config(" ok. loaded = " + objectName);
	Message m = new ODObjectLoadedMessage(objectName);
	oe.getObject().addMessage(m);
      }
    }
  }
  /** ������������ �������� ��������� ��������.
      @param className ��� ������������ ������
      @param mult ���������� ����������� ��������
      @param param �������� ��������
  */
  private void loadResource(final String className, final int mult, final String param) {
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
  /** �������� ���������� �������.
      @param name ��� ���������� �������
      @param code ��� ������
  */
  private void unloadResource(final String name, final int code) {
    if (resources.containsKey(name)) {
      ResourceEntry res = (ResourceEntry) resources.get(name);
      List dependingObjs = new ArrayList();
      Iterator it = objects.keySet().iterator();
      while (it.hasNext()) {
	String className = (String) it.next();
	String[] depends = ((ObjectEntry) objects.get(className)).depends;
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
	  unloadObject((String) it.next(), code);
	}
      }
      res.getResource().cleanUp(code);
      resources.remove(name);
    }
  }
  /** ������������ �������� ������� (� �ޣ��� ������������).
   * @param cName ��� ������������ ������
   */
  private void loadObject(final String cName) {
    log.config("loading object " + cName);
    try {
      Object[] params = new Object[1];
      params[0] = new Integer(objCount++);
      Class[] dParams = new Class[1];
      dParams[0] = params[0].getClass();
      ODObject load =
	(ODObject) Class.forName(cName).getConstructor(dParams).newInstance(params);
      load.setDispatcher(this);
      synchronized (objects) {
	ObjectEntry oe =
	  new ObjectEntry(cName, 0, load.getDepends(), load.getProviding());
	oe.object = load;
	oe.loaded = false;
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
  private void unloadObject(final String objectName, final int code) {
    if (objects.containsKey(objectName)) {
      ObjectEntry oe = (ObjectEntry) objects.get(objectName);
      String[] provides = oe.provides;
      Iterator it = objects.keySet().iterator();
      List dependingObjs = new ArrayList();

      while (it.hasNext()) {
	String className = (String) it.next();
	String[] depends = ((ObjectEntry) objects.get(className)).depends;
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
      ODObject obj = oe.object;
      ODCleanupMessage m = new ODCleanupMessage(objectName, 0);
      m.setReason(code);
      sendMessage(m);
      obj.interrupt();
      objects.remove(objectName);
      log.config("\tobject " + objectName + " unloaded");
    }
  }
  /** ��������� ��� �������� ���� ��� ������� ���������.
   * ��������� multicast �������� ���������
   * @param message ��������� ��� �������
   */
  public final void sendMessage(final Message message) {
    if (message.getAction().length() == 0 || !message.isCorrect()) {
      return;
    }
    synchronized (objects) {
      Iterator it = objects.keySet().iterator();
      while (it.hasNext()) {
	String className = (String) it.next();
	ObjectEntry oe = (ObjectEntry) objects.get(className);
	if (oe.isBlockedState() || !oe.loaded) {
	  log.finer("deffered message for " + className
		    + " (loaded=" + oe.loaded + ")");
	  messages.addMessage(className, message);
	  continue;
	}
	ODObject objToSendTo = oe.object;
	objToSendTo.addMessage(message);
	synchronized (objToSendTo) {
	  objToSendTo.notify();
	}
      }
    }
  }
  /** ��������� ��� �������� ���� ��� ������� ���������.
   * ��������� multicast �������� ���������� ���������
   * @param messageList ������ ��������� ��� �������
   */
  public final void sendMessages(final Message[] messageList) {
    if (messageList.length == 0) {
      return;
    }
    for (int i = 0; i < messageList.length; i++) {
      Message message = messageList[i];
      sendMessage(message);
    }
  }
  /** ��������� �������� ������ ��������� ��� �������� ���������� ����������
   * ���������.
   * @param action �������� ������� ����� ���������
   * @param destination ������� ���������
   * @param origin ����������� ���������
   * @param inReplyTo ������������� ��������� �� ������� ������������ �����
   * @return Message ��������� ���������
   */
  public final Message getNewMessage(final String action,
				     final String destination,
				     final String origin,
				     final int inReplyTo) {
    return new StandartMessage(action, destination, origin, inReplyTo);
  }

  /** ������� ����� ������ ���������.
   * @return ������ �� ���������
   */
  public final Message getNewMessage() {
    return new StandartMessage();
  }
  /** ��������� ������� ���������� ������� �� �������.
   * @param objName ��� �������
   * @param state ����� ������� ����������
   */
  private void setBlockedState(final String objName, final int state) {
    if (!objects.containsKey(objName)) {
      return;
    }
    ((ObjectEntry) objects.get(objName)).setBlockedState(state);
  }
  /** �������� ������ ���������� �������.
   * @param objName ��� �������
   * @return �������� ������ ����������
   */
  private int getBlockedState(final String objName) {
    if (!objects.containsKey(objName)) {
      return 0;
    }
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
    ODObject objectRef = oe.object;
    objectRef.addMessages(messages.flush(objectName));
    synchronized (objectRef) {
      objectRef.notify();
    }
    loadPending();
  }
  /** ����������� ����������� �������������� ����� ��������.
   * �� ������ ������
   * @param objs ������ �������� ��� ��������
   */
  public StandartDispatcher(final List objs) {
    log.info(toString() + " starting up...");
    StandartDispatcherHandler stdh
      = new StandartDispatcherHandler(new Integer(0));
    ObjectEntry oe
      = new ObjectEntry(stdh.getClass().getName(), 0, stdh.getDepends(), stdh.getProviding());
    oe.object = stdh;
    objects.put("stddispatcher", oe);
    loadPending();
    Iterator it = objs.iterator();
    Pattern p = Pattern.compile("(o:|(r:)(\\d+:)?)([^:]+)(:(.*))?");
    //                            type    mult     class  param
    while (it.hasNext()) {
      int mult = 1;
      String param = "";
      String className = (String) it.next();
      Matcher m = p.matcher(className);
      m.find();
      String parsedLine = "";
      for (int i = 0; i != m.groupCount(); i++) {
	parsedLine += i + "='" + m.group(i) + "' ";
      }
      log.finest(parsedLine);
      if (m.groupCount() == 6) {
	if (m.group(1).equals("o:")) {
			loadObject(m.group(4));
	}
	if (m.group(1).startsWith("r:")) {
	  if (m.group(3) != null) {
	    int len = m.group(3).length() - 1;
	    mult = new Integer(m.group(3).substring(0, len)).intValue();
	  }
	  if (m.group(5) != null) {
                            param = m.group(5).substring(1);
	  }
	  loadResource(m.group(4), mult, param);
	}
	loadPending();
      }
    }
  }
  /** ������� ��������� �� ������ � ������ ������������ ����������. */
  public static void usage() {
    log.severe("Usage: java com.novel.odisp.StandartDispatcher <config>");
    System.exit(0);
  }
  /** ����� ����� � StandartDispatcher.
   * @param args �� 0 ������ ��������� ��� ����� � ������������� �������,
   * ������� ���������� ���������
   */
  public static void main(final String[] args) {
    log.setLevel(java.util.logging.Level.ALL);
    if (args.length != 1) {
		usage();
    } else {
      try {
	BufferedReader cfg = new BufferedReader(new FileReader(args[0]));
	List objs = new ArrayList();
	String s;
	while ((s = cfg.readLine()) != null) {
	  if (!s.startsWith("#")) {
	    objs.add(s);
	  }
	}
	new StandartDispatcher(objs);
      } catch (FileNotFoundException e) {
	log.severe("[e] configuration file " + args[0] + " not found.");
      } catch (IOException e) {
	log.severe("[e] unable to read configuration file.");
      }
    }
  }

  /** ������ �� ������� � ������� ��������. */
  private class ObjectEntry {
    /** ���������� �������� �� ������. */
    private boolean loaded;
    /** �������� �������� �������.
     * @return ��������� ��������
     */
    public final boolean isLoaded() {
      return loaded;
    }
    /** ��������� �������� ��������.
     * @param newLoaded ����� �������� ���������
     */
    public final void setLoaded(final boolean newLoaded) {
      loaded = newLoaded;
    }

    /** ��� ������ ��� �������. */
    private String className;
    /** ������� ��� ������ ��� �������.
     * @return ��� ������
     */
    public final String getClassName() {
      return className;
    }
    /** ���������� ��� ������ ��� �������.
     * @param newClassName ����� ��� ������
     */
    public final void setClassName(final String newClassName) {
      className = newClassName;
    }
    /** ��������� ����������. */
    private int blockedState;
    /** ������� ��������� ���������� �������.
     * @return ��������� ����������
     */
    public final int getBlockedState() {
      return blockedState;
    }
    /** �������� ������������ ������ ��� ���.
     * @return ������ ����������
     */
    public final boolean isBlockedState() {
      return (blockedState > 0);
    }
    /** ���������� ��������� ����������.
     * @param newBlockedState ����� ��������� ����������
     */
    public final void setBlockedState(final int newBlockedState) {
      if (newBlockedState < 0) {
	blockedState = 0;
      } else {
	blockedState = newBlockedState;
      }
    }
    /** ������ �� ������. */
    private ODObject object;
    /** ������� ������ �� ������.
     * @return ������ �� ������
     */
    public final ODObject getObject() {
      return object;
    }
    /** ������ ������������. */
    private String[] depends;
    /** ������� ������ ������������.
     * @return ������ ������������
    */
    public final String[] getDepends() {
      return depends;
    }
    /** ������ ������������ ����������� �� ������.
     * @param toRemove �����������
     */
    public final void removeDepend(final String toRemove) {
      String[] newDeps = new String[depends.length - 1];
      for (int i = 0; i < depends.length; i++) {
	if (!depends[i].equals(toRemove)) {
	  newDeps[i] = new String(depends[i]);
	}
      }
      depends = newDeps;
    }
    /** ������ ��������. */
    private String[] provides;
    /** ������� ������ ��������.
     * @return ������ ��������
     */
    public final String[] getProvides() {
      return provides;
    }
    /** ����������� ������.
     * @param cn ��� ������
     * @param bs ����������� ��������� ����������
     * @param newDepends ������ ������������
     * @param newProvides ������ ��������
     */
    public ObjectEntry(final String cn,
		       final int bs,
		       final String[] newDepends,
		       final String[] newProvides) {
      className = cn;
      blockedState = bs;
      depends = newDepends;
      for (int i = 0; i < depends.length; i++) {
	log.fine("object " + cn + " depends on " + depends[i]);
      }
      provides = newProvides;
    }
  }
  /** ������ � ������� � ������� ��������. */
  private class ResourceEntry {
    /** ��������� ��������. */
    private boolean loaded;
    /** ������� ��������� ��������.
     * @return ��������� ��������
     */
    public final boolean isLoaded() {
      return loaded;
    }
    /** ���������� ��������� ��������.
     * @param newLoaded ����� �������� ���������
     */
    public final void setLoaded(final boolean newLoaded) {
      this.loaded = newLoaded;
    }

    /** ��� ������ �������. */
    private String className;
    /** ������ �� ������. */
    private Resource resource;
    /** ������� ������ �� ������.
     * @return ������ �� ������
     */
    public final Resource getResource() {
      return resource;
    }
    /** ���������� ������ �� ������.
     * @param newResource ����� �������� ������
     */
    public final void setResource(final Resource newResource) {
      resource = newResource;
    }
    /** ����������� ������.
     * @param cn ��� ������ �������
     */
    public ResourceEntry(final String cn) {
      loaded = false;
      className = cn;
    }
  }
  /** ��������� ���������� ���������. */
  private class DefferedMessages {
    /** ����� �������� ���������. */
    private Map queues = new HashMap();
    /** ���������� ��������� � ������������ �������.
     * @param objName ��� ������� (�������)
     * @param m ��������� ��� ����������
     */
    public final void addMessage(final String objName, final Message m) {
      if (!queues.containsKey(objName)) {
	List lmessages = new ArrayList();
	lmessages.add(m);
	queues.put(objName, lmessages);
      } else {
	((List) queues.get(objName)).add(m);
      }
    }
    /** ���������� ������ ����������� ��� ������� ���������.
     * @param objectName ��� ������� (�������)
     * @return ������ ��������� �� �������
     */
    public final List flush(final String objectName) {
      if (queues.containsKey(objectName)) {
	List res = new ArrayList((List) queues.get(objectName));
	queues.remove(objectName);
	return res;
      } else {
	return new ArrayList();
      }
    }
  }
  /** ���������� ��������� ����������. */
  private class StandartDispatcherHandler extends CallbackODObject {
    /** ����� �������� � ��������. */
    private Map resourceRequests = new HashMap();
    /** ��� �������. */
    private String name = "stddispatcher";
    /** ������� ������ ��������.
     * @return ������ ��������
     */
    public final String[] getProviding() {
      String[] res = {
	"stddispatcher"
      };
      return res;
    }
    /** ������� ������ ������������.
     * @return ������ ������������
     */
    public final String[] getDepends() {
      String[] res = {};
      return res;
    }
    /** ���������������� ����������� ���������. */
    protected final void registerHandlers() {
      addHandler("unload_object", new MessageHandler() {
	  public final void messageReceived(final Message msg) {
	    if (msg.getFieldsCount() != 1) {
	      return;
	    }
	    String objname = (String) msg.getField(0);
	    unloadObject(objname, 1);
	    objects.remove(objname);
	  }
	});
      addHandler("load_object", new MessageHandler() {
	  public final void messageReceived(final Message msg) {
	    if (msg.getFieldsCount() != 1) {
	      return;
	    }
	    String objname = (String) msg.getField(0);
	    loadObject(objname);
	    loadPending();
	  }
	});
      addHandler("od_shutdown", new MessageHandler() {
	  public final void messageReceived(final Message msg) {
	    int exitCode = 0;
	    log.info(toString() + " shutting down...");
	    if (msg.getFieldsCount() == 1) {
	      exitCode = ((Integer) msg.getField(0)).intValue();
	    }
	    unloadObject("stddispatcher", exitCode);
	  }
	});
      addHandler("od_acquire", new MessageHandler() {
	  public final void messageReceived(final Message msg) {
	    if (msg.getFieldsCount() > 0) {
	      String className = (String) msg.getField(0);
	      boolean willBlockState = false;
	      if (msg.getFieldsCount() == 2) {
		willBlockState = ((Boolean) msg.getField(1)).booleanValue();
	      }
	      Iterator it = resources.keySet().iterator();
	      boolean found = false;
	      while (it.hasNext()) { // first hit
		String curClassName = (String) it.next();
		if (Pattern.matches(className + ":\\d+", curClassName)
		    && ((ResourceEntry) resources.get(curClassName)).loaded) {
		  ODResourceAcquiredMessage m
		    = new ODResourceAcquiredMessage(msg.getOrigin(), msg.getId());
		  m.setResourceName(curClassName);
		  m.setResource(((ResourceEntry) resources.get(curClassName)).resource);
		  sendMessage(m);
		  if (willBlockState) {
		    setBlockedState(msg.getOrigin(), getBlockedState(msg.getOrigin()) + 1);
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
	  }
	});
      addHandler("od_release", new MessageHandler() {
	  public final void messageReceived(final Message msg) {
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
		setBlockedState(odObjectName, getBlockedState(odObjectName) + 1);
	      }
	      ODResourceAcquiredMessage m = new ODResourceAcquiredMessage(odObjectName, msg.getId());
	      m.setResourceName(className);
	      m.setResource(res);
	      sendMessage(m);
	    } else {
	      resources.put(className, new ResourceEntry(className.substring(0, className.length() - className.indexOf(":"))));
	    }
	    // decrease blocking state counter in case acquire was blocking
	    setBlockedState(msg.getOrigin(), getBlockedState(msg.getOrigin()) - 1);
	  }
	});
      addHandler("od_list_objects", new MessageHandler() {
	  public final void messageReceived(final Message msg) {
	    Message m = getNewMessage("object_list", msg.getOrigin(), "stddispatcher", msg.getId());
	    m.addField(new ArrayList(objects.keySet()));
	    sendMessage(m);
	  }
	});
      addHandler("od_list_resources", new MessageHandler() {
	  public final void messageReceived(final Message msg) {
	    Message m = getNewMessage("resource_list", msg.getOrigin(), "stddispatcher", msg.getId());
	    m.addField(new ArrayList(resources.keySet()));
	    sendMessage(m);
	  }
	});
      addHandler("od_remove_dep", new MessageHandler() {
	  public final void messageReceived(final Message msg) {
	    if (msg.getFieldsCount() != 1) {
	      return;
	    }
	    ObjectEntry oe = (ObjectEntry) objects.get(msg.getOrigin());
	    oe.removeDepend((String) msg.getField(0));
	  }
	});
    }
    /** ����� ������ �� �������.
     * @param type ������� ������
     * @return ��� ��������
     */
    public final int cleanUp(final int type) {
      return 0;
    }
    /** ����������� �������.
     * @param id ���������� ����� �������
     */
    public StandartDispatcherHandler(final Integer id) {
      super("stddispatcher");
    }
  }
}
