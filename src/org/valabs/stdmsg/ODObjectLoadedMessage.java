package com.novel.stdmsg;

/** ����� ����������� ��������� � �������� �������.
 * ������ ��������� �� ����� �� ����� �������������� �����,
 * �� ����� ��� �� ��������� ���� ������� ����� StandartMessage.
 * @author <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @author (C) 2003, ��� "�����-��"
 * @version $Id: ODObjectLoadedMessage.java,v 1.5 2004/01/16 14:31:57 valeks Exp $
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
} // ODObjectLoaded
