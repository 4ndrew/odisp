package com.novel.stdmsg;

/** ����� ����������� ��������� � �������� �������.
 * ������ ��������� �� ����� �� ����� �������������� �����, �� ����� ��� �� ���������
 * ���� ������� ����� StandartMessage.
 * @author <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @author (C) 2003, ��� "�����-��"
 * @version $Id: ODObjectLoadedMessage.java,v 1.3 2003/12/04 09:48:20 valeks Exp $
 */

public class ODObjectLoadedMessage extends StandartMessage {
  /** ���������� ��� ���������*/
  public static final String name = "od_object_loaded";
  /** ����������� ��������� � �������� �������
   * @param destination ���������� ���������
   */
  public ODObjectLoadedMessage(String destination) {
    super("od_object_loaded", destination, "stddispatcher", 0);
  }
}// ODObjectLoaded
