/*
 * $Id: EchoObject.java,v 1.1 2003/10/02 23:16:31 valeks Exp $
 *	EchoObject.java
 *	Simple testing object that reflects any message it receives
 *      (C) 2003, JTT Novel-IL
 */
package com.novel.stdobj.echo;
import com.novel.odisp.common.*;
import java.util.regex.Pattern;

public class EchoObject extends ODObject {
	public String name = "echo";
	public void handleMessage(Message msg){
            if(!Pattern.matches(msg.getDestination(), toString()))
                return;	
	    if(msg.getAction().equals("od_cleanup"))
		cleanUp(((Integer)msg.getField(0)).intValue());
	    else {
		Message m = dispatcher.getNewMessage("echo_reply",msg.getOrigin(),this.toString(),msg.getId());
		for(int i = 0;i<msg.getFieldsCount();i++)
		    m.addField(msg.getField(i));
		dispatcher.sendMessage(m);
	    }
	    return;
	}
	public int cleanUp(int type){
	    return 0;
	}
	public String getName(){return name;}
}