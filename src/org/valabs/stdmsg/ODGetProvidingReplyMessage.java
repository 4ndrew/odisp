package com.novel.stdmsg;

import com.novel.odisp.common.Message;
import java.util.List;

/** ������ �� ������������ ��������� ������ ��������.
 * @author <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @author (C) 2004, ��� "�����-��"
 * @version $Id: ODGetProvidingReplyMessage.java,v 1.2 2004/03/27 20:07:56 valeks Exp $
 */

public class ODGetProvidingReplyMessage extends StandartMessage {
  /** ������������� ��� ���������. */
  public static final String NAME = "od_get_providing_reply";
  /** �������� �������. */
  private transient List providingList = null;
  /** ��� ��������� � ���������. */
  private static String PROVIDINGLIST_IDX = "0";
  /** ���������� �����������.
   * @param msg ���������� ���������
   */
  public ODGetProvidingReplyMessage(final Message msg) {
    super(msg);
  }
  /** ����������� ���������.
   * @param objectName ��� �������
   * @param replyId ����� ��������� �� ������� ������������ �����
   */
  public ODGetProvidingReplyMessage(final String objectName, final int replyId) {
    // ���������� ����� ������ ���������
    super(NAME, objectName, "stddispatcher", replyId);
  }
  /** ������ � ����� �������. */
  public final List getProvidingList() {
    if (isCE()) {
      return (List) getField(PROVIDINGLIST_IDX);
    }
    return providingList;
  }
  /** ��������� ����� �������. */
  public final void setProvidingList(final List newProvidingList) {
    providingList = newProvidingList;
  }
  /** �������� ������������ ���������. */
  public final boolean isCorrect() {
    if (isCE()) {
      return true;
    }
    if (providingList != null) {
      addField(PROVIDINGLIST_IDX, providingList);
      setCE(true);
    }
    return isCE();
  }

  /** �������� �� ��������� ��������������. */
  public final boolean isRoutable() {
    return false;
  }
} // ODGetProvidingReplyMessage
