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
 * @version $Id: ODAcquireMessage.java,v 1.4 2003/12/15 14:02:43 valeks Exp $
 */

public class ODAcquireMessage extends StandartMessage {
  /** ���������� ��� ���������*/
  public static final String name = "od_acquire";
  private transient String resourceName;
  private transient boolean willBlock = false;
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
    if (ce) {
      return (String) getField(0);
    }
    return resourceName;
  }

  /** ���������� ��� �������
   * @param newName ����� ���
   * @return ������ �� ������� ���������
   */
  public ODAcquireMessage setResourceName(String newName) {
    resourceName = newName;
    return this;
  }

  /** ������� ����� �������
   * �� ��������� - ������������� ������
   * @return ����� �������
   */
  public boolean getWillBlock() {
    if (ce) {
      return ((Boolean) getField(1)).booleanValue();
    }
    return willBlock;
  }

  /** ���������� ����� ����������
   * @param newBlock ����� �����
   * @return ������ �� ������� ���������
   */
  public ODAcquireMessage setWillBlock(boolean newBlock) {
    willBlock = newBlock;
    return this;
  }
  
  public boolean isCorrect() {
    if (ce) {
      return true;
    }
    if (resourceName != "") {
      fields.clear();
      addField(resourceName);
      addField(new Boolean(willBlock));
      ce = true;
      return true;
    } else {
      return false;
    }
  }
}// ODAcquireMessage
