package org.valabs.stdmsg.webcon;

import org.valabs.odisp.common.Message;

/** ���������� �������� � ��������� WebCon.
 * <p> ��� ����, ��� �� �� ������ �������������� ������������ � �������� ����������� �� ������ ������
 * ����� ������� ������������ ������. �������� �� ������������ ����� ������������ ��� ���������, � ��
 * �� ����� isCorrect().</p>
 * <p>��������� �� �������� ����������������, ��� ��� �� ����� ����� ��������� �������� � ��������� ���������.</p>
 * @author (C) 2004 <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @version $Id: WCAddServletMessage.java,v 1.7 2004/10/28 22:57:00 valeks Exp $
 */

public class WCAddServletMessage {
  /** �������������� ��� ���������. */
  public static final String NAME = "wc_add_servlet";
  /** ��� ����. */
  private static final String SERVLETMASK_IDX = "servletmask";
  /** ��� ����. */
  private static final String SERVLETHANDLER_IDX = "servlethandler";
  /** ����������� ���������.
   * @param webConName ��� ������� webcon. � ������ ���� null ������������ ������ <tt>webcon</tt>.
   * @param objectName �������� ���������
   * @param msgId ������ ��������� �� ������� ������������ �����
   */
  public static final void setup(final Message msg, final String webConName, final String objectName, final int msgId) {
    msg.setAction(NAME);
    if (webConName == null) {
      msg.setDestination("webcon");      
    } else {
      msg.setDestination(webConName);
    }
    msg.setOrigin(objectName);
    msg.setReplyTo(msgId);
    msg.setRoutable(false);
    msg.setCorrect(false);
  }

  /** ��������� �����. */
  public static final void setServletMask(final Message msg, final String newServletMask) {
    msg.addField(SERVLETMASK_IDX, newServletMask);
    if (msg.getContents().size() == 2) {
      msg.setCorrect(true);
    }
  }

  /** ������ � �����. */
  public static final String getServletMask(final Message msg) {
    return (String) msg.getField(SERVLETMASK_IDX);
  }

  /** ��������� �����������. */
  public static final void setServletHandler(final Message msg, final Object newServletHandler) {
    msg.addField(SERVLETHANDLER_IDX, newServletHandler);
    if (msg.getContents().size() == 2) {
      msg.setCorrect(true);
    }
  }

  /** ������ � �����������. */
  public static final Object getServletHandler(final Message msg) {
    return msg.getField(SERVLETHANDLER_IDX);
  }

  public static final boolean equals(final Message msg) {
    return msg.getAction().equals(NAME);
  }
} // WCAddServletMessage
