package com.novel.stdmsg;

/** ������ ���������� �� ������ �������.
 * <p>������ ������ ��������� ������� 1 �������� - ��� ���������� �������.
 * �������������� �������������� �������� (�� ��������� - ����) ����������
 * ����� �� ������ �������� ������������� �� ��� ��� ���� �� �� ���������
 * ������.</p>
 * @author <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @author (C) 2003, ��� "�����-��"
 * @version $Id: ODAcquireMessage.java,v 1.10 2004/05/21 21:49:28 valeks Exp $
 */

public class ODAcquireMessage extends StandartMessage {
  /** ���������� ��� ���������. */
  public static final String NAME = "od_acquire";
  /** ��� �������. */
  private transient String resourceName;
  /** ������ ���������� � ������ �����. */
  private static final String NAME_IDX = "0";
  /** ������� ����� ��������� ���������� � �������� �� ������ �������.
   * @param origin �����
   * @param replyTo � ����� �� ��������� No.
   */
  public ODAcquireMessage(final String origin, final int replyTo) {
    super("od_acquire", "dispatcher", origin, replyTo);
  }

  /** ������� ��� ������� ��� �������.
   * @return ��� �������
   */
  public final String getResourceName() {
    if (isCE()) {
      return (String) getField(NAME_IDX);
    }
    return resourceName;
  }

  /** ���������� ��� �������.
   * @param newName ����� ���
   * @return ������ �� ������� ���������
   */
  public final ODAcquireMessage setResourceName(final String newName) {
    resourceName = newName;
    return this;
  }

  /** �������� ������������ ���������.
   * @return ���� ������������
   */
  public final boolean isCorrect() {
    if (isCE()) {
      return true;
    }
    if (resourceName != "") {
      getContents().clear();
      addField(NAME_IDX, resourceName);
      setCE(true);
      return true;
    } else {
      return false;
    }
  }

  public boolean isRoutable() {
    return false;
  }
} // ODAcquireMessage
