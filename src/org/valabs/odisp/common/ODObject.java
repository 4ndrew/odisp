package com.novel.odisp.common;
import java.util.*;

/** Базовый класс реализующий почтовый ящик для сообщений
* посылаемых диспетчером ODISP.
* @author Валентин А. Алексеев
* @author (С) 2003, НПП "Новел-ИЛ"
* @version $Id: ODObject.java,v 1.3 2003/10/06 12:38:12 valeks Exp $
*/
public abstract class ODObject extends Thread {
	protected Dispatcher dispatcher;
	private List messages;
	/** Признак окончания работы основного цикла обработки сообщений */
	private boolean doExit;
	/** Уникальный идентификатор экземпляра объекта*/
	protected int quant = 0;
	/** Внутреннее имя объекта в ядре ODISP */
	public String name;
	/** Конструктор инициализирующий почтовый ящик*/
	public ODObject(){
	    messages = new ArrayList();
	}
	/** Доступ к диспетчеру */
	protected Dispatcher getDispatcher(){
	    return dispatcher;
	}
	/** Установка индекса объекта для обеспечения работоспособности нескольких одинаковых объектов*/
	public void setQuant(int quant){
	    this.quant = quant;
	}
	/** Возвращает внутреннее ODISP имя объекта */
	public String getObjectName(){
	    return name;
	}
	/** Устанавливает диспетчера для текущего объекта */
	public void setDispatcher(Dispatcher d){
	    this.dispatcher = d;
	}
	/** Возвращает полное имя ODISP объекта */
	public String toString(){
	    return getObjectName()+quant;
	}
	/** Цикл обработки приходящих сообщений */
	public final void run(){
	    while(doExit!=true){
		try {
		    wait();
		} catch(InterruptedException e){}
		List localMessages;
		synchronized (messages) {
		    localMessages = new ArrayList(messages);
		}
		if(localMessages != null && localMessages.size() > 0){
		    Iterator mIter = localMessages.iterator();
		    while(mIter.hasNext())
			handleMessage((Message)mIter.next());
		}
	    }
	}
	/** Интерфейс добавления сообщения в ящик */
	public final void addMessage(Message msg){
	    synchronized (messages) {
		messages.add(msg);
	    }
	}
	/** Метод который вызывает для обработки пришедшего сообщения.
	* Подклассы обязаны реализовать этот метод для корректной работы.
	*/
	public abstract void handleMessage(Message msg);
	/** Метод вызываемый для очистки данных класса */
	public int cleanUp(int type){
	    doExit = true;
	    return 0;
	}
}