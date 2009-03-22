package org.valabs.odisp.standart5;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.valabs.odisp.common.Message;
/**
 * ��������� ���������� ���������.
 * 
 * @author (C) 2003-2004 <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @version $Id: DefferedMessages.java,v 1.3 2006/03/29 11:23:31 valeks Exp $
 */
class DefferedMessages {
  /** ����� �������� ���������. */
  private final Map<String, List<Message>> queues = new HashMap<String, List<Message>>();
  
  /**
   * ���������� ��������� � ������������ �������.
   * 
   * @param objName ��� ������� (�������).
   * @param msg ��������� ��� ����������.
   */
  public final void addMessage(final String objName, final Message msg) {
    List<Message> lmessages;
    if (queues.containsKey(objName)) {
      lmessages = queues.get(objName);
    } else {
      lmessages = new LinkedList<Message>();
      queues.put(objName, lmessages);
    }
    lmessages.add(msg);
  }
  
  /**
   * ���������� ������ ����������� ��� ������� ���������.
   * 
   * @param objectName ��� ������� (�������).
   * @return ������ ��������� �� �������.
   */
  public final List<Message> flush(final String objectName) {
    List<Message> res;
    if (queues.containsKey(objectName)) {
      res = queues.get(objectName);
      queues.remove(objectName);
    } else {
      res = new ArrayList<Message>(0);
    }
    return res;
  }
} // DefferedMessages
