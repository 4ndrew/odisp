/*
 * $Id: Dispatcher.java,v 1.1 2003/10/02 23:16:30 valeks Exp $
 *	Dispatcher.java
 *	Common Dispatcher dispatcher interface. Defined as base interface
 *	for all classes that may be dispatcher that implements message
 * 	passing routines
 *      (C) 2003, JTT Novel-IL
 */
 
package com.novel.odisp.common;

public interface Dispatcher {
	public void sendMessage(Message message);
	public Resource getResourceObject();
	public Message getNewMessage(String action, String destination, String origin, int inReplyTo);
}