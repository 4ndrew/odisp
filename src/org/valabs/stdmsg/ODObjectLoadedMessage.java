package com.novel.stdmsg;

/** ����� ����������� ��������� � �������� �������.
 * ������ ��������� �� ����� �� ����� �������������� �����,
 * �� ����� ��� �� ��������� ���� ������� ����� StandartMessage.
 * @author <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @author (C) 2003, ��� "�����-��"
 * @version $Id: ODObjectLoadedMessage.java,v 1.7 2004/04/02 09:54:49 valeks Exp $
 */

public class ODObjectLoadedMessage extends StandartMessage {
  /** ���������� ��� ���������. */
  public static final String NAME = "od_object_loaded";
  /** ����������� ��������� � �������� �������.
   * @param destination ���������� ���������
   */
  public ODObjectLoadedMessage(final String destination) {
    super("od_object_loaded", destination, "dispatcher", 0);
  }

  public final boolean isRoutable() {
    return false;
  }

} // ODObjectLoaded
