package com.novel.stdmsg;

/** ������ ���������� �� ������ �������.
 * <p>������ ������ ��������� ������� 1 �������� - ��� ���������� �������.
 * �������������� �������������� �������� (�� ��������� - ����) ����������
 * ����� �� ������ �������� ������������� �� ��� ��� ���� �� �� ���������
 * ������.</p>
 * @author <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @author (C) 2003, ��� "�����-��"
 * @version $Id: ODAcquireMessage.java,v 1.9 2004/04/02 09:54:49 valeks Exp $
 */

public class ODAcquireMessage extends StandartMessage {
  /** ���������� ��� ���������. */
  public static final String NAME = "od_acquire";
  /** ��� �������. */
  private transient String resourceName;
  /** ������ ���������� � ������ �����. */
  private static final String NAME_IDX = "0";
  /** ���� ����������. */
  private transient boolean willBlock = false;
  /** ������ ����� ����������. */
  private static final String BLOCK_IDX = "1";
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
      getContents().clear();
      addField(NAME_IDX, resourceName);
      addField(BLOCK_IDX, new Boolean(willBlock));
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
