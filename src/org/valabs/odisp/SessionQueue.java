package org.valabs.odisp;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.valabs.odisp.common.Dispatcher;
import org.valabs.odisp.common.Message;
import org.valabs.odisp.common.MessageHandler;


/** 
 * Это экспериментальный код по реализации сессии цепочки пакетов. Пока примеры есть
 * только UpdaterGUI.java. в скором времени здесь тоже в javadoc будут примеры.
 * 
 * @author <a href="dron@novel-il.ru">Андрей А. Порохин</a>
 * @author (C) 2005 НПП "Новел-ИЛ"
 * @version $Id: SessionQueue.java,v 1.1 2005/10/13 09:32:34 dron Exp $
 */
public class SessionQueue implements Runnable {
  private List messages = new ArrayList();
  private int counter = 0;
  private SessionPacketReply spr;
  
  Logger logger = Logger.getLogger(SessionPacketReply.class.getName());
  SessionManager sm = SessionManager.getSessionManager();
  Dispatcher dispatcher;
  
  /**
   * Конструктор. 
   *
   * @param _dispatcher
   */
  public SessionQueue(Dispatcher _dispatcher) {
    dispatcher = _dispatcher;
    spr = new SessionPacketReply(this, sm);
  }
  
  /**
   * Добавить сообщение в пакет.
   * 
   * @param msg
   * @param mh_reply
   */
  public void addMessage(Message msg, MessageHandlerEx mh_reply) {
    if (mh_reply == null) {
      throw new InvalidParameterException("Null mh_reply passed.");
    }
    MessageData newPacket = new MessageData();
    newPacket.msg = msg;
    newPacket.mh_reply = mh_reply;
    messages.add(newPacket);
  }
  
  public int getCurrentState() {
    return counter;
  }
  
  public MessageData getNextMessage() {
    return (messages.size() > 0) ? (MessageData) messages.get(0) : null;
  }
  
  public void run() {
    synchronized (messages) {
      spr.currentData = getNextMessage();
      if (spr.currentData != null) {
        if (spr.currentData.msg != null) {
          sm.addMessageListener(spr.currentData.msg.getId(), spr);
          dispatcher.send(spr.currentData.msg);
          messages.remove(0);
        } else {
          spr.messageReceived(null);
        }
      }
    }
  }
  
  /**
   * Расширенный обработчик сообщений. Добавлен результат у метода и ссылка
   * на {@link SessionQueue}.
   */
  public interface MessageHandlerEx {
    public int messageReceived(Message msg, SessionQueue sq);
  }
  
  /**
   * Контейнер для сообщения.
   */
  public class MessageData {
    public Message msg;
    public MessageHandlerEx mh_reply;
  }
  
  /**
   * Ответная часть для SessionManager'а.
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
        logger.warning("SPR, SM_reply: handler return " + errCode + "\n" +
                " + Session aborted at currentState = " + getCurrentState() + "\n" +
                " + NextMessage = " + getNextMessage());
      }
    }
  }
}

