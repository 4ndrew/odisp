package com.novel.odisp.common;

/** ����������� ��������� ����������, ������� ������ �������������
 * �����, ������� ��������� � �������� ODISP ����������.
 * @author �������� �. ��������
 * @author (C) 2003, ��� "�����-��"
 * @version $Id: Dispatcher.java,v 1.4 2003/11/15 19:04:24 valeks Exp $
 */
public interface Dispatcher {
  /** ������� ���������� ��������� 
   * @param message ���������
   */
  void sendMessage(Message message);
  /** ������� ������ ���������
   * @param messageList ������ ���������
   */
  void sendMessages(Message[] messageList);
  /** ��������� ������ ��������� � ��������� �����������
   * @param action ��������
   * @param destination ����������
   * @param origin �����������
   * @param inReplyTo ��������� �� ������� ������������ �����
   * @return ���������
   */
  Message getNewMessage(String action, String destination, String origin, int inReplyTo);
}
