package org.valabs.odisp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
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
 * @version $Id: SessionManager.java,v 1.13 2005/02/17 12:31:44 valeks Exp $
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
    Iterator it = handlers.iterator();
    while (it.hasNext()) {
      SessionRecord element = (SessionRecord) it.next();
      if (element.getMsgId().equals(messageId) && element.getMessageHandler().equals(messageHandler)) {
        it.remove();
      }
    }
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
    Iterator it = handlers.iterator();
    List toPerform = new ArrayList();
    List toRemove = new ArrayList();
    while (it.hasNext()) {
      SessionRecord element = (SessionRecord) it.next();
      if (element.getMsgId().equals(msg.getReplyTo())) {
        toPerform.add(element);
        if (!element.isMultiply()) {
          toRemove.add(element);
        }
        matched = true;
      }
    }
    it = toPerform.iterator();
    while (it.hasNext()) {
      SessionRecord element = (SessionRecord) it.next();
      element.getMessageHandler().messageReceived(msg);
    }
    it = toRemove.iterator();
    while (it.hasNext()) {
      Object element = it.next();
      handlers.remove(element);
    }
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