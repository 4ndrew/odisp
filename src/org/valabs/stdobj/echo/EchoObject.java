package com.novel.stdobj.echo;

import com.novel.odisp.common.CallbackODObject;
import com.novel.odisp.common.Message;
import com.novel.odisp.common.MessageHandler;

/** Простейший ODISP объект реализующий автоответчик на приходящие сообщения
* @author Валентин А. Алексеев
* @author (C) 2003, НПП "Новел-ИЛ"
* @version $Id: EchoObject.java,v 1.8 2003/11/15 19:46:00 valeks Exp $
*/
public class EchoObject extends CallbackODObject {
  protected void registerHandlers() {
    addHandler("od_cleanup", new MessageHandler() {
	public void messageReceived(Message msg) {
	  cleanUp(((Integer) msg.getField(0)).intValue());
	}
      });
    addHandler("echo", new MessageHandler() {
	public void messageReceived(Message msg) {
	  Message m = dispatcher.getNewMessage("echo_reply", msg.getOrigin(), getObjectName(), msg.getId());
	  for (int i = 0; i < msg.getFieldsCount(); i++) {
			m.addField(msg.getField(i));
	  }
	  dispatcher.sendMessage(m);
	}
      });
  }
  public int cleanUp(int type) {
    return 0;
  }
  public EchoObject(Integer id) {
    super("echo" + id);
  }
  public String[] getProviding() {
    String res[] = {"echo"};
    return res;
  }
  public String[] getDepends() {
    String res[] = {
      "stddispatcher",
      "log"
    };
    return res;
  }
}
