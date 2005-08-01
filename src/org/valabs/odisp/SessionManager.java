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
 * </pre>
 * 
 * @author (C) 2004 <a href="dron@novel-il.ru">������ �. ������� </a>
 * @version $Id: SessionManager.java,v 1.16 2005/08/01 08:48:31 valeks Exp $
 */
public class SessionManager {

  /** ������ �� ODSessionManager */
  private static SessionManager sessionManager = new SessionManager();

  /** ������ ������������. */
  private final List handlers = Collections.synchronizedList(new ArrayList());

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
  public void addMessageListener(final UUID messageId, final MessageHandler messageHandler) {
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
  public final void addMessageListener(final UUID messageId, final MessageHandler messageHandler, final boolean multiply) {
    handlers.add(new SessionRecord(messageId, messageHandler, multiply));
  }

  /**
   * �������� ����������� ��������� ������.
   * 
   * @param messageId ������������� ������ ���������
   * @param messageHandler ���������� ���������
   * @see SessionManager#addMessageListener(UUID, MessageHandler, boolean)
   */
  public final void removeMessageListener(final UUID messageId, final MessageHandler messageHandler) {
    final Iterator handlerIt = handlers.iterator();
    while (handlerIt.hasNext()) {
      final SessionRecord element = (SessionRecord) handlerIt.next();
      if (element.getMsgId().equals(messageId) && element.getMessageHandler().equals(messageHandler)) {
        handlerIt.remove();
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
  public final boolean processMessage(final Message msg) {
    boolean matched = false;
    final List toPerform = new ArrayList();
    final List toRemove = new ArrayList();
    Iterator commonIt;
    synchronized (handlers) {
		commonIt = handlers.iterator();
		while (commonIt.hasNext()) {
			final SessionRecord element = (SessionRecord) commonIt.next();
			if (element.getMsgId().equals(msg.getReplyTo())) {
				toPerform.add(element);
				if (!element.isMultiply()) {
					toRemove.add(element);
				}
				matched = true;
			}
		}

		commonIt = toRemove.iterator();
		while (commonIt.hasNext()) {
			final Object element = commonIt.next();
			handlers.remove(element);
		}
	}

    commonIt = toPerform.iterator();
    while (commonIt.hasNext()) {
      final SessionRecord element = (SessionRecord) commonIt.next();
      element.getMessageHandler().messageReceived(msg);
    }
    return matched;
  }

  static class SessionRecord {

    private final UUID msgId;

    private final MessageHandler messageHandler;

    private final boolean multiply;

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