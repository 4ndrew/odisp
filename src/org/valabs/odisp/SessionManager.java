package org.valabs.odisp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.doomdark.uuid.UUID;
import org.valabs.odisp.common.Message;
import org.valabs.odisp.common.MessageHandler;

/**
 * <h5>������������� ODsessionManager'�:</h5>
 * <p>
 * �������� ������ �������� � ������ ���� ���, � ������ ������ ������������ Odisp (��� �������� �������������
 * ������). ��������� ���� ������������� ����:
 * 
 * <pre>
 * 
 *  
 *   
 *    
 *     
 *      
 *       # ����� ������� ���������: 
 *       Message newMsg = dispatcher.getNewMessage(...);
 *       ...
 *       ODSessionManager sm = ODSessionManager.getSessionManager();
 *       sm.add(newMsg.getId(), new MessageHandler() {
 *          public void messageReceived(Message msg) {
 *            System.out.println(&quot;Some reply-message received!&quot;);
 *          }
 *       });
 *       
 *       ...
 *       
 *       handleMessage(Message msg) {
 *         ODSessionManager sm = ODSessionManager.getSessionManager();
 *         if (!sm.processMessage(msg)) {
 *           System.out.println(&quot;Message processed by SessionManager&quot;);
 *         } else if (ODObjectLoaded.equals(msg)) {
 *           ...
 *         }
 *         ...
 *       }
 *       
 *      
 *     
 *    
 *   
 *  
 * </pre>
 * 
 * @author (C) 2004 <a href="dron@novel-il.ru">������ �. ������� </a>
 * @version $Id: SessionManager.java,v 1.11 2004/12/01 10:32:59 boris Exp $
 */
public class SessionManager {

  /** ������ �� ODSessionManager */
  private static SessionManager sessionManager = new SessionManager();

  /** ������ ������������. */
  private List handlers = Collections.synchronizedList(new ArrayList());

  /**
   * �����������
   */
  protected SessionManager() {
    super();
  }

  /**
   * �������� ������� ��������
   * 
   * @return �������� ������.
   */
  public static SessionManager getSessionManager() {
    return sessionManager;
  }

  /**
   * �������� ���������� � ������. ��-��������� -- ���������� � �������������� �������� ����� ���������.
   * 
   * @param messageId ������������� ���������, ������� ���� �������.
   * @see org.valabs.odisp.common.Message#getId()
   * @param messageHandler ���������� ��� ���������.
   */
  public void addMessageListener(UUID messageId, MessageHandler messageHandler) {
    addMessageListener(messageId, messageHandler, false);
  }

  /**
   * ���������� ����������� ��� ���������� ���������. � ������� �� ����������� �������� ���� ��������
   * (multiply ���������� � true) ����� ��������� ��������� ������������ ������ � ������ �� SessionManager ��
   * ���������. ��� ����, ��� �� ������� ţ ������� ���������� ��������������� ������� removeMessageListener.
   * 
   * @param messageId ������������� ������ ���������
   * @param messageHandler ���������� ���������
   * @param multiply ������� ���������������
   * @see SessionManager#removeMessageListener(UUID, MessageHandler)
   */
  public void addMessageListener(UUID messageId, MessageHandler messageHandler, boolean multiply) {
    handlers.add(new SessionRecord(messageId, messageHandler, multiply));
  }

  /**
   * �������� ����������� ��������� ������.
   * 
   * @param messageId ������������� ������ ���������
   * @param messageHandler ���������� ���������
   * @see SessionManager#addMessageListener(UUID, MessageHandler, boolean)
   */
  public void removeMessageListener(UUID messageId, MessageHandler messageHandler) {
    List toDelete = new ArrayList();

    for (int i = 0; i < handlers.size(); i++) {
      SessionRecord arecord = (SessionRecord) handlers.get(i);
      if (arecord.getMsgId().equals(messageId) && arecord.getMessageHandler().equals(messageHandler)) {
        toDelete.add(new Integer(i));
      }
    }
    if (toDelete.size() > 0) {
      for (int i = 0; i < toDelete.size(); i++) {
        handlers.remove(((Integer) toDelete.get(i)).intValue());
      }
    }

    //    List tmp;
    //    synchronized (handlers) {
    //      tmp = new ArrayList(handlers);
    //    }
    //    Iterator it = tmp.iterator();
    //    while (it.hasNext()) {
    //      SessionRecord rec = (SessionRecord) it.next();
    //      if (rec.getMsgId().equals(messageId) && rec.getMessageHandler().equals(messageHandler)) {
    //        it.remove();
    //      }
    //    }
    //    synchronized (handlers) {
    //      handlers = tmp;
    //    }
  }

  /**
   * ������� ��������� ��������� Odisp. �������������� �������� �� messageRecevied � handleMessage.
   * 
   * @param msg ��������� Odisp
   * @return ���� � ������ ��������� ������ ���������� ���������� ��� ������� ���������, �� ���������� true, �
   *         ��������� ������ ������ ���������, ������� �� ������� �� ��� ���� ����� (����������
   *         ��������������� ���������).
   */
  public boolean processMessage(Message msg) {
    boolean matched = false;

    List toDelete = new ArrayList();
    List toPerform = new ArrayList();
    for (int i = 0; i < handlers.size(); i++) {
      SessionRecord arecord = (SessionRecord) handlers.get(i);
      if (arecord.getMsgId().equals(msg.getReplyTo())) {
        if (!arecord.isMultiply()) {
          toDelete.add(new Integer(i));
        }
        toPerform.add(((SessionRecord) handlers.get(i)).getMessageHandler());
        matched = true;
      }
    }
    if (matched) {
      for (int i = 0; i < toDelete.size(); i++) {
        handlers.remove(((Integer) toDelete.get(i)).intValue());
      }
      for (int i = 0; i < toPerform.size(); i++) {
        ((MessageHandler) toPerform.get(i)).messageReceived(msg);
      }
    }

    //    List toPerform;
    //    synchronized (handlers) {
    //      toPerform = new ArrayList(handlers);
    //    }
    //    Iterator it = toPerform.iterator();
    //    while (it.hasNext()) {
    //      SessionRecord arecord = (SessionRecord) it.next();
    //      if (arecord.getMsgId().equals(msg.getReplyTo())) {
    //        arecord.getMessageHandler().messageReceived(msg);
    //        if (!arecord.isMultiply()) {
    //          it.remove();
    //        }
    //        matched = true;
    //      }
    //    } // while
    //    synchronized (handlers) {
    //      handlers = toPerform;
    //    }

    return matched;
  }

  class SessionRecord {

    private UUID msgId;

    private MessageHandler messageHandler;

    private boolean multiply;

    SessionRecord(UUID _msgId, MessageHandler _messageHandler, boolean _multiply) {
      msgId = _msgId;
      messageHandler = _messageHandler;
      multiply = _multiply;
    }

    MessageHandler getMessageHandler() {
      return messageHandler;
    }

    UUID getMsgId() {
      return msgId;
    }

    boolean isMultiply() {
      return multiply;
    }
  }
}