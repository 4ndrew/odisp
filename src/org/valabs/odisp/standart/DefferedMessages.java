package com.novel.odisp;

import com.novel.odisp.common.Message;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
/**
 * @author <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @author (C) 2003, ��� "�����-��"
 * @version $Id: DefferedMessages.java,v 1.2 2004/02/15 21:55:56 valeks Exp $
 */
/** ��������� ���������� ���������. */
public class DefferedMessages {
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
