package org.valabs.odisp.common;

/** ��������� ����������� ��������� ���� ODISP �������.
 * @author (C) <a href="mailto:valeks@novel-il.ru">�������� �. ��������</a>
 * @version $Id: MessageHandler.java,v 1.5 2004/08/23 07:42:37 valeks Exp $
 */
public interface MessageHandler {
  /** ����� ���������� ��� ��������� ������� ������� ���������.
   * ������������������� ����.
   * @param msg ���������� ���������
   */
  void messageReceived(Message msg);
}
