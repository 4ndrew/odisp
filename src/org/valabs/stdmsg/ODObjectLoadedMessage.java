package com.novel.stdmsg;

/** ����� ����������� ��������� � �������� �������.
 * ������ ��������� �� ����� �� ����� �������������� �����, �� ����� ��� �� ���������
 * ���� ������� ����� StandartMessage.
 * @author <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @author (C) 2003, ��� "�����-��"
 * @version $Id: ODObjectLoadedMessage.java,v 1.2 2003/12/03 19:17:53 valeks Exp $
 */

public class ODObjectLoadedMessage extends StandartMessage {
  public ODObjectLoadedMessage(String destination) {
    super("od_object_loaded", destination, "stddispatcher", 0);
  }
}// ODObjectLoaded
