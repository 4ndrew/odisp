/*
 * $Id: ODObject.java,v 1.1 2003/10/02 23:16:30 valeks Exp $
 *	ODObject.java
 *	Common Dispatcher Object. Defined as base class
 *	that implements message passing routines
 *      (C) 2003, JTT Novel-IL
 */
package com.novel.odisp.common;

public class ODObject {
	protected Dispatcher dispatcher;
	public String name;
	protected int quant = 0;
	public void setQuant(int quant){
	    this.quant = quant;
	}
	public String getName(){
	    return name;
	}
	public void setDispatcher(Dispatcher d){
	    this.dispatcher = d;
	}
	public Dispatcher getDispatcher(){
	    return dispatcher;
	}
	public String toString(){
	    return getName()+quant;
	}
	public void handleMessage(Message msg){return;};
	public int cleanUp(int type){return 0;};
}