package com.novel.odisp.common;

/** ��������� ����������� ��������� ���� ODISP �������.
 * @author �������� �. ��������
 * @author (C) 2003, ��� "�����-��"
 * @version $Id: MessageHandler.java,v 1.3 2004/01/16 14:31:57 valeks Exp $
 */
public interface MessageHandler {
  /** ����� ���������� ��� ��������� ������� ������� ���������.
   * ������������������� ����.
   * @param msg ���������� ���������
   */
  void messageReceived(Message msg);
}
