package com.novel.odisp.common;

/** ��������� ����������� ��������� ���� ODISP �������.
 * @author (C) <a href="mailto:valeks@novel-il.ru">�������� �. ��������</a>
 * @version $Id: MessageHandler.java,v 1.4 2004/08/18 12:48:39 valeks Exp $
 */
public interface MessageHandler {
  /** ����� ���������� ��� ��������� ������� ������� ���������.
   * ������������������� ����.
   * @param msg ���������� ���������
   */
  void messageReceived(Message msg);
}
