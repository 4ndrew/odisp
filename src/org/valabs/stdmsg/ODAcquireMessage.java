package com.novel.stdmsg;

import com.novel.odisp.*;
import com.novel.odisp.common.*;

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
 * @version $Id: ODAcquireMessage.java,v 1.1 2003/12/01 22:19:29 valeks Exp $
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
    fields.set(0, name);
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
    fields.set(1, new Boolean(newBlock));
    return this;
  }
  
}// ODAcquireMessage
