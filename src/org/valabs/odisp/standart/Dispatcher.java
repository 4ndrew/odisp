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
 * @version $Id: Dispatcher.java,v 1.3 2003/10/04 12:53:05 valeks Exp $
 */
public class StandartDispatcher implements Dispatcher {
	Map objects = new HashMap();
	List messages = new ArrayList();
	int obj_count = 0;
	/** ������������ �������� �������
	 * @param className ��� ������������ ������
	 * @return void
	 */
	private void loadObject(String className){
	    System.err.print("  loading object "+className);
	    try {
		ODObject load = (ODObject)Class.forName(className).newInstance();
		load.setDispatcher(this);
		load.setQuant(obj_count++);
		Message m = getNewMessage("od_object_loaded",load.getObjectName()+(obj_count-1),toString(),0);
		load.addMessage(m);
		synchronized (objects){
		    objects.put(load.toString(),load);
		}
		System.err.println(" ok. loaded="+load.toString());
		load.start();
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
	}
	/** �������������� �������� ������� �
	 * ����� �������� ������
	 * @param objectName ���������� ��� ������� ��� ��������
	 * @return void
	 */
	private void unloadObject(String objectName){
	    if(objects.containsKey(objectName)){
		ODObject obj = (ODObject)objects.get(objectName);
		Message m = getNewMessage("od_cleanup",toString(), obj.toString(), 0);
		m.addField(new Integer(1));
		obj.addMessage(m);
		objects.remove(objectName); 
		/* ������ ��� ��� ����� ������������, �� ��������� ��� ����������� ��� �� ����� */
	    }
	}
	/** ��������� ��� �������� ���� ��� ������� ���������.
	 * ��������� multicast �������� ���������
	 * @param message ��������� ��� �������
	 * @return void
	 */
	public void sendMessage(Message message){
	    Iterator it = objects.keySet().iterator();
	    while(it.hasNext())
		((ODObject)it.next()).addMessage(message);
	}
	/** ����������� ����������� �������������� ����� ��������
	 * �� ������ ������
	 * @param objs ������ �������� ��� ��������
	 */
	public StandartDispatcher(List objs){
	    System.err.println("[i] "+toString()+" starting up...");
	    objects.put(toString(), this);
	    Iterator it = objs.iterator();
	    while(it.hasNext()){
		String cl_n = (String)it.next();
		loadObject(cl_n);
	    }
	}
	/** ��������� �������� ������ ��������� ��� �������� ���������� ����������
	 * ���������.
	 * @param action �������� ������� ����� ���������
	 * @param destination ������� ��������� ({@link java.util.regex.Pattern ���.���.})
	 * @param origin ����������� ���������
	 * @param inReplyTo ������������� ��������� �� ������� ������������ �����
	 * @return Message ��������� ���������
	 */
	public Message getNewMessage(String action, String destination, String origin, int inReplyTo){
	    return new StandartMessage(action, destination, origin, inReplyTo);
	}
	/** ���������� �������. ������������ ��� �������� ���������
	 * @return String ���������� ��� �������
	 */
	public String toString(){ return "dispatcher"; }
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
}