/*
 * $Id: Dispatcher.java,v 1.1 2003/10/02 23:16:30 valeks Exp $
 *	StandartDispatcher.java
 *	Implementation of an ODISP dispatcher
 *      (C) 2003, JTT Novel-IL 
 */
package com.novel.odisp;

import java.io.*;
import java.util.*;
import java.lang.reflect.*;
import java.util.regex.*;

import com.novel.odisp.common.*;

public class StandartDispatcher extends ODObject implements Dispatcher, Runnable {
	Map objects = new HashMap();
	List messages = new ArrayList();
	int obj_count = 0;
	private void loadObject(String className){
	    System.err.print("  loading object "+className);
	    try {
		ODObject load = (ODObject)Class.forName(className).newInstance();
		load.setDispatcher(this);
		load.setQuant(obj_count++);
		Message m = getNewMessage("od_object_loaded",load.getName()+(obj_count-1),toString(),0);
		load.handleMessage(m);
		synchronized (objects){
		    objects.put(load.toString(),load);
		}
		System.err.println(" ok. loaded="+load.toString());
	    } catch(ClassNotFoundException e){
		System.err.println(" failed: "+e);
	    } catch(InstantiationException e){
	        System.err.println(" failed: "+e);
	        e.printStackTrace();
	    } catch(IllegalAccessException e){
	        System.err.println(" failed: "+e);
	    } catch(IllegalArgumentException e){
	        System.err.println(" failed: "+e);
	    }
	    new Thread(this, "StandartDispatcherMsgProcessor").start();
	}
	private synchronized void unloadObject(String objectName){
	    if(objects.containsKey(objectName)){
		ODObject hObj = (ODObject)objects.get(objectName);
		Message m = getNewMessage("od_cleanup", objectName, toString(), 0);
		m.addField(new Integer(1));
		hObj.handleMessage(m);
		objects.remove(objectName);
		System.gc();
	    }
	}
	public void run(){
	    try {
		while(true){
		    if(messages.size() != 0){
			List localMessages;
			Map localObjects;
			synchronized (messages){
			    localMessages = new ArrayList(messages);
			    messages.clear();
			}
			synchronized (objects){
			    localObjects = new HashMap(objects);
			}
			if(localMessages != null && localMessages.size() != 0){
			    Iterator mIter = localMessages.iterator();
			    while(mIter.hasNext()){
				Message toPost = (Message)mIter.next();
				System.out.println("[D] Delivering "+toPost);
				Iterator e = localObjects.keySet().iterator();
				while(e.hasNext()){
				    String name = (String)e.next();
	    			    ODObject next = (ODObject)objects.get(name);
				    if(next != null)
	    				next.handleMessage(toPost);
				}
			    }
			}
		    }
		    Thread.currentThread().sleep(1);
		}
	    } catch(InterruptedException e) { 
		System.out.println("[i] stopping dispatcher thread");
	    }
	}
	
	public StandartDispatcher(List objs){
	    System.err.println("[i] "+toString()+" starting up...");
	    objects.put(toString(), this);
	    Iterator it = objs.iterator();
	    while(it.hasNext()){
		String cl_n = (String)it.next();
		loadObject(cl_n);
	    }
	}
	public void sendMessage(Message message){
	    synchronized(messages){
		messages.add(message);
	    }
	    return;
	}
	public Resource getResourceObject(){
	    return null;
	}
	public int cleanUp(int type){
	    // we must cleanup all waiting messages...
	    synchronized (messages) {messages.clear();}
	    // we must cleanup all our objects...
	    Map localObjects;
	    synchronized (objects) {
		localObjects = new HashMap(objects);
	    }
	    Iterator objIt = localObjects.keySet().iterator();
	    while(objIt.hasNext()){
		String name = (String)objIt.next();
		if(name!=toString()) // avoid cleanup loop
		    unloadObject(name);
	    }
	    // we must stop thread...
	    Thread.currentThread().interrupt();
	    return 0;
	}
	public void handleMessage(Message message){
	    if(Pattern.matches(message.getDestination(),toString())){
		if(message.getAction().equals("list_objects")){
		    Message m = getNewMessage("object_list",message.getOrigin(),toString(),message.getId());
		    Set objlist = objects.keySet();
		    m.addField(objlist);
		    sendMessage(m);
		}
		if(message.getAction().equals("load_object")){
		    if(message.getFieldsCount() == 1)
			loadObject((String)message.getField(0));
		}
		if(message.getAction().equals("unload_object")){
		    if(message.getFieldsCount() == 1)
			unloadObject((String)message.getField(0));
		}
		if(message.getAction().equals("od_cleanup")) // we have to be destroyed
		    cleanUp(((Integer)message.getField(0)).intValue());
	    }
	    return;
	}
	public Message getNewMessage(String action, String destination, String origin, int inReplyTo){
	    return new StandartMessage(action, destination, origin, inReplyTo);
	}
	public String toString(){ return "dispatcher"; }
	public static void usage(){
	    System.err.println("Usage: java com.novel.odisp.StandartDispatcher <file-with-list-of-ODobjects-to-load>");
	    System.exit(0);
	}
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
}