package com.novel.stdmsg;

import com.novel.odisp.*;
import com.novel.odisp.common.*;

/** ����� ����������� ��������� � �������� �������.
 * ������ ��������� �� ����� �� ����� �������������� �����, �� ����� ��� �� ���������
 * ���� ������� ����� StandartMessage.
 * @author <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @author (C) 2003, ��� "�����-��"
 * @version $Id: ODObjectLoadedMessage.java,v 1.1 2003/12/01 22:19:29 valeks Exp $
 */

public class ODObjectLoadedMessage extends StandartMessage {
  public ODObjectLoadedMessage(String destination, String origin, int replyTo) {
    super("od_object_loaded", destination, origin, replyTo);
  }
}// ODObjectLoaded
