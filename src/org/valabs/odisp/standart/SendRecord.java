package com.novel.odisp.standart;

import com.novel.odisp.common.Message;
import com.novel.odisp.common.ODObject;

/** ������ � ���������� ���������.
 * �������� ������ �� ������������ ��������� � �� ������-����������.
 * @author <a href="valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @author (C) 2004 ��� "�����-��"
 * @version $Id: SendRecord.java,v 1.2 2004/07/21 08:05:42 valeks Exp $
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
