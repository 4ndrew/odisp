package com.novel.stdmsg;

/** ��������� � ����������� ������ �������.
 * �������������� �������� reason ���������� ��� ������� ������
 * @author <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @author (C) 2003, ��� "�����-��"
 * @version $Id: ODCleanupMessage.java,v 1.4 2004/01/16 10:29:15 valeks Exp $
 */

public class ODCleanupMessage extends StandartMessage {
  /** ���������� ��� ���������. */
  public static final String NAME = "od_cleanup";
  /** ������� ������. */
  private transient int reason = 0;
  /** ����� ���� � ��������. */
  private static final int REASON_IDX = 0;
  /** ����������� ���������.
   * @param destination ���������� ���������
   * @param replyId ������ ��������� �� ������� ������������ �����
   */
  public ODCleanupMessage(final String destination, final int replyId) {
    super("od_cleanup", destination, "stddispatcher", replyId);
  }

  /** ������� ������� �������� ������� ������.
   * @return ��� ������
   */
  public final int getReason() {
    if (isCE()) {
      return ((Integer) getField(REASON_IDX)).intValue();
    }
    return reason;
  }

  /** ���������� ����� �������� ���� ������.
   * @param newReason ����� �������� ����
   * @return ������ �� ���������
   */
  public final ODCleanupMessage setReason(final int newReason) {
    reason = newReason;
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
    addField(new Integer(reason));
    setCE(true);
    return true;
  }
} // ODCleanupMessage
