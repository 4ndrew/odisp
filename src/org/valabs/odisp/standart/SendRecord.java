package com.novel.odisp.standart;

import com.novel.odisp.common.Message;
import com.novel.odisp.common.ODObject;

/** Запись о посылаемом сообщении.
 * Содержит ссылку на отправляемое сообщение и на объект-получатель.
 * @author <a href="valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @author (C) 2004 НПП "Новел-ИЛ"
 * @version $Id: SendRecord.java,v 1.2 2004/07/21 08:05:42 valeks Exp $
 */
class SendRecord {
	private Message message;
	private ODObject object;
	
	/** Доступ к сообщению.
	 * @return ссылка на сообщение
	 */
	public Message getMessage() {
		return message;
	}

	/** Доступ к объекту-получателю
	 * @return ссылка на объект-получатель
	 */
	public ODObject getObject() {
		return object;
	}
	
	/** Конструктор новой записи с заданными значениями 
	 * @param nmessage ссылка на сообщение
	 * @param nobject ссылка на объект-получатель
	 */
	public SendRecord(final Message nmessage, final ODObject nobject) {
		message = nmessage;
		object = nobject;
	}
	
	public String toString() {
		return "SendRecord to " + object.getObjectName() + " with message: " + message;
	}
}
