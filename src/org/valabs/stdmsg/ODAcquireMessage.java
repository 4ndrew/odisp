package com.novel.stdmsg;

import com.novel.odisp.common.Message;
/** ������ ���������� �� ������ �������.
 * ������ ������ ��������� ������� 1 �������� - ��� ���������� �������.
 * @author <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @author (C) 2003, ��� "�����-��"
 * @version $Id: ODAcquireMessage.java,v 1.11 2004/06/09 14:19:38 valeks Exp $
 */

public class ODAcquireMessage {
  /** ���������� ��� ���������. */
  public static final String NAME = "od_acquire";
  /** ������ ������ ������� � ������ �����. */
  private static final String NAME_IDX = "resname";
  /** ��������� ���������.
   * @param msg ������������� ���������
   */
  public final static void setup(final Message msg) {
    msg.setAction(NAME);
    msg.setDestination("dispatcher");
    msg.setRoutable(false);
    msg.setCorrect(false);
  }

  /** ������� ��� ������� ��� �������.
   * @return ��� �������
   */
  public final static String getResourceName(final Message msg) {
    return (String) msg.getField(NAME_IDX);
  }

  /** ���������� ��� �������.
   * @param newName ����� ���
   * @return ������ �� ������� ���������
   */
  public final static void setResourceName(final Message msg, final String newName) {
    assert newName != null;
    assert msg.getAction() != null;
    msg.addField(NAME_IDX, newName);
    msg.setCorrect(true);
  }

  public final static boolean equals(final Message msg) {
    return msg.getAction().equals(NAME);
  }
} // ODAcquireMessage
