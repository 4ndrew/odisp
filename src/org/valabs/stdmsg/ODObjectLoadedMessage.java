package com.novel.stdmsg;

/** ����� ����������� ��������� � �������� �������.
 * ������ ��������� �� ����� �� ����� �������������� �����,
 * �� ����� ��� �� ��������� ���� ������� ����� StandartMessage.
 * @author <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @author (C) 2003, ��� "�����-��"
 * @version $Id: ODObjectLoadedMessage.java,v 1.6 2004/02/24 00:35:14 valeks Exp $
 */

public class ODObjectLoadedMessage extends StandartMessage {
  /** ���������� ��� ���������. */
  public static final String NAME = "od_object_loaded";
  /** ����������� ��������� � �������� �������.
   * @param destination ���������� ���������
   */
  public ODObjectLoadedMessage(final String destination) {
    super("od_object_loaded", destination, "stddispatcher", 0);
  }

  public final boolean isRoutable() {
    return false;
  }

} // ODObjectLoaded
