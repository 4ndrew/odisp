package com.novel.stdmsg;

import java.util.List;

import com.novel.odisp.common.Message;

/** ������ �� ������������ ��������� ������ ��������.
 * @author <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @author (C) 2004, ��� "�����-��"
 * @version $Id: ODGetProvidingReplyMessage.java,v 1.5 2004/06/09 18:55:57 valeks Exp $
 */

public class ODGetProvidingReplyMessage {
  /** ������������� ��� ���������. */
  public static final String NAME = "od_get_providing_reply";

  /** ��� ��������� � ���������. */
  private static String PROVIDINGLIST_IDX = "0";

  /** ����������� ���������.
   * @param objectName ��� �������
   * @param replyId ����� ��������� �� ������� ������������ �����
   */
  public static final void setup(final Message msg, final String objectName, final int replyId) {
    // ���������� ����� ������ ���������
    msg.setAction(NAME);
    msg.setOrigin("dispatcher");
    msg.setDestination(objectName);
    msg.setReplyTo(replyId);
    msg.setRoutable(false);
  }
  /** ������ � ����� �������. */
  public static final List getProvidingList(final Message msg) {
    return (List) msg.getField(PROVIDINGLIST_IDX);
  }
  /** ��������� ����� �������. */
  public final static void setProvidingList(final Message msg, final List newProvidingList) {
    msg.addField(PROVIDINGLIST_IDX, newProvidingList);
  }

  public final static boolean equals(final Message msg) {
    return msg.getAction().equals(NAME);
  }
} // ODGetProvidingReplyMessage
