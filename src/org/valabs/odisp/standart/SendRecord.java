package org.valabs.odisp.standart;

import org.valabs.odisp.common.Message;
import org.valabs.odisp.common.ODObject;

/** ������ � ���������� ���������.
 * �������� ������ �� ������������ ��������� � �� ������-����������.
 * @author (C) 2004 <a href="valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @version $Id: SendRecord.java,v 1.4 2004/08/23 07:42:37 valeks Exp $
 */
class SendRecord {
	private Message message;
	private ODObject object;
	
	/** ������ � ���������.
	 * @return ������ �� ���������
	 */
	public Message getMessage() {
		return message;
	}

	/** ������ � �������-����������
	 * @return ������ �� ������-����������
	 */
	public ODObject getObject() {
		return object;
	}
	
	/** ����������� ����� ������ � ��������� ���������� 
	 * @param nmessage ������ �� ���������
	 * @param nobject ������ �� ������-����������
	 */
	public SendRecord(final Message nmessage, final ODObject nobject) {
		message = nmessage;
		object = nobject;
	}
	
	public String toString() {
		return "SendRecord to " + object.getObjectName() + " with message: " + message;
	}
}
