package com.novel.stdmsg;

/** ������ ���������� �� ������ �������.
 * <p>������ ������ ��������� ������� 1 �������� - ��� ���������� �������.
 * �������������� �������������� �������� (�� ��������� - ����) ����������
 * ����� �� ������ �������� ������������� �� ��� ��� ���� �� �� ��������� ������.</p>
 * <p><b>��������</b>: ������� ���������� ������������ ���������� �� ��������� ��������� 
 * ����������� ���� �� ����������� ������, ������� ������������� � ����������� ��� ���.
 * ��� ����, ��� �� �������� ������������� ���������, ������������� ������������ ����������� 
 * ������ �� ����� ������ ������� � ������������� ������ ����������.</p>
 * @author <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @author (C) 2003, ��� "�����-��"
 * @version $Id: ODAcquireMessage.java,v 1.2 2003/12/03 19:17:53 valeks Exp $
 */

public class ODAcquireMessage extends StandartMessage {
  /** ������� ����� ��������� ���������� � �������� �� ������ �������
   * @param origin �����
   * @param replyTo � ����� �� ��������� No.
   */
  public ODAcquireMessage(String origin, int replyTo) {
    super("od_acquire", "stddispatcher", origin, replyTo);
  }

  /** ������� ��� ������� ��� �������
   * @return ��� �������
   */
  public String getResourceName() {
    return (String) getField(0);
  }

  /** ���������� ��� �������
   * @param newName ����� ���
   * @return ������ �� ������� ���������
   */
  public ODAcquireMessage setResourceName(String newName) {
    fields.add(0, newName);
    return this;
  }

  /** ������� ����� �������
   * �� ��������� - ������������� ������
   * @return ����� �������
   */
  public boolean getWillBlock() {
    boolean result = false;
    if (getFieldsCount() == 2) {
      result = ((Boolean) getField(1)).booleanValue();
    }
    return result;
  }

  /** ���������� ����� ����������
   * @param newBlock ����� �����
   * @return ������ �� ������� ���������
   */
  public ODAcquireMessage setWillBlock(boolean newBlock) {
    fields.add(1, new Boolean(newBlock));
    return this;
  }
  
}// ODAcquireMessage
