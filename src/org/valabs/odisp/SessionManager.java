package org.valabs.odisp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.doomdark.uuid.UUID;
import org.valabs.odisp.common.Message;
import org.valabs.odisp.common.MessageHandler;

/**
 * <h5>������������� ODsessionManager'�:</h5>
 * 
 * <p> �������� ������ �������� � ������ ���� ���, � ������ ������
 * ������������ Odisp (��� �������� ������������� ������). ��������� ����
 * ������������� ����: 
 * 
 * <pre>
 * # ����� ������� ���������: 
 * Message newMsg = dispatcher.getNewMessage(...);
 * ...
 * ODSessionManager sm = ODSessionManager.getSessionManager();
 * sm.add(newMsg.getId(), new MessageHandler() {
 *    public void messageReceived(Message msg) {
 *      System.out.println("Some reply-message received!");
 *    }
 * });
 * 
 * ...
 * 
 * handleMessage(Message msg) {
 *   ODSessionManager sm = ODSessionManager.getSessionManager();
 *   if (!sm.processMessage(msg)) {
 *     System.out.println("Message processed by SessionManager");
 *   } else if (ODObjectLoaded.equals(msg)) {
 *     ...
 *   }
 *   ...
 * }
 * </pre>
 * 
 * @author (C) 2004 <a href="dron@novel-il.ru">������ �. �������</a>
 * @version $Id: SessionManager.java,v 1.9 2004/11/05 14:11:29 valeks Exp $ 
 */
public class SessionManager {
  /** ������ �� ODSessionManager */
  private static SessionManager sessionManager = new SessionManager();
  /** ������� msgId <=> ���������� (MessageHandler) */
  private Map handlers = new HashMap();
  
  /** �����������
   */
  protected SessionManager() {
    super();
  }
  
  /** �������� ������� ��������
   * 
   * @return �������� ������.
   */
  public static SessionManager getSessionManager() {
    return sessionManager;
  }
  
  /** �������� ���������� � ������.
   * 
   * @param messageId ������������� ���������, ������� ���� �������.
   * @see org.valabs.odisp.common.Message#getId()
   * @param messageHandler ���������� ��� ���������.
   */
  public void addMessageListener(UUID messageId,
                                 MessageHandler messageHandler) {
    handlers.put(messageId, messageHandler);
  }
  
  /** ������� ��������� ��������� Odisp. �������������� �������� ��
   * messageRecevied � handleMessage. 
   * @param msg ��������� Odisp
   * @return ���� � ������ ��������� ������ ���������� ���������� ��� �������
   * ���������, �� ���������� true, � ��������� ������ ������ ���������,
   * ������� �� ������� �� ��� ���� ����� (���������� ���������������
   * ���������).
   */
  public boolean processMessage(Message msg) {
    boolean matched = false;     
    Map toPerform;
    List toRemove = new ArrayList();
    synchronized(handlers) {
      toPerform = new HashMap(handlers);
    }
    Iterator it = toPerform.keySet().iterator();
    while (it.hasNext()) {
      UUID key = (UUID) it.next();
      if (key.equals(msg.getReplyTo())) {
        ((MessageHandler) toPerform.get(key)).messageReceived(msg);
        toRemove.add(key);
      }
    } // while
    synchronized(handlers) {
      Iterator ri = toRemove.iterator();
      while (ri.hasNext()) {
        handlers.remove(ri.next());
      }
    }
    return matched;
  }

}
