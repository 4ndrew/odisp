package com.novel.stdmsg;

/** ����� ����������� ��������� ���������� ODISP �� ��������.
 * ��� ��������� ������ ������������� ������ �����������.
 * � �������� ��������� ����� ������� ��� ������ ������� �����
 * ���������� ��������.
 * @author <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @author (C) 2003, ��� "�����-��"
 * @version $Id: ODShutdownMessage.java,v 1.6 2004/01/16 10:29:15 valeks Exp $
 */

public class ODShutdownMessage extends StandartMessage {
  /** ���������� ��� ���������. */
  public static final String NAME = "od_shutdown";

  /** ��� ������. */
  private transient int exitCode = 0;
  /** ������ ���� � ���������. */
  private static final int EXITCODE_IDX = 0;

  /** ������� ����� ��������� � ��������� �����������.
   * @param origin �����������
   * @param replyTo ������ ��������� �� ������� ������������ �����
   */
  public ODShutdownMessage(final String origin, final int replyTo) {
    super("od_shutdown", origin, "stddispatcher", replyTo);
  }

  /** ������� �������� ���� ������.
   * @return ��� ������
   */
  public final int getExitCode() {
    if (isCE()) {
      return ((Integer) getField(EXITCODE_IDX)).intValue();
    }
    return exitCode;
  }

  /** ���������� �������� ���� ������.
   * @param newExitCode ����� �������� ���� ������
   * @return ������ �� ���������
   */
  public final ODShutdownMessage setExitCode(final int newExitCode) {
    exitCode = newExitCode;
    return this;
  }

  /** �������� ������������ ���������.
   * @return ���� ������������
   */
  public final boolean isCorrect() {
    if (isCE()) {
      return true;
    }
    getFields().clear();
    addField(new Integer(exitCode));
    setCE(true);
    return true;
  }

} // ODShutdownMessage
