package com.novel.odisp.common;

/** ����������� ��������� ����������, ������� ������ �������������
 * �����, ������� ��������� � �������� ODISP ����������.
 * @author �������� �. ��������
 * @author (C) 2003, ��� "�����-��"
 * @version $Id: Dispatcher.java,v 1.3 2003/10/14 09:38:02 valeks Exp $
 */
public interface Dispatcher {
	public void sendMessage(Message message);
	public void sendMessages(Message[] messageList);
	public Message getNewMessage(String action, String destination, String origin, int inReplyTo);
}