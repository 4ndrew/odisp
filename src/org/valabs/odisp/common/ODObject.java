package com.novel.odisp.common;
import java.util.*;
import java.util.regex.*;

/** Базовый класс реализующий почтовый ящик для сообщений
* посылаемых диспетчером ODISP.
* @author Валентин А. Алексеев
* @author (С) 2003, НПП "Новел-ИЛ"
* @version $Id: ODObject.java,v 1.6 2003/10/12 20:03:33 valeks Exp $
*/
public abstract class ODObject extends Thread {
	/** Диспетчер работающий с этим объектом */
	protected Dispatcher dispatcher;
	/** Список сообщений к обработке */
	protected List messages;
	/** Признак окончания работы основного цикла обработки сообщений */
	protected boolean doExit;
	/** Regex маска принимаемых сообщений. По умолчанию инициализируется именем объекта. */
	protected String match;
	/** Внутреннее имя объекта в ядре ODISP */
	public String name;
	/** Изменить маску принимаемых сообщений
	    @param match новая маска
	    @return void
	*/
	protected void setMatch(String match){
	    this.match = match;
	}
	/** Конструктор инициализирующий почтовый ящик
	@param name имя объекта
	*/
	public ODObject(String name){
	    messages = new ArrayList();
	    this.name = name;
	    this.match = name;
	}
	/** Доступ к диспетчеру 
	@return ссылка на диспетчер
	*/
	protected Dispatcher getDispatcher(){
	    return dispatcher;
	}
	/** Возвращает внутреннее ODISP имя объекта 
	@return ODISP имя объекта
	*/
	public String getObjectName(){
	    return name;
	}
	/** Устанавливает диспетчера для текущего объекта 
	@param d диспетчер работающий с этим объектом
	@return void
	*/
	public void setDispatcher(Dispatcher d){
	    this.dispatcher = d;
	}
	/** Интерфейс добавления сообщения в ящик 
	@param msg сообщение для добавления
	@return void
	*/
	public abstract void addMessage(Message msg);
	/** Добавление списка сообщений в ящик 
	@param messages список сообщений для добавления
	@return void
	*/
	public void addMessages(List messages){
	    Iterator it = messages.iterator();
	    while(it.hasNext())
		addMessage((Message)it.next());
	}	
	/** Метод который вызывает для обработки пришедшего сообщения.
	Подклассы обязаны реализовать этот метод для корректной работы.
	@param msg сообщение для обработки
	@return void
	*/
	protected abstract void handleMessage(Message msg);
	/** Метод вызываемый для очистки данных класса 
	@param type условие выхода
	@return void
	*/
	public abstract int cleanUp(int type);
	/** Вывод журнальных сообщений на экран
	@param place участок кода к которому относится сообщение
	@param msg сообщение
	*/
	protected void log(String place, String msg){
	    if(false)
		System.out.println("[D] "+getObjectName()+"."+place+": "+msg);
	}
}