package com.novel.odisp.common;

/** ��������� ����������� ��������� ���� ODISP �������
 * @author �������� �. ��������
 * @author (C) 2003, ��� "�����-��"
 * @version $Id: MessageHandler.java,v 1.2 2003/11/15 19:08:18 valeks Exp $
 */
public interface MessageHandler {
  /** ����� ���������� ��� ��������� ������� ������� ��������� ������������������� ���� 
   * @param msg ���������� ���������
   */
  void messageReceived(Message msg);
}
