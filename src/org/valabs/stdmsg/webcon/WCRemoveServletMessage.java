package com.novel.stdmsg.webcon;

import com.novel.stdmsg.StandartMessage;
import com.novel.odisp.common.Message;

/** �������� �������� �� ����������.
 * ������������ �������� ������ �� ���������� �������� �� ������� ����������.
 * @author <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @author (C) 2004, ��� "�����-��"
 * @version $Id: WCRemoveServletMessage.java,v 1.1 2004/03/27 21:27:40 valeks Exp $
 */

public class WCRemoveServletMessage extends StandartMessage {
  /** �������������� ��� ���������. */
  public static final String NAME = "wc_remove_servlet";
  /** ������-���������� �������. */
  private transient Object servletHandler = null;
  /** ��� ����. */
  private static final String SERVLETHANDLER_IDX = "0";
  /** ����������� ���������.
   * @param webConName ��� ������� webcon. � ������ ���� null ������������ ������ <tt>webcon</tt>.
   * @param objectName �������� ���������
   * @param msgId ������ ��������� �� ������� ������������ �����
   */
  public WCRemoveServletMessage(String webConName, final String objectName, final int msgId) {
    super(NAME, webConName, objectName, msgId);
    if (webConName == null) {
      // �������� ����� Discovery
      setDestination("webcon");
    }
  }

  /** ���������� �����������.
   * @param msg ��������� ��� ����������
   */
  public WCRemoveServletMessage(final Message msg) {
    super(msg);
  }

  /** ��������� �����������. */
  public final void setServletHandler(final String newServletHandler) {
    servletHandler = newServletHandler;
  }

  /** ������ � �����������. */
  public final Object getServletHandler() {
    if (isCE()) {
      return (Object) getField(SERVLETHANDLER_IDX);
    }
    return servletHandler;
  }

  /** �������� ������������ ���������. */
  public final boolean isCorrect() {
    if (isCE()) {
      return isCE();
    }
    if (servletHandler != null) {
      addField(SERVLETHANDLER_IDX, servletHandler);
      setCE(true);
    }
    return isCE();
  }

  /** �������� �� ��������� ����������������. */
  public final boolean isRoutable() {
    return false;
  }
}// WCRemoveServletMessage
