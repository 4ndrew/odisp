package com.novel.stdobj.echo;
import com.novel.odisp.common.*;
import java.util.regex.Pattern;

/** Простейший ODISP объект реализующий автоответчик на приходящие сообщения
* @author Валентин А. Алексеев
* @author (C) 2003, НПП "Новел-ИЛ"
* @version $Id: EchoObject.java,v 1.3 2003/10/07 11:14:38 valeks Exp $
*/
public class EchoObject extends CallbackODObject {
	public String name = "echo";
	protected void registerHandlers(){
	    addHandler("od_object_created", new MessageHandler(){public void messageReceived(Message msg){}});
	    addHandler("od_cleanup", new MessageHandler(){
		public void messageReceived(Message msg){cleanUp(((Integer)msg.getField(0)).intValue());}
	    });
	    addHandler("echo", new MessageHandler(){
		public void messageReceived(Message msg){
		    Message m = dispatcher.getNewMessage("echo_reply",msg.getOrigin(),getObjectName(),msg.getId());
		    for(int i = 0;i<msg.getFieldsCount();i++)
			m.addField(msg.getField(i));
		    log("handleMessage[echo_reply]",""+m);
		    dispatcher.sendMessage(m);
		}
	    });
	}
	public int cleanUp(int type){
	    return 0;
	}
	public EchoObject(Integer id){
	    super("echo"+id);
	}
}