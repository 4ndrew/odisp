package com.novel.stdmsg;

import com.novel.odisp.common.Message;
/** ��������� � ����������� ������ �������.
 * �������������� �������� reason ���������� ��� ������� ������
 * @author <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @author (C) 2003, ��� "�����-��"
 * @version $Id: ODCleanupMessage.java,v 1.8 2004/06/09 00:28:48 valeks Exp $
 */

public class ODCleanupMessage {
  /** ���������� ��� ���������. */
  public static final String NAME = "od_cleanup";
  /** ����� ���� � ��������. */
  private static final String REASON_IDX = "reason";
  /** ��������� ���������.
   * @param destination ���������� ���������
   * @param replyId ������ ��������� �� ������� ������������ �����
   */
  public static void setup(final Message msg) {
    msg.setAction(NAME);
    msg.setOrigin("dispatcher");
    msg.setRoutable(false);
  }

  /** ������� ������� �������� ������� ������.
   * @return ��� ������
   */
  public static final int getReason(final Message msg) {
    return ((Integer) msg.getField(REASON_IDX)).intValue();
  }

  /** ���������� ����� �������� ���� ������.
   * @param newReason ����� �������� ����
   * @return ������ �� ���������
   */
  public static final void setReason(final Message msg, final int newReason) {
    msg.addField(REASON_IDX, new Integer(newReason));
  }
} // ODCleanupMessage
