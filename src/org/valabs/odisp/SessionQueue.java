/* ODISP -- Message Oriented Middleware
 * Copyright (C) 2003-2005 Valentin A. Alekseev
 * Copyright (C) 2003-2005 Andrew A. Porohin 
 * 
 * ODISP is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, version 2.1 of the License.
 * 
 * ODISP is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with ODISP.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.valabs.odisp;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import org.valabs.odisp.common.Dispatcher;
import org.valabs.odisp.common.Message;
import org.valabs.odisp.common.MessageHandler;


/** 
 * ���������� ���������������� ������� ��������� � ������ ������. � ������
 * ���������� ������ ��������� ���������� ���������������, ����� ��������� ������
 * �� ���������� ���������, ������ ��� �� ����� ���� �������� ��� ţ
 * ������������������ ����� ���� �������������� � ������ ������ ��� ���������
 * ������.
 * 
 * <code>
 * 
 * 
 * SessionQueue sq = new SessionQueue();
 * Message m1 = dispatcher.getNewMessage();
 * TypicalMessage1.setup(m1, ....);
 * sq.addMessage(m1, new SessionManager.MessageHandlerEx() {
 *   public int messageReceived(Message msg, SessionQueue sq) {
 *      ... msg - �������� ���������, sq - ������ �� ������
 *      return 0; -- ���������� �������� (0 - ������ ������, �������� - ������ ������ ���������)
 *   }
 * });
 * 
 * -- ���������� � ������ � ��������� ������������. 
 * 
 * Message m2 = dispatcher.getNewMessage();
 * TypicalMessage2.setup(m2, ....);
 * sq.addMessage(m2, new SessionManager.MessageHandlerEx() {
 *   public int messageReceived(Message msg, SessionQueue sq) {
 *      if (ReplyTimeoutMessage.equals(msg)) {
 *         ...
 *         return -1;
 *      }
 *      ...
 *      return 0;   
 *   }
 * }, 5);
 * 
 * Message m3 = dispatcher.getNewMessage();
 * TypicalMessage3.setup(m1, ....);
 * sq.addMessage(m3, new SessionManager.MessageHandlerEx() {
 *   public int messageReceived(Message msg, SessionQueue sq) {
 *      if (...) {
 *         Message m4 = dispatcher.getNewMessage();
 *         TypicalMessage4.setup(m4, ....);
 *         sq.addMessageAfterCurrent(m4, new SessionManager.MessageHandlerEx() {
 *           public int messageReceived(Message msg, SessionQueue sq) {
 *              return 0;
 *           }
 *         });
 *      }
 *   }
 * });
 * 
 * -- �� �������� ��������� =)
 * -- ������ ���������� � ������ ���� �� ������ ��� � ���������.
 * sq.run();
 * </code>
 * 
 * @see org.valabs.odisp.SessionManager
 * 
 * @author <a href="dron@novel-il.ru">������ �. �������</a>
 * @author (C) 2005 ��� "�����-��"
 * @version $Id: SessionQueue.java,v 1.9 2006/04/13 14:22:44 dron Exp $
 */
public class SessionQueue implements Runnable {
  public static int ERROR_NOERROR = 0;
  public static int ERROR_ABORT = -1;
  public static int ERROR_PAUSE = -30000;
  
  /** ������ ���������. */
  private List messages = Collections.synchronizedList(new ArrayList());
  /** ������� ������ ������. */
  private int counter = 0;
  /** �������� ����� ��� SessionManager. */
  private SessionPacketReply spr;
  
  Logger logger = Logger.getLogger(SessionPacketReply.class.getName());
  SessionManager sm = SessionManager.getSessionManager();
  Dispatcher dispatcher;
  
  /**
   * �����������. 
   *
   * @param _dispatcher ���������.
   */
  public SessionQueue(Dispatcher _dispatcher) {
    dispatcher = _dispatcher;
    spr = new SessionPacketReply(this, sm);
  }
  
  /**
   * �������� ��������� � �����.
   * 
   * @param msg ���������.
   * @param mh_reply �������� �����.
   * @param timeOut ��������� ����������� (� �������� -- timeCycle ~ 2-3 ���)
   * @see SessionManager
   */
  public MessageData addMessage(Message msg, MessageHandlerEx mh_reply, int timeOut) {
    if (mh_reply == null) {
      throw new InvalidParameterException("Null mh_reply passed.");
    }
    MessageData newPacket = new MessageData();
    newPacket.msg = msg;
    newPacket.mh_reply = mh_reply;
    newPacket.timeOut = timeOut;
    messages.add(newPacket);
    
    return newPacket;
  }
  
  /**
   * �������� ��������� � ����� ��� ���������� �����������.
   * 
   * @param msg ���������.
   * @param mh_reply �������� �����.
   */
  public MessageData addMessage(Message msg, MessageHandlerEx mh_reply) {
    return addMessage(msg, mh_reply, 0);
  }
  
  /**
   * �������� ��������� ����� ��������.
   * 
   * @param msg ���������.
   * @param mh_reply �������� �����.
   * @param timeOut ��������� ����������� (� �������� -- timeCycle ~ 2-3 ���)
   */
  public MessageData addMessageAfterCurrent(Message msg, MessageHandlerEx mh_reply, int timeOut) {
    if (mh_reply == null) {
      throw new InvalidParameterException("Null mh_reply passed.");
    }
    MessageData newPacket = new MessageData();
    newPacket.msg = msg;
    newPacket.mh_reply = mh_reply;
    newPacket.timeOut = timeOut;
    messages.add(0, newPacket);
    
    return newPacket;
  }
  
  /**
   * �������� ��������� ����� ��������.
   * 
   * @param msg ���������.
   * @param mh_reply �������� �����.
   */
  public MessageData addMessageAfterCurrent(Message msg, MessageHandlerEx mh_reply) {
    return addMessageAfterCurrent(msg, mh_reply, 0);
  }
  
  /**
   * �������� ��������� ����� ����������.
   * 
   * @param after ����� ������ ����������� ��������.
   * @param msg C��������.
   * @param mh_reply ����������.
   */
  public void addMessageAfter(MessageData after, Message msg, MessageHandlerEx mh_reply) {
    addMessageAfter(after, msg, mh_reply, 0);
  }
  
  /**
   * �������� ��������� ����� ����������.
   * 
   * @param after ����� ������ ����������� ��������.
   * @param msg C��������.
   * @param mh_reply ����������.
   * @param timeOut ����� �������� ������.
   */
  public void addMessageAfter(MessageData after, Message msg, MessageHandlerEx mh_reply, int timeOut) {
    MessageData newPacket = new MessageData();
    newPacket.msg = msg;
    newPacket.mh_reply = mh_reply;
    newPacket.timeOut = timeOut;
    
    int index = 0;
    for(Iterator it = messages.iterator(); it.hasNext(); ) {
      index++;
      MessageData mData = (MessageData) it.next();
      if (mData.equals(after)) {
        break;
      }
    }
    
    messages.add(index, newPacket);
  }
  
  /**
   * �������� ������� ��������� �������.
   * @return ����������� �������� ��������������� �������� ���������.
   */
  public int getCurrentState() {
    return counter;
  }
  
  /**
   * �������� ������ �� ��������� ���������.
   * @return ��������� ��������� ��������� � ��������������� ����������.s
   */
  public MessageData getNextMessage() {
    return (messages.size() > 0) ? (MessageData) messages.get(0) : null;
  }
  
  /**
   * ��������� ������. ������ ���������� � ������ ���� �� ������.
   */
  public void run() {
    synchronized (messages) {
      spr.currentData = getNextMessage();
      if (spr.currentData != null) {
        messages.remove(0);
        if (spr.currentData.msg != null) {
          if (spr.currentData.timeOut > 0) {
            sm.addMessageListenerWithTimeout(spr.currentData.msg.getId(), spr, spr.currentData.timeOut);
          } else {
            sm.addMessageListener(spr.currentData.msg.getId(), spr);
          }
          dispatcher.send(spr.currentData.msg);
        } else {
          spr.messageReceived(null);
        }
        counter++;
      }
    }
  }
  
  /**
   * ������ ������. �������� ���� ������������, ��������� � ������ �������, �������
   * ������ ������. ������ ����� �� ���������������.
   */
  public void cancelSession() {
    for (Iterator it = messages.iterator(); it.hasNext(); ) {
      MessageData md = (MessageData) it.next();
      sm.removeMessageListener(md.msg.getId(), spr);
      
      it.remove();
    }
  }
  
  /**
   * ����������� ���������� ���������. �������� ��������� � ������ � ������
   * �� {@link SessionQueue}.
   */
  public interface MessageHandlerEx {
    /**
     * @param msg
     * @param sq
     * @return ����� ��������� ���� ������. (ERROR_NOERROR - ��� ������, ERROR_PAUSE - ����������
     * ������ ���������� ���������, ������������ ��� ����������� ������� ��������� �������).
     * @see SessionQueue#ERROR_ABORT
     * @see SessionQueue#ERROR_NOERROR
     * @see SessionQueue#ERROR_PAUSE
     */
    public int messageReceived(Message msg, SessionQueue sq);
  }
  
  /**
   * ��������� ��� ���������.
   */
  public class MessageData {
    public Message msg;
    public MessageHandlerEx mh_reply;
    public int timeOut = 0;
    
    public boolean equals(MessageData mData) {
      return msg != null && msg.equals(mData.msg) && timeOut == mData.timeOut;
    }
  }
  
  /**
   * �������� ����� ��� SessionManager'�.
   */
  class SessionPacketReply implements MessageHandler {
    public SessionQueue sp;
    public SessionManager sm;
    public MessageData currentData;
    
    public SessionPacketReply(SessionQueue _sp, SessionManager _sm) {
      sp = _sp;
      sm = _sm;
    }
    
    public void messageReceived(Message msg) {
      logger.finest("[DBG]: SPR, SM_reply, currentState = " + sp.getCurrentState());
      int errCode = -1;
      if ((errCode = currentData.mh_reply.messageReceived(msg, sp)) == 0) {
        sp.run();
      } else {
        if (errCode != ERROR_PAUSE) {
          logger.warning("SPR, SM_reply: handler return " + errCode + "\n" +
                  " + Session aborted at currentState = " + getCurrentState() + "\n" +
                  " + NextMessage = " + getNextMessage());
        } else {
          logger.finest("SPR, SM_reply: running paused, awating for next reply.");
        }
      }
    }
  }
}

