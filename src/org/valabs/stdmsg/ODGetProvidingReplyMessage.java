package com.novel.stdmsg;

import java.util.List;

import com.novel.odisp.common.Message;

/** ������ �� ������������ ��������� ������ ��������.
 * @author <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @author (C) 2004, ��� "�����-��"
 * @version $Id: ODGetProvidingReplyMessage.java,v 1.6 2004/07/20 11:55:24 valeks Exp $
 */

public final class ODGetProvidingReplyMessage {
  /** ������������� ��� ���������. */
  public static final String NAME = "od_get_providing_reply";

  /** ��� ��������� � ���������. */
  private static String PROVIDINGLIST_IDX = "0";

  /** ����������� ���������.
   * @param objectName ��� �������
   * @param replyId ����� ��������� �� ������� ������������ �����
   */
  public static void setup(final Message msg, final String objectName, final int replyId) {
    // ���������� ����� ������ ���������
    msg.setAction(NAME);
    msg.setOrigin("dispatcher");
    msg.setDestination(objectName);
    msg.setReplyTo(replyId);
    msg.setRoutable(false);
  }
  /** ������ � ����� �������. */
  public static List getProvidingList(final Message msg) {
    return (List) msg.getField(PROVIDINGLIST_IDX);
  }
  /** ��������� ����� �������. */
  public final void setProvidingList(final Message msg, final List newProvidingList) {
    msg.addField(PROVIDINGLIST_IDX, newProvidingList);
  }

  public final boolean equals(final Message msg) {
    return msg.getAction().equals(NAME);
  }
} // ODGetProvidingReplyMessage
