package com.novel.odisp.common;
import java.util.*;
import java.util.regex.*;

/** Базовый класс реализующий почтовый ящик для сообщений
* посылаемых диспетчером ODISP.
* @author Валентин А. Алексеев
* @author (С) 2003, НПП "Новел-ИЛ"
* @version $Id: PollingODObject.java,v 1.5 2003/10/22 21:21:33 valeks Exp $
*/
public abstract class PollingODObject extends ODObject {
	/** Конструктор задающий ODISP имя объекта */
	public PollingODObject(String name){
	    super(name);
	}
	/** Цикл обработки приходящих сообщений */
	public final void run(){
	    logger.finest("message processing loop started");
	    while(doExit!=true){
		List localMessages;
		synchronized(this){
		    try {
			wait(1000);
		    } catch(InterruptedException e){}
		}
		synchronized (messages) {
		    localMessages = new ArrayList(messages);
		    messages.clear();
		}
		if(localMessages != null && localMessages.size() > 0){
		    Iterator mIter = localMessages.iterator();
		    while(mIter.hasNext()){
			Message m = (Message)mIter.next();
			logger.finest("processing "+m);
			handleMessage(m);
		    }
		}
	    }
	}
	/** Интерфейс добавления сообщения в ящик */
	public final void addMessage(Message msg){
	    if(!Pattern.matches(match, msg.getDestination()))
		return;
	    synchronized (this) {messages.add(msg);}
//	    log("addMessage",""+msg);
	}
	/** Метод вызываемый для очистки данных класса */
	public int cleanUp(int type){
	    doExit = true;
	    return 0;
	}
}