/*
 * $Id: ConsoleObject.java,v 1.1 2003/10/02 23:16:31 valeks Exp $
 *	CommonObject.java
 *	Common Dispatcher Object. Defined as base class
 *	that implements message passing routines
 *      (C) 2003, JTT Novel-IL 
 */
package com.novel.stdobj.console;
import com.novel.odisp.common.*;
import java.util.regex.Pattern;

public class ConsoleObject extends ODObject {
	public String name = "console";
	private ConsoleReader reader;
	public void handleMessage(Message msg){
            if(!Pattern.matches(msg.getDestination(), toString()))
                return;	
	    if(msg.getAction().equals("od_object_loaded"))
		reader = new ConsoleReader(this);
	    else if(msg.getAction().equals("od_cleanup"))
		cleanUp(((Integer)msg.getField(0)).intValue());
	    else {
		System.out.println("Received:");
		System.out.println(msg.toString());
		if(msg.getFieldsCount()>0)
		System.out.println("Fields dump:");
		for(int i = 0;i<msg.getFieldsCount();i++){
		    System.out.print(i+":");
		    System.out.println(msg.getField(i).toString());
		}
	    }
	    return;
	}
	public int cleanUp(int type){
	    if(reader != null)
		reader.interrupt();
	    return 0;
	}
	public String getName(){return name;}
}