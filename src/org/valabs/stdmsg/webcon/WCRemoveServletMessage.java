package com.novel.stdmsg.webcon;

import com.novel.odisp.common.Message;

/** �������� �������� �� ����������.
 * ������������ �������� ������ �� ���������� �������� �� ������� ����������.
 * @author <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @author (C) 2004, ��� "�����-��"
 * @version $Id: WCRemoveServletMessage.java,v 1.3 2004/06/09 21:07:36 valeks Exp $
 */

public class WCRemoveServletMessage {
  /** �������������� ��� ���������. */
  public static final String NAME = "wc_remove_servlet";
  /** ��� ����. */
  private static final String SERVLETHANDLER_IDX = "servlethandler";
  /** ����������� ���������.
   * @param webConName ��� ������� webcon. � ������ ���� null ������������ ������ <tt>webcon</tt>.
   * @param objectName �������� ���������
   * @param msgId ������ ��������� �� ������� ������������ �����
   */
  public static final void setup(final Message msg, final String webConName, final String objectName, final int msgId) {
    msg.setAction(NAME);
    msg.setDestination(webConName);
    msg.setOrigin(objectName);
    msg.setReplyTo(msgId);
    msg.setRoutable(false);
    msg.setCorrect(false);
  }

  /** ��������� �����������. */
  public static final void setServletHandler(final Message msg, final String newServletHandler) {
    msg.addField(SERVLETHANDLER_IDX, newServletHandler);
    msg.setCorrect(true);
  }

  /** ������ � �����������. */
  public static final Object getServletHandler(final Message msg) {
    return (Object) msg.getField(SERVLETHANDLER_IDX);
  }

  public static final boolean equals(final Message msg) {
    return msg.getAction().equals(NAME);
  }
}// WCRemoveServletMessage
