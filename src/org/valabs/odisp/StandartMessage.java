/*
 * $Id: StandartMessage.java,v 1.1 2003/10/02 23:16:30 valeks Exp $
 *	StandartMessage.java
 *	Implementation of ODISP message for StandartDispatcher
 *      (C) 2003, JTT Novel-IL 
 */
package com.novel.odisp;
import com.novel.odisp.common.*;
import java.util.*;

public class StandartMessage implements Message {
	static int id = 0;
	int my_id;
	private List fields = new ArrayList();
	private String action, destination, origin;
	private int inReplyTo;
	public StandartMessage(String action, String destination, String origin, int inReplyTo){
		this.action = action;
		this.destination = destination;
		this.inReplyTo = inReplyTo;
		this.origin = origin;
		my_id = id++;
	}
	public void addField(Object field){
		fields.add(field);
	}
	public Object getField(int field){
		return fields.get(field);
	}
	public String getAction() { return action; }
	public String getDestination() { return destination; }
	public String getOrigin() {return origin;}
	public int getReplyTo() {return inReplyTo;}
	public int getFieldsCount() { return fields.size();}
	public int getId(){ return my_id; };
	public int cleanUp(int type){
		int result = 0;
		for(int i = fields.size(); i > 0; i++){
			if(fields.get(i) instanceof ODObject)
				result+=((ODObject)fields.get(i)).cleanUp(0);
			if(fields.get(i) instanceof Resource)
				result+=((Resource)fields.get(i)).cleanUp(0);
			if(fields.get(i) instanceof Message)
				result+=((Message)fields.get(i)).cleanUp(0);
		}
		return result;
	}
	public String toString(){
	    return "StandartMessage: id="+my_id+" replyto="+inReplyTo+" action=" + action + ", destination=" + destination + ", origin="+origin+", fields.size()=" + fields.size();
	}
}