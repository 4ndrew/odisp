package com.novel.stdmsg;

import com.novel.odisp.common.Message;

/** ������ ������ ��������� �������� ����������.
 * @author <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @author (C) 2003, ��� "�����-��"
 * @version $Id: ODGetProvidingMessage.java,v 1.1 2004/03/27 19:40:18 valeks Exp $
 */

public class ODGetProvidingMessage extends StandartMessage {
  /** ������������� ��� ���������. */
  public static final String NAME = "od_get_providing";
  /** ���������� �����������.
   * @param msg ���������� ���������
   */
  public ODGetProvidingMessage(final Message msg) {
    super(msg);
  }

  /** �������� �� ��������� ��������������. */
  public final boolean isRoutable() {
    return false;
  }
} // ODGetProvidingMessage
