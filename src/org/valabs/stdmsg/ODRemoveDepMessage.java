package com.novel.stdmsg;

/** ����� ����������� ��������� ���������� ODISP �� ��������� ������ ������������.
 * ��� ��������� ������ ������������� ������ �����������. 
 * � �������� ��������� ����� ������� ��� �����������.
 * @author <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @author (C) 2003, ��� "�����-��"
 * @version $Id: ODRemoveDepMessage.java,v 1.3 2003/12/04 09:48:20 valeks Exp $
 */

public class ODRemoveDepMessage extends StandartMessage {
  /** ���������� ��� ���������*/
  public static final String name = "od_remove_dep";
  /** ������� ����� ��������� � ��������� ����������� 
   * @param origin �����������
   * @param replyTo ������ ��������� �� ������� ������������ �����
   */
  public ODRemoveDepMessage(String origin, int replyTo) {
    super("od_remove_dep", origin, "stddispatcher", replyTo);
  }

  /** ������� �������� ���� ������
   * @return ��� ������
   */
  public String getDepName() {
    return (String) getField(0);
  }

  /** ���������� �������� ����� �����������
   * @param newDepName ����� ��� �����������
   */
  public ODRemoveDepMessage setDepName(String newDepName) {
    fields.clear();
    addField(newDepName);
    return this;
  }
  
}// ODShutdownMessage
