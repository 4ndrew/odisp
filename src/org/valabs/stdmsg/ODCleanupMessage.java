package com.novel.stdmsg;

/** ��������� � ����������� ������ �������.
 * �������������� �������� reason ���������� ��� ������� ������
 * @author <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @author (C) 2003, ��� "�����-��"
 * @version $Id: ODCleanupMessage.java,v 1.1 2003/12/03 19:18:38 valeks Exp $
 */

public class ODCleanupMessage extends StandartMessage {
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
    if (getFieldsCount() == 1) {
      return ((Integer) getField(0)).intValue();
    }
    return 0;
  }

  /** ���������� ����� �������� ���� ������
   * @param newReason ����� �������� ����
   * @return ������ �� ���������
   */
  public ODCleanupMessage setReason(int newReason) {
    fields.add(0, new Integer(newReason));
    return this;
  }
  
}// ODCleanupMessage
