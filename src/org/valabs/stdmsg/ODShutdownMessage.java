package com.novel.stdmsg;

import com.novel.odisp.common.Message;
/** ����� ����������� ��������� ���������� ODISP �� ��������.
 * ��� ��������� ������ ������������� ������ �����������.
 * � �������� ��������� ����� ������� ��� ������ ������� �����
 * ���������� ��������.
 * @author <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @author (C) 2003-2004, ��� "�����-��"
 * @version $Id: ODShutdownMessage.java,v 1.11 2004/06/09 18:23:12 valeks Exp $
 */

public class ODShutdownMessage {
  /** ���������� ��� ���������. */
  public static final String NAME = "od_shutdown";
  /** ������ ���� � ���������. */
  private static final String EXITCODE_IDX = "0";

  /** ����������� ��������� � ��������� �����������.
   * @param origin �����������
   * @param replyTo ������ ��������� �� ������� ������������ �����
   */
  public static final void setup(final Message msg, final String origin, final int replyTo) {
    msg.setAction(NAME);
    msg.setDestination("dispatcher");
    msg.setOrigin(origin);
    msg.setReplyTo(replyTo);
    msg.setRoutable(false);
    msg.setCorrect(true);
  }

  /** ������� �������� ���� ������.
   * @return ��� ������
   */
  public static final int getExitCode(final Message msg) {
    return ((Integer) msg.getField(EXITCODE_IDX)).intValue();
  }

  /** ���������� �������� ���� ������.
   * @param newExitCode ����� �������� ���� ������
   * @return ������ �� ���������
   */
  public static final void setExitCode(final Message msg, final int newExitCode) {
    msg.addField(EXITCODE_IDX, new Integer(newExitCode));
  }

  public static final boolean equals(final Message msg) {
    return msg.getAction().equals(NAME);
  }
} // ODShutdownMessage
