package com.novel.stdmsg;

import com.novel.odisp.common.Message;

/** ������ �� ������������ ��������� ������ ��������.
 * @author <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @author (C) 2003, ��� "�����-��"
 * @version $Id: ODAddProviderMessage.java,v 1.1 2004/03/27 19:40:18 valeks Exp $
 */

public class ODAddProviderMessage extends StandartMessage {
  /** ������������� ��� ���������. */
  public static final String NAME = "od_add_provider";
  /** �������� �������. */
  private transient String serviceName = null;
  /** ��� ��������� � ���������. */
  private static String SERVICENAME_IDX = "0";
  /** ���������� �����������.
   * @param msg ���������� ���������
   */
  public ODAddProviderMessage(final Message msg) {
    super(msg);
  }
  /** ����������� ���������.
   * @param objectName ��� �������
   * @param replyId ����� ��������� �� ������� ������������ �����
   */
  public ODAddProviderMessage(final String objectName, final int replyId) {
    super(NAME, "stddispatcher", objectName, replyId);
  }
  /** ������ � ����� �������. */
  public final String getServiceName() {
    if (isCE()) {
      return (String) getField(SERVICENAME_IDX);
    }
    return serviceName;
  }
  /** ��������� ����� �������. */
  public final void setSerivceName(final String newServiceName) {
    serviceName = newServiceName;
  }
  /** �������� ������������ ���������. */
  public final boolean isCorrect() {
    if (isCE()) {
      return true;
    }
    if (serviceName != null) {
      addField(SERVICENAME_IDX, serviceName);
      setCE(true);
    }
    return isCE();
  }

  /** �������� �� ��������� ��������������. */
  public final boolean isRoutable() {
    return false;
  }
} // ODAddProviderMessage
