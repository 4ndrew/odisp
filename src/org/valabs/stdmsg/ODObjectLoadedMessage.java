package com.novel.stdmsg;

import com.novel.odisp.common.Message;
/** ����� ����������� ��������� � �������� �������.
 * ������ ��������� �� ����� �� ����� �������������� �����,
 * �� ����� ��� �� ��������� ���� ������� ����� StandartMessage.
 * @author <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @author (C) 2003, ��� "�����-��"
 * @version $Id: ODObjectLoadedMessage.java,v 1.8 2004/06/09 00:28:48 valeks Exp $
 */

public class ODObjectLoadedMessage {
  /** ���������� ��� ���������. */
  public static final String NAME = "od_object_loaded";
  /** ���������� ��������� � ��������.
   * @param destination ���������� ���������
   */
  public static void setup(final Message msg) {
    msg.setAction(NAME);
    msg.setOrigin("dispatcher");
    msg.setId(0);
    msg.setRoutable(false);
  }

  public static boolean equals(final Message msg) {
    return msg.getAction().equals(NAME);
  }
} // ODObjectLoaded
