package com.novel.stdmsg;

/** ����� ����������� ��������� ���������� ODISP �� ��������.
 * ��� ��������� ������ ������������� ������ �����������.
 * � �������� ��������� ����� ������� ��� ������ ������� ����� ���������� ��������.
 * @author <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @author (C) 2003, ��� "�����-��"
 * @version $Id: ODShutdownMessage.java,v 1.4 2003/12/04 09:48:20 valeks Exp $
 */

public class ODShutdownMessage extends StandartMessage {
  /** ���������� ��� ���������*/
  public static final String name = "od_shutdown";
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
