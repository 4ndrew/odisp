package org.valabs.odisp.standart;

import org.valabs.odisp.common.Message;
import org.valabs.odisp.common.ODObject;

/** Запись о посылаемом сообщении.
 * Содержит ссылку на отправляемое сообщение и на объект-получатель.
 * @author (C) 2004 <a href="valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @version $Id: SendRecord.java,v 1.5 2005/02/27 12:37:31 valeks Exp $
 */
class SendRecord {
	private final Message message;
	private final ODObject object;
	
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
