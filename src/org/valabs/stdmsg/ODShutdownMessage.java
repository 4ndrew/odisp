package com.novel.stdmsg;

/** ����� ����������� ��������� ���������� ODISP �� ��������.
 * ��� ��������� ������ ������������� ������ �����������.
 * � �������� ��������� ����� ������� ��� ������ ������� ����� ���������� ��������.
 * @author <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @author (C) 2003, ��� "�����-��"
 * @version $Id: ODShutdownMessage.java,v 1.5 2003/12/15 14:02:43 valeks Exp $
 */

public class ODShutdownMessage extends StandartMessage {
  /** ���������� ��� ���������*/
  public static final String name = "od_shutdown";
  
  private transient int exitCode = 0;

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
    if (ce) {
      return ((Integer) getField(0)).intValue();
    }
    return exitCode;
  }

  /** ���������� �������� ���� ������
   * @param newExitCode ����� �������� ���� ������
   */
  public ODShutdownMessage setExitCode(int newExitCode) {
    exitCode = newExitCode;
    return this;
  }
  
  public boolean isCorrect() {
    if (ce) {
      return true;
    }
    fields.clear();
    addField(new Integer(exitCode));
    return true;
  }

}// ODShutdownMessage
