package com.novel.odisp;

import com.novel.odisp.common.Message;
import com.novel.odisp.common.ODObject;

/** ������ � ���������� ���������.
 * �������� ������ �� ������������ ��������� � �� ������-����������.
 * @author <a href="valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @author (C) 2004 ��� "�����-��"
 * @version $Id: SendRecord.java,v 1.1 2004/05/14 07:56:42 valeks Exp $
 */
public class SendRecord {
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
