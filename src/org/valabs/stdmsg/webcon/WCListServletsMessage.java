package org.valabs.stdmsg.webcon;

import org.valabs.odisp.common.Message;

/** ��������� ������ ��������� � ����������.
 * @author (C) 2004 <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @version $Id: WCListServletsMessage.java,v 1.5 2004/08/23 07:42:36 valeks Exp $
 */

public class WCListServletsMessage {
  /** �������������� ��� ���������. */
  public static final String NAME = "wc_list_servlets";
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
    msg.setCorrect(true);
  }

  public static final boolean equals(final Message msg) {
    return msg.getAction().equals(msg);
  }
}// WCRemoveServletMessage
