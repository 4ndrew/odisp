package com.novel.stdmsg.webcon;

import java.util.List;

import com.novel.odisp.common.Message;

/** ������ �� ������������ ��������� ������ ��������.
 * @author <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @author (C) 2004, ��� "�����-��"
 * @version $Id: WCListServletsReplyMessage.java,v 1.3 2004/06/09 21:07:36 valeks Exp $
 */

public class WCListServletsReplyMessage {
  /** ������������� ��� ���������. */
  public static final String NAME = "wc_list_servlets_reply";
  /** ��� ��������� � ���������. */
  private static String SERVLETSLIST_IDX = "servletlist";
  /** ����������� ���������.
   * @param webCon ������-�����������
   * @param objectName ��� �������-����������
   * @param replyId ����� ��������� �� ������� ������������ �����
   */
  public static final void setup(final Message msg, final String webCon, final String objectName, final int replyId) {
    msg.setAction(NAME);
    msg.setDestination(objectName);
    msg.setOrigin(webCon);
    msg.setReplyTo(replyId);
    msg.setRoutable(false);
    msg.setCorrect(false);
  }
  /** ������ � ����� �������. */
  public static final List getServletsList(final Message msg) {
    return (List) msg.getField(SERVLETSLIST_IDX);
  }
  /** ��������� ����� �������. */
  public static final void setServletsList(final Message msg, final List newServletsList) {
    msg.addField(SERVLETSLIST_IDX, newServletsList);
    msg.setCorrect(true);
  }

  public static final boolean equals(final Message msg) {
    return msg.getAction().equals(NAME);
  }
} // WCListServletsReplyMessage

