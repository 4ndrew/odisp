package com.novel.stdmsg;

/** ����� ����������� ��������� ���������� ODISP �� ��������� ������ ������������.
 * ��� ��������� ������ ������������� ������ �����������. 
 * � �������� ��������� ����� ������� ��� �����������.
 * @author <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @author (C) 2003, ��� "�����-��"
 * @version $Id: ODRemoveDepMessage.java,v 1.4 2003/12/15 14:02:43 valeks Exp $
 */

public class ODRemoveDepMessage extends StandartMessage {
  /** ���������� ��� ���������*/
  public static final String name = "od_remove_dep";

  private transient String depName = "";
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
    if (ce) {
      return (String) getField(0);
    }
    return depName;
  }

  /** ���������� �������� ����� �����������
   * @param newDepName ����� ��� �����������
   */
  public ODRemoveDepMessage setDepName(String newDepName) {
    depName = newDepName;
    return this;
  }
  
  public boolean isCorrect() {
    if (ce) {
      return true;
    }
    if (depName != "") {
      fields.clear();
      addField(depName);
      return true;
    } else {
      return false;
    }
  }

}// ODShutdownMessage
