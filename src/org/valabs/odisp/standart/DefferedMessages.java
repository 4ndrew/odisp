package com.novel.odisp.standart;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.novel.odisp.common.Message;
/**
 * @author (C) 2003-2004 <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @version $Id: DefferedMessages.java,v 1.5 2004/08/18 12:48:40 valeks Exp $
 */
/** ��������� ���������� ���������. */
class DefferedMessages {
  /** ����� �������� ���������. */
  private Map queues = new HashMap();
  /** ���������� ��������� � ������������ �������.
   * @param objName ��� ������� (�������)
   * @param m ��������� ��� ����������
   */
  public final void addMessage(final String objName, final Message m) {
    List lmessages;
    if (!queues.containsKey(objName)) {
      lmessages = new ArrayList();
      queues.put(objName, lmessages);
    } else {
      lmessages = (List) queues.get(objName);
    }
    lmessages.add(m);
  }
  /** ���������� ������ ����������� ��� ������� ���������.
   * @param objectName ��� ������� (�������)
   * @return ������ ��������� �� �������
   */
  public final List flush(final String objectName) {
    if (queues.containsKey(objectName)) {
      List res = new ArrayList((List) queues.get(objectName));
      queues.remove(objectName);
      return res;
    } else {
      return new ArrayList();
    }
  }
} // DefferedMessages
