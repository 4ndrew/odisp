/* ODISP -- Message Oriented Middleware
 * Copyright (C) 2003-2005 Valentin A. Alekseev
 * Copyright (C) 2003-2005 Andrew A. Porohin 
 * 
 * ODISP is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, version 2.1 of the License.
 * 
 * ODISP is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with ODISP.  If not, see <http://www.gnu.org/licenses/>.
 */
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
