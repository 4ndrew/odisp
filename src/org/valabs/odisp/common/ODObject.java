package com.novel.odisp.common;
import java.util.*;
import java.util.regex.*;

/** Базовый класс реализующий почтовый ящик для сообщений
* посылаемых диспетчером ODISP.
* @author Валентин А. Алексеев
* @author (С) 2003, НПП "Новел-ИЛ"
* @version $Id: ODObject.java,v 1.4 2003/10/07 11:05:20 valeks Exp $
*/
public abstract class ODObject extends Thread {
	protected Dispatcher dispatcher;
	protected List messages;
	/** Признак окончания работы основного цикла обработки сообщений */
	protected boolean doExit;
	/** Внутреннее имя объекта в ядре ODISP */
	public String name;
	/** Конструктор инициализирующий почтовый ящик*/
	public ODObject(String name){
	    messages = new ArrayList();
	    this.name = name;
	}
	/** Доступ к диспетчеру */
	protected Dispatcher getDispatcher(){
	    return dispatcher;
	}
	/** Возвращает внутреннее ODISP имя объекта */
	public String getObjectName(){
	    return name;
	}
	/** Устанавливает диспетчера для текущего объекта */
	public void setDispatcher(Dispatcher d){
	    this.dispatcher = d;
	}
	/** Интерфейс добавления сообщения в ящик */
	public abstract void addMessage(Message msg);
	/** Метод который вызывает для обработки пришедшего сообщения.
	* Подклассы обязаны реализовать этот метод для корректной работы.
	*/
	protected abstract void handleMessage(Message msg);
	/** Метод вызываемый для очистки данных класса */
	public abstract int cleanUp(int type);
	protected void log(String place, String msg){
	    if(false)
	    System.out.println("[D] "+getObjectName()+"."+place+": "+msg);
	}
}