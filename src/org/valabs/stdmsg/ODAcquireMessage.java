package com.novel.stdmsg;

/** ������ ���������� �� ������ �������.
 * <p>������ ������ ��������� ������� 1 �������� - ��� ���������� �������.
 * �������������� �������������� �������� (�� ��������� - ����) ����������
 * ����� �� ������ �������� ������������� �� ��� ��� ���� �� �� ���������
 * ������.</p>
 * @author <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @author (C) 2003, ��� "�����-��"
 * @version $Id: ODAcquireMessage.java,v 1.6 2004/02/15 23:09:07 valeks Exp $
 */

public class ODAcquireMessage extends StandartMessage {
  /** ���������� ��� ���������. */
  public static final String NAME = "od_acquire";
  /** ��� �������. */
  private transient String resourceName;
  /** ������ ���������� � ������ �����. */
  private static final int NAME_IDX = 0;
  /** ���� ����������. */
  private transient boolean willBlock = false;
  /** ������ ����� ����������. */
  private static final int BLOCK_IDX = 0;
  /** ������� ����� ��������� ���������� � �������� �� ������ �������.
   * @param origin �����
   * @param replyTo � ����� �� ��������� No.
   */
  public ODAcquireMessage(final String origin, final int replyTo) {
    super("od_acquire", "stddispatcher", origin, replyTo);
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

  /** ������� ����� �������.
   * �� ��������� - ������������� ������
   * @return ����� �������
   */
  public final boolean getWillBlock() {
    if (isCE()) {
      return ((Boolean) getField(BLOCK_IDX)).booleanValue();
    }
    return willBlock;
  }

  /** ���������� ����� ����������.
   * @param newBlock ����� �����
   * @return ������ �� ������� ���������
   */
  public final ODAcquireMessage setWillBlock(final boolean newBlock) {
    willBlock = newBlock;
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
      getFields().clear();
      addField(resourceName);
      addField(new Boolean(willBlock));
      setCE(true);
      return true;
    } else {
      return false;
    }
  }
} // ODAcquireMessage
