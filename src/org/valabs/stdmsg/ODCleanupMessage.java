package com.novel.stdmsg;

/** ��������� � ����������� ������ �������.
 * �������������� �������� reason ���������� ��� ������� ������
 * @author <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @author (C) 2003, ��� "�����-��"
 * @version $Id: ODCleanupMessage.java,v 1.3 2003/12/15 14:02:43 valeks Exp $
 */

public class ODCleanupMessage extends StandartMessage {
  /** ���������� ��� ��������� */
  public static final String name = "od_cleanup";
  private transient int reason = 0;
  /** ����������� ���������
   * @param destination ���������� ���������
   * @param replyId ������ ��������� �� ������� ������������ �����
   */
  public ODCleanupMessage(String destination, int replyId) {
    super("od_cleanup", destination, "stddispatcher", replyId);
  }

  /** ������� ������� �������� ������� ������
   * @return ��� ������
   */
  public int getReason() {
    if (ce) {
      return ((Integer) getField(0)).intValue();
    }
    return reason;
  }

  /** ���������� ����� �������� ���� ������
   * @param newReason ����� �������� ����
   * @return ������ �� ���������
   */
  public ODCleanupMessage setReason(int newReason) {
    reason = newReason;
    return this;
  }
  
  public boolean isCorrect() {
    if (ce) {
      return true;
    }
    fields.clear();
    addField(new Integer(reason));
    ce = true;
    return true;
  }
}// ODCleanupMessage
