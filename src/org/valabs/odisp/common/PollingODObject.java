package com.novel.odisp.common;
import java.util.*;
import java.util.regex.*;

/** Базовый класс реализующий почтовый ящик для сообщений
* посылаемых диспетчером ODISP.
* @author Валентин А. Алексеев
* @author (С) 2003, НПП "Новел-ИЛ"
* @version $Id: PollingODObject.java,v 1.1 2003/10/07 11:11:00 valeks Exp $
*/
public abstract class PollingODObject extends ODObject {
	/** Цикл обработки приходящих сообщений */
	public PollingODObject(String name){
	    super(name);
	}
	public final void run(){
	    log("run","message processing loop started");
	    while(doExit!=true){
		List localMessages;
		synchronized (messages) {
		    localMessages = new ArrayList(messages);
		    messages.clear();
		}
		if(localMessages != null && localMessages.size() > 0){
		    Iterator mIter = localMessages.iterator();
		    while(mIter.hasNext()){
			Message m = (Message)mIter.next();
			log("run","processing "+m);
			handleMessage(m);
		    }
		}
		synchronized(this){
		    try {
			wait();
		    } catch(InterruptedException e){}
		}
	    }
	}
	/** Интерфейс добавления сообщения в ящик */
	public final void addMessage(Message msg){
	    if(!Pattern.matches(msg.getDestination(), getObjectName()))
		return;
	    synchronized (this) {messages.add(msg);}
	    log("addMessage",""+msg);
	}
	/** Метод вызываемый для очистки данных класса */
	public int cleanUp(int type){
	    doExit = true;
	    return 0;
	}
}