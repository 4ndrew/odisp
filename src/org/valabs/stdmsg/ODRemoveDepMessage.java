package com.novel.stdmsg;

/** ����� ����������� ��������� ���������� ODISP �� ���������
 * ������ ������������.
 * ��� ��������� ������ ������������� ������ �����������.
 * � �������� ��������� ����� ������� ��� �����������.
 * @author <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @author (C) 2003, ��� "�����-��"
 * @version $Id: ODRemoveDepMessage.java,v 1.9 2004/04/02 09:54:49 valeks Exp $
 */

public class ODRemoveDepMessage extends StandartMessage {
  /** ���������� ��� ���������. */
  public static final String NAME = "od_remove_dep";
  /** ��� �����������. */
  private transient String depName = "";
  /** ������ �����. */
  private static final String DEPNAME_IDX = "0";
  /** ������� ����� ��������� � ��������� �����������.
   * @param origin �����������
   * @param replyTo ������ ��������� �� ������� ������������ �����
   */
  public ODRemoveDepMessage(final String origin, final int replyTo) {
    super("od_remove_dep", "dispatcher", origin, replyTo);
  }

  /** ������� �������� ���� ������.
   * @return ��� ������
   */
  public final String getDepName() {
    if (isCE()) {
      return (String) getField(DEPNAME_IDX);
    }
    return depName;
  }

  /** ���������� �������� ����� �����������.
   * @param newDepName ����� ��� �����������
   * @return ������ �� ���������
   */
  public final ODRemoveDepMessage setDepName(final String newDepName) {
    depName = newDepName;
    return this;
  }

  /** �������� ������������ ���������.
   * @return ���� ������������
   */
  public final boolean isCorrect() {
    if (isCE()) {
      return true;
    }
    if (depName != "") {
      getContents().clear();
      addField(DEPNAME_IDX, depName);
      setCE(true);
    }
    return isCE();
  }

  public final boolean isRoutable() {
    return false;
  }

} // ODShutdownMessage
