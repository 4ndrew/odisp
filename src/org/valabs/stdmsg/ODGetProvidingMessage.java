package com.novel.stdmsg;

import com.novel.odisp.common.Message;

/** ������ ������ ��������� �������� ����������.
 * @author <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @author (C) 2004, ��� "�����-��"
 * @version $Id: ODGetProvidingMessage.java,v 1.3 2004/06/09 18:55:57 valeks Exp $
 */

public class ODGetProvidingMessage {
  /** ������������� ��� ���������. */
  public static final String NAME = "od_get_providing";
  /** ���������� �����������.
   * @param msg ���������� ���������
   */
  public static final void setup(final Message msg) {
    msg.setAction(NAME);
    msg.setDestination("dispatcher");
    msg.setRoutable(false);
    msg.setCorrect(true);
  }
  
  public static final boolean equals(final Message msg) {
    return msg.getAction().equals(NAME);
  }
} // ODGetProvidingMessage
