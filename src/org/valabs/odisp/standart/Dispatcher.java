package com.novel.odisp;

import java.io.*;
import java.util.*;
import java.lang.reflect.*;
import java.util.regex.*;

import com.novel.odisp.common.*;

/** ����������� ��������� ODISP.
 * ����������� ��������� ��������� ��������� ��������� ����� ��������� ����
 * � ���������� ���������� ���������.
 * @author �������� �. ��������
 * @author (C) 2003, ��� "�����-��"
 * @version $Id: Dispatcher.java,v 1.9 2003/10/14 09:09:58 valeks Exp $
 */
public class StandartDispatcher implements Dispatcher {
	
	Map objects = new HashMap();
	Map resources = new HashMap();
	List provided = new ArrayList();
	List requested = new ArrayList();
	DefferedMessages messages = new DefferedMessages();
	int obj_count = 0;
	/** ������� ��������� ��������� ������� ��� ����� ������� �������� */
	private void loadPending(){
	    // resources
	    Iterator it = resources.keySet().iterator();
	    while(it.hasNext()){
		String od_n = (String)it.next();
		ResourceEntry re = (ResourceEntry)resources.get(od_n);
		if(re.loaded)
		    continue;
		re.loaded = true;
		System.out.println("[D] added resource provider "+re.className);
		provided.add(re.className);
	    }
	    it = objects.keySet().iterator();
	    while(it.hasNext()){
		String od_n = (String)it.next();
		ObjectEntry oe = (ObjectEntry)objects.get(od_n);
		if(oe.loaded)
		    continue;
		System.out.println("[D] trying to load object "+od_n);
		int requested = oe.depends.length;
		for(int i = 0;i<oe.depends.length;i++)
		    if(provided.contains(oe.depends[i]))
			requested--;
		if(requested == 0){
		    oe.object.start();
		    oe.loaded = true;
		    for(int i = 0;i<oe.provides.length; i++)
			if(!provided.contains(oe.provides[i])){
			    System.out.println("[D] added provider of "+oe.provides[i]);
			    provided.add(oe.provides[i]);
			}
		    System.out.println(" ok. loaded = " + od_n);
		}
	    }
	}
	/** ������������ �������� ��������� ��������
	    @param className ��� ������������ ������
	    @param mult ���������� ����������� ��������
	    @return void
	*/
	private void loadResource(String className, int mult){
	    System.err.print("\tloading resource "+className);
	    for(int i = 0; i < mult; i++){
		try {
		    Resource r = (Resource)Class.forName(className).newInstance();
		    ResourceEntry re = new ResourceEntry(className);
		    re.resource = r;
		    resources.put(className+":"+i, re);
		    System.out.print("+");
		} catch(ClassNotFoundException e){
		    System.err.println(" failed: "+e);
		} catch(InstantiationException e){
	    	    System.err.println(" failed: "+e);
		} catch(IllegalAccessException e){
	    	    System.err.println(" failed: "+e);
		}	    
	    }
	    System.out.println(" ok.");
	}
	/** �������� ���������� �������
	@param roName ��� ���������� �������
	@code ��� ������
	*/
	private void unloadResource(String roName, int code){
	    if(resources.containsKey(roName)){
		ResourceEntry res = (ResourceEntry)resources.get(roName);
		List dependingObjs = new ArrayList();
		Iterator it = objects.keySet().iterator();
		while(it.hasNext()){
		    String cl_n = (String)it.next();
		    String[] depends = ((ObjectEntry)objects.get(cl_n)).depends;
		    for(int i = 0;i<depends.length;i++)
			if(depends[i].equals(roName.substring(0, roName.length() - roName.indexOf(":"))) 
			    && !dependingObjs.contains(roName))
			    dependingObjs.add(cl_n);
		}
		if(code == 0){
		    it = dependingObjs.iterator();
		    while(it.hasNext())
			unloadObject((String)it.next(), code);
		}
		res.resource.cleanUp(code);
		resources.remove(roName);
	    }
	}
	/** ������������ �������� �������
	 * @param className ��� ������������ ������
	 * @return void
	 */
	private void loadObject(String className){
	    System.err.print("\tloading object "+className);
		try {
		    Object params[] = new Object[1];
		    params[0] = new Integer(obj_count++);
		    Class declParams[] = new Class[1];
		    declParams[0] = params[0].getClass();
		    ODObject load = (ODObject)Class.forName(className).getConstructor(declParams).newInstance(params);
		    Message m = getNewMessage("od_object_loaded",load.getObjectName(),"stddispatcher",0);
		    load.setDispatcher(this);
		    load.addMessage(m);
		    synchronized(objects){
			ObjectEntry oe = new ObjectEntry(className, false, load.getDepends(), load.getProviding());
			oe.object = load;
			objects.put(load.getObjectName(), oe);
		    }
		} catch(InvocationTargetException e){
		    System.err.println(" failed: "+e);
		} catch(NoSuchMethodException e){
		    System.err.println(" failed: "+e);
		} catch(ClassNotFoundException e){
		    System.err.println(" failed: "+e);
		} catch(InstantiationException e){
	    	    System.err.println(" failed: "+e);
		} catch(IllegalAccessException e){
	    	    System.err.println(" failed: "+e);
		} catch(IllegalArgumentException e){
	    	    System.err.println(" failed: "+e);
		}
	}
	/** �������������� �������� ������� �
	 * ����� �������� ������
	 * @param objectName ���������� ��� ������� ��� ��������
	 * @return void
	 */
	private void unloadObject(String objectName, int code){
	    if(objects.containsKey(objectName)){
		ObjectEntry oe = (ObjectEntry)objects.get(objectName);
		String[] provides = oe.provides;
		Iterator it = objects.keySet().iterator();
		List dependingObjs = new ArrayList();
		while(it.hasNext()){
		    String cl_n = (String)it.next();
		    String depends[] = ((ObjectEntry)objects.get(cl_n)).depends;
		    for(int i=0;i<provides.length;i++){
			for(int j=0;j<depends.length;j++)
			    if(provides[i].equals(depends[j]) && !dependingObjs.contains(cl_n))
				dependingObjs.add(cl_n);
		    }
		}
		ODObject obj = oe.object;
		Message m = getNewMessage("od_cleanup", objectName, "stddispatcher", 0);
		m.addField(new Integer(code));
		sendMessage(m);
		obj.interrupt();
		objects.remove(objectName);
		if(code == 0){
		    it = dependingObjs.iterator();
		    while(it.hasNext()){
			String cl_n = (String)it.next();
			if(objects.containsKey(cl_n)){
			    System.out.println("[D] removing "+objectName+"'s dependency "+cl_n);
			    unloadObject(cl_n, code);
			}
		    }
		}
		System.out.println("\tobject "+objectName+" unloaded");
	    }
	}
	/** ��������� ��� �������� ���� ��� ������� ���������.
	 * ��������� multicast �������� ���������
	 * @param message ��������� ��� �������
	 * @return void
	 */
	public void sendMessage(Message message){
	    if(message.getAction().length() == 0)
		return;
	    synchronized(objects){
		Iterator it = objects.keySet().iterator();
		while(it.hasNext()){
		    String cl_n = (String)it.next();
		    ObjectEntry oe = (ObjectEntry)objects.get(cl_n);
		    if(oe.blockedState || !oe.loaded){
			System.out.println("[D] deffered message for "+cl_n+" (loaded="+oe.loaded+")");
			messages.addMessage(cl_n, message);
			continue;
		    }
		    ODObject cl_send = oe.object;
		    cl_send.addMessage(message);
		    synchronized(cl_send){cl_send.notify();}
		}
	    }
	}
	/** ��������� ��� �������� ���� ��� ������� ���������.
	 * ��������� multicast �������� ���������� ���������
	 * @param messageList ������ ��������� ��� �������
	 * @return void
	 */
	public void sendMessages(Message[] messageList){
	    if(messageList.length == 0)
		return;
	    for(int i = 0;i<messageList.length;i++){
		Message message = messageList[i];
		if(message.getAction().length() == 0)
		    continue;
		synchronized(objects){
		    Iterator it = objects.keySet().iterator();
		    while(it.hasNext()){
			String cl_n = (String)it.next();
			ObjectEntry oe = (ObjectEntry)objects.get(cl_n);
			if(oe.blockedState || !oe.loaded){
			    System.out.println("[D] deffered message for "+cl_n+" (loaded="+oe.loaded+")");
			    messages.addMessage(cl_n, message);
			    continue;
			}
			ODObject cl_send = oe.object;
			cl_send.addMessage(message);
			synchronized(cl_send){cl_send.notify();}
		    }
		}
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
	public Message getNewMessage(String action, String destination, String origin, int inReplyTo){
	    return new StandartMessage(action, destination, origin, inReplyTo);
	}
	/** ��������� ������� ���������� ������� �� ������� */
	private void setBlockedState(String objName, boolean state){
	    if(!objects.containsKey(objName))
		return;
	    ((ObjectEntry)objects.get(objName)).blockedState = state;
	}
	/** ����� ���������� ��������� ��� ������ ���������� � ������� */
	private void flushDefferedMessages(String cl_n){
	    if(!objects.containsKey(cl_n))
		return;
	    ObjectEntry oe = (ObjectEntry)objects.get(cl_n);
	    if(!oe.loaded)
		return;
	    ODObject cl_send = oe.object;
	    cl_send.addMessages(messages.flush(cl_n));
	    synchronized(cl_send){cl_send.notify();}
	}
	/** ����������� ����������� �������������� ����� ��������
	 * �� ������ ������
	 * @param objs ������ �������� ��� ��������
	 */
	public StandartDispatcher(List objs){
	    System.err.println("[i] "+toString()+" starting up...");
	    StandartDispatcherHandler stdh = new StandartDispatcherHandler(new Integer(0));
	    ObjectEntry oe = new ObjectEntry(stdh.getClass().getName(), false, stdh.getDepends(), stdh.getProviding());
	    oe.object = stdh;
	    objects.put("stddispatcher", oe);
	    loadPending();
	    Iterator it = objs.iterator();
	    Pattern p = Pattern.compile("(o:|(r:)(\\d+:)?)(.+)");	    
	    while(it.hasNext()){
		int mult = 1;
		String cl_n = (String)it.next();
		Matcher m = p.matcher(cl_n);
		m.find();
		if(m.groupCount() == 4){
		    if(m.group(1).equals("o:"))
			loadObject(m.group(4));
		    if(m.group(1).startsWith("r:")){
			if(m.group(3) != null)
			    mult = new Integer(m.group(3).substring(0,m.group(3).length()-1)).intValue();
			loadResource(m.group(4), mult);
		    }
		    loadPending();
		}
	    }
	}
	/** ������� ��������� �� ������ � ������ ������������ ����������
	 */
	public static void usage(){
	    System.err.println("Usage: java com.novel.odisp.StandartDispatcher <file-with-list-of-ODobjects-to-load>");
	    System.exit(0);
	}
	/** ����� ����� � StandartDispatcher.
	 * @param args �� 0 ������ ��������� ��� ����� � ������������� �������, ������� ���������� ���������
	 */
	public static void main(String args[]){
	    if(args.length != 1)
		usage();
	    else {
		try {
		    BufferedReader cfg = new BufferedReader(new FileReader(args[0]));
		    List objs = new ArrayList();
		    String s;
		    while((s = cfg.readLine()) != null)
			if(!s.startsWith("#"))
			    objs.add(s);
		    new StandartDispatcher(objs);
		} catch (FileNotFoundException e){
		    System.err.println("[e] configuration file "+args[0]+" not found.");
		} catch (IOException e){
		    System.err.println("[e] unable to read configuration file.");
		}
	    }
	}

	/** ������ �� ������� � ������� �������� */
	private class ObjectEntry {
	    public boolean loaded;
	    public String className;
	    public boolean blockedState;
	    public ODObject object;
	    public String[] depends;
	    public String[] provides;
	    ObjectEntry(String cn, boolean bs, String[] depends, String[] provides){
		className = cn;
		blockedState = bs;
		this.depends = depends;
		for(int i = 0;i<depends.length;i++)
		System.out.println("[D] object "+cn+" depends on "+depends[i]);
		this.provides = provides;
	    }
	}
	/** ������ � ������� � ������� �������� */
	private class ResourceEntry {
	    public boolean loaded;
	    public String className;
	    public Resource resource;
	    ResourceEntry(String cn){
		loaded = false;
		className = cn;
	    }
	}
	/** ��������� ���������� ��������� */
	private class DefferedMessages {
	    Map queues = new HashMap();
	    public void addMessage(String objName, Message m){
		if(!queues.containsKey(objName)){
		    List messages = new ArrayList();
		    messages.add(m);
		    queues.put(objName, messages);
		} else 
		    ((List)queues.get(objName)).add(m);
	    }
	    public List flush(String objectName){
		if(queues.containsKey(objectName)){
		    List res = new ArrayList((List)queues.get(objectName));
		    queues.remove(objectName);
		    return res;
		} else
		    return new ArrayList();
	    }
	}
	/** ���������� ��������� ���������� */
	private class StandartDispatcherHandler extends CallbackODObject {
		public String name = "stddispatcher";
		public String[] getProviding(){
		    String res[] = new String[1];
		    res[0] = "stddispatcher";
		    return res;
		}
		protected void registerHandlers(){
		    addHandler("unload_object", new MessageHandler(){
			public void messageReceived(Message msg){
			    if(msg.getFieldsCount() != 1)
				return;
			    String name = (String)msg.getField(0);
			    unloadObject(name, 1);
			    objects.remove(name);
			}
		    });
		    addHandler("od_shutdown", new MessageHandler(){
			public void messageReceived(Message msg){
			    int exitCode = 0;
			    System.out.println("[i] "+toString()+" shutting down...");
			    if(msg.getFieldsCount() == 1)
				exitCode = ((Integer)msg.getField(0)).intValue();
			    unloadObject("stddispatcher", exitCode);
			}
		    });
		    addHandler("od_acquire", new MessageHandler(){
			public void messageReceived(Message msg){
			    if(msg.getFieldsCount()>0){
				String cl_n = (String)msg.getField(0);
				boolean willBlockState = false;
				if(msg.getFieldsCount() == 2)
				    willBlockState = ((Boolean)msg.getField(1)).booleanValue();
				Iterator it = resources.keySet().iterator();
				while(it.hasNext()){ // first hit
				    String cur_cl_n = (String)it.next();
				    if(Pattern.matches(cl_n+":\\d+",cur_cl_n) && ((ResourceEntry)resources.get(cur_cl_n)).loaded){
					Message m = getNewMessage("resource_acquired", msg.getOrigin(), "stddispatcher", msg.getId());
					m.addField(cur_cl_n);
					m.addField(((ResourceEntry)resources.get(cur_cl_n)).resource);
					resources.remove(cur_cl_n);
					sendMessage(m);
					setBlockedState(msg.getOrigin(), willBlockState);
					break;
				    }
				}
			    }
			}
		    });
		    addHandler("od_release", new MessageHandler(){
			public void messageReceived(Message msg){
			    if(msg.getFieldsCount() != 2)
				return;
			    String cl_n = (String)msg.getField(0);
			    Resource res = (Resource)msg.getField(1);
			    resources.put(cl_n, new ResourceEntry(cl_n.substring(0,cl_n.length() - cl_n.indexOf(":"))));
			    flushDefferedMessages(msg.getOrigin());
			    setBlockedState(msg.getOrigin(), false);
			}
		    });
		    addHandler("od_list_objects", new MessageHandler(){
			public void messageReceived(Message msg){
			    Message m = getNewMessage("object_list", msg.getOrigin(), "stddispatcher", msg.getId());
			    m.addField(new ArrayList(objects.keySet()));
			    sendMessage(m);
			}
		    });
		    addHandler("od_list_resources", new MessageHandler(){
			public void messageReceived(Message msg){
			    Message m = getNewMessage("resource_list", msg.getOrigin(), "stddispatcher", msg.getId());
			    m.addField(new ArrayList(resources.keySet()));
			    sendMessage(m);
			}
		    });
		    addHandler("od_remove_dep", new MessageHandler(){
			public void messageReceived(Message msg){
			    if(msg.getFieldsCount() != 1)
				return;
			    ObjectEntry oe = (ObjectEntry)objects.get(msg.getOrigin());
			    String[] deps = oe.depends;
			    String[] newDeps = new String[deps.length-1];
			    String toRemove = (String)msg.getField(0);
			    for(int i=0;i<deps.length;i++)
				if(!deps[i].equals(toRemove))
				    newDeps[i] = new String(deps[i]);
			    synchronized(oe.depends){
				oe.depends = newDeps;
			    }
			}
		    });
		}
		public int cleanUp(int type){
		    return 0;
		}
		public StandartDispatcherHandler(Integer id){
		    super("stddispatcher");
		}
	
	}
}