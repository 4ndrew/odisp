package com.novel.stdmsg.webcon;

import com.novel.odisp.common.Message;
import com.novel.stdmsg.StandartMessage;
import java.util.List;

/** ������ �� ������������ ��������� ������ ��������.
 * @author <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @author (C) 2004, ��� "�����-��"
 * @version $Id: WCListServletsReplyMessage.java,v 1.1 2004/03/27 21:27:40 valeks Exp $
 */

public class WCListServletsReplyMessage extends StandartMessage {
  /** ������������� ��� ���������. */
  public static final String NAME = "wc_list_servlets_reply";
  /** �������� �������. */
  private transient List servletsList = null;
  /** ��� ��������� � ���������. */
  private static String SERVLETSLIST_IDX = "0";
  /** ���������� �����������.
   * @param msg ���������� ���������
   */
  public WCListServletsReplyMessage(final Message msg) {
    super(msg);
  }
  /** ����������� ���������.
   * @param webCon ������-�����������
   * @param objectName ��� �������-����������
   * @param replyId ����� ��������� �� ������� ������������ �����
   */
  public WCListServletsReplyMessage(final String webCon, final String objectName, final int replyId) {
    super(NAME, objectName, webCon, replyId);
  }
  /** ������ � ����� �������. */
  public final List getServletsList() {
    if (isCE()) {
      return (List) getField(SERVLETSLIST_IDX);
    }
    return servletsList;
  }
  /** ��������� ����� �������. */
  public final void setServletsList(final List newServletsList) {
    servletsList = newServletsList;
  }
  /** �������� ������������ ���������. */
  public final boolean isCorrect() {
    if (isCE()) {
      return true;
    }
    if (servletsList != null) {
      addField(SERVLETSLIST_IDX, servletsList);
      setCE(true);
    }
    return isCE();
  }

  /** �������� �� ��������� ��������������. */
  public final boolean isRoutable() {
    return false;
  }
} // WCListServletsReplyMessage
