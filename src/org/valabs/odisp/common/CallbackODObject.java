package com.novel.odisp.common;
import java.util.*;
import java.util.regex.*;

/** Базовый класс реализующий почтовый ящик для сообщений
* посылаемых диспетчером ODISP и вызывающий обработчик сообщения по мере прихода.
* @author Валентин А. Алексеев
* @author (С) 2003, НПП "Новел-ИЛ"
* @version $Id: CallbackODObject.java,v 1.2 2003/10/12 20:02:49 valeks Exp $
*/
public abstract class CallbackODObject extends ODObject {
	private Map handlers; 
	/** Признак окончания работы основного цикла обработки сообщений */
	public CallbackODObject(String name){
	    super(name);
	    handlers = new HashMap();

	}
	/** Добавление нового обработчика событий
	@param message сообщение обрабатываемое обработчиком
	@param handler класс-обработчик сообщения	
	*/
	protected void addHandler(String message, MessageHandler handler){
	    if(handlers.containsKey(message))
		return;
	    log("addHandler","registered handler for "+message);
	    handlers.put(message, handler);
	}
	/** Цикл обработки приходящих сообщений */
	public final void run(){
	    registerHandlers();	
	    // we do not need message loop.
	}
	/** Интерфейс добавления сообщения в ящик */
	public final void addMessage(Message msg){
	    if(!Pattern.matches(match, msg.getDestination()))
		return;
	    handleMessage(msg);
	}
	protected final void handleMessage(Message msg){
	    if(handlers.containsKey(msg.getAction())){
		((MessageHandler)handlers.get(msg.getAction())).messageReceived(msg);
	    } else {
		log("handleMessage","there is no handler for message "+msg.getAction());
	    }
	}
	/** Метод вызываемый для очистки данных класса */
	public int cleanUp(int type){
	    doExit = true;
	    return 0;
	}
	/** Наследующие классы должны реализовывать этот метод для регистрации обработчиков  */
	protected abstract void registerHandlers();
}