package com.novel.stdmsg;

/** ����� ����������� ��������� ���������� ODISP �� ��������.
 * ��� ��������� ������ ������������� ������ �����������.
 * � �������� ��������� ����� ������� ��� ������ ������� ����� ���������� ��������.
 * @author <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @author (C) 2003, ��� "�����-��"
 * @version $Id: ODShutdownMessage.java,v 1.3 2003/12/03 19:17:53 valeks Exp $
 */

public class ODShutdownMessage extends StandartMessage {
  /** ������� ����� ��������� � ��������� ����������� 
   * @param origin �����������
   * @param replyTo ������ ��������� �� ������� ������������ �����
   */
  public ODShutdownMessage(String origin, int replyTo) {
    super("od_shutdown", origin, "stddispatcher", replyTo);
  }

  /** ������� �������� ���� ������
   * @return ��� ������
   */
  public int getExitCode() {
    int exitCode = 0;
    if (getFieldsCount() == 1) {
      exitCode = ((Integer) getField(0)).intValue();
    }
    return exitCode;
  }

  /** ���������� �������� ���� ������
   * @param newExitCode ����� �������� ���� ������
   */
  public ODShutdownMessage setExitCode(int newExitCode) {
    fields.clear();
    addField(new Integer(newExitCode));
    return this;
  }
  
}// ODShutdownMessage
