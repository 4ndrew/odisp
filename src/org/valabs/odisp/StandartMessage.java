package com.novel.odisp;
import com.novel.odisp.common.*;
import java.util.*;

/** Реализация стандартного сообщения для стандартного диспетчера ODISP
 * @author Валентин А. Алексеев
 * @author (C) 2003, НПП "Новел-ИЛ"
 * @version $Id: StandartMessage.java,v 1.4 2003/10/12 20:01:43 valeks Exp $
 */
public class StandartMessage implements Message {
	/** Внутренний уникальный счетчик сообщения */
	static int id = 0;
	private int my_id;
	private List fields = new ArrayList();
	private String action, destination, origin;
	private int inReplyTo;
	/** Реализация конструктора сообщения
	 * @param action действие которое несет сообщение
	 * @param destination адресат сообщения
	 * @param origin отправитель сообщения
	 * @param inReplyTo идентификатор сообщения на которое производится ответ
	 */
	public StandartMessage(String action, String destination, String origin, int inReplyTo){
		this.action = action;
		this.destination = destination;
		this.inReplyTo = inReplyTo;
		this.origin = origin;
		my_id = id++;
	}
	/** Добавление произвольного объекта в тело сообщения
	 * @param field объект который будет добавлен сообщение
	 */
	public void addField(Object field){
		fields.add(field);
	}
	/** Выборка сохраненного в теле сообщения объекта по индексу
	 * @param field индекс объекта
	 */
	public Object getField(int field){
		return fields.get(field);
	}
	/** Возвращает действие которое несет сообщение */
	public String getAction() { return action; }
	/** Возвращает адресата сообщения */
	public String getDestination() { return destination; }
	/** Возвращает автора сообщения */
	public String getOrigin() {return origin;}
	/** Возвращает идентификатор сообщения на которое производится ответ */
	public int getReplyTo() {return inReplyTo;}
	/** Возвращает количество полей в сообщении */
	public int getFieldsCount() { return fields.size();}
	/** Возвращает уникальный идентификатор сообщения */
	public int getId(){ return my_id; };
	/** Представление сообщения в виде текстовой строки 
	 * @return строчное представление сообщения
	*/
	public String toString(){
	    return "stdmessage id="+my_id+" replyto="+inReplyTo+" action=" + action + ", destination=" + destination + ", origin="+origin+", fields.size()=" + fields.size();
	}
}