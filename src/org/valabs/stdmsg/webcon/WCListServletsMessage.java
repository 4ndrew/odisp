package com.novel.stdmsg.webcon;

import com.novel.stdmsg.StandartMessage;
import com.novel.odisp.common.Message;

/** ��������� ������ ��������� � ����������.
 * @author <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @author (C) 2004, ��� "�����-��"
 * @version $Id: WCListServletsMessage.java,v 1.1 2004/03/27 21:27:40 valeks Exp $
 */

public class WCListServletsMessage extends StandartMessage {
  /** �������������� ��� ���������. */
  public static final String NAME = "wc_list_servlets";
  /** ����������� ���������.
   * @param webConName ��� ������� webcon. � ������ ���� null ������������ ������ <tt>webcon</tt>.
   * @param objectName �������� ���������
   * @param msgId ������ ��������� �� ������� ������������ �����
   */
  public WCListServletsMessage(String webConName, final String objectName, final int msgId) {
    super(NAME, webConName, objectName, msgId);
    if (webConName == null) {
      // �������� ����� Discovery
      setDestination("webcon");
    }
  }

  /** ���������� �����������.
   * @param msg ��������� ��� ����������
   */
  public WCListServletsMessage(final Message msg) {
    super(msg);
  }

  /** �������� �� ��������� ����������������. */
  public final boolean isRoutable() {
    return false;
  }
}// WCRemoveServletMessage
