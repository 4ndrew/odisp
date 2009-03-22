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
 * Реализация последовательной очереди сообщений в рамках сессии. В данной
 * реализации каждое сообщение посылаются последовательно, после получения ответа
 * на предыдущее сообщение, сессия так же может быть прервана или её
 * последовательность может быть модифицирована в случае ошибки при получении
 * ответа.
 * 
 * <code>
 * 
 * 
 * SessionQueue sq = new SessionQueue();
 * Message m1 = dispatcher.getNewMessage();
 * TypicalMessage1.setup(m1, ....);
 * sq.addMessage(m1, new SessionManager.MessageHandlerEx() {
 *   public int messageReceived(Message msg, SessionQueue sq) {
 *      ... msg - ответное сообщение, sq - ссылка на сессию
 *      return 0; -- возращение значения (0 - статус хорошо, отличное - сессия должна прерватся)
 *   }
 * });
 * 
 * -- добавление в сессию с временным ограничением. 
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
 * -- не забываем запускать =)
 * -- запуск происходит в рамках того же потока что и запускает.
 * sq.run();
 * </code>
 * 
 * @see org.valabs.odisp.SessionManager
 * 
 * @author <a href="dron@novel-il.ru">Андрей А. Порохин</a>
 * @author (C) 2005 НПП "Новел-ИЛ"
 * @version $Id: SessionQueue.java,v 1.9 2006/04/13 14:22:44 dron Exp $
 */
public class SessionQueue implements Runnable {
  public static int ERROR_NOERROR = 0;
  public static int ERROR_ABORT = -1;
  public static int ERROR_PAUSE = -30000;
  
  /** Список сообщений. */
  private List messages = Collections.synchronizedList(new ArrayList());
  /** Текущая стадия работы. */
  private int counter = 0;
  /** Ответная часть для SessionManager. */
  private SessionPacketReply spr;
  
  Logger logger = Logger.getLogger(SessionPacketReply.class.getName());
  SessionManager sm = SessionManager.getSessionManager();
  Dispatcher dispatcher;
  
  /**
   * Конструктор. 
   *
   * @param _dispatcher Диспетчер.
   */
  public SessionQueue(Dispatcher _dispatcher) {
    dispatcher = _dispatcher;
    spr = new SessionPacketReply(this, sm);
  }
  
  /**
   * Добавить сообщение в пакет.
   * 
   * @param msg Сообщение.
   * @param mh_reply Ответная часть.
   * @param timeOut Временное ограничение (в попугаях -- timeCycle ~ 2-3 сек)
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
   * Добавить сообщение в пакет без временного ограничения.
   * 
   * @param msg Сообщение.
   * @param mh_reply Ответная часть.
   */
  public MessageData addMessage(Message msg, MessageHandlerEx mh_reply) {
    return addMessage(msg, mh_reply, 0);
  }
  
  /**
   * Добавить сообщение после текущего.
   * 
   * @param msg Сообщение.
   * @param mh_reply Ответная часть.
   * @param timeOut Временное ограничение (в попугаях -- timeCycle ~ 2-3 сек)
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
   * Добавить сообщение после текущего.
   * 
   * @param msg Сообщение.
   * @param mh_reply Ответная часть.
   */
  public MessageData addMessageAfterCurrent(Message msg, MessageHandlerEx mh_reply) {
    return addMessageAfterCurrent(msg, mh_reply, 0);
  }
  
  /**
   * Добавить сообщение после указанного.
   * 
   * @param after После какого обработчика добавить.
   * @param msg Cообщение.
   * @param mh_reply Обработчик.
   */
  public void addMessageAfter(MessageData after, Message msg, MessageHandlerEx mh_reply) {
    addMessageAfter(after, msg, mh_reply, 0);
  }
  
  /**
   * Добавить сообщение после указанного.
   * 
   * @param after После какого обработчика добавить.
   * @param msg Cообщение.
   * @param mh_reply Обработчик.
   * @param timeOut Время ожидания ответа.
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
   * Получить текущее положение очереди.
   * @return возращается значение соответствующее текущему положению.
   */
  public int getCurrentState() {
    return counter;
  }
  
  /**
   * Получить ссылку на следующее сообщение.
   * @return Стркутура содержаще сообщение и вспомогательную информацию.s
   */
  public MessageData getNextMessage() {
    return (messages.size() > 0) ? (MessageData) messages.get(0) : null;
  }
  
  /**
   * Запустить сессию. Запуск происходит в рамках того же потока.
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
   * Отмена сессии. Удаление всех обработчиков, связанных с данной сессией, очистка
   * данных сессии. Данный вызов не синхронизирован.
   */
  public void cancelSession() {
    for (Iterator it = messages.iterator(); it.hasNext(); ) {
      MessageData md = (MessageData) it.next();
      sm.removeMessageListener(md.msg.getId(), spr);
      
      it.remove();
    }
  }
  
  /**
   * Расширенный обработчик сообщений. Добавлен результат у метода и ссылка
   * на {@link SessionQueue}.
   */
  public interface MessageHandlerEx {
    /**
     * @param msg
     * @param sq
     * @return Может возращать коды ошибок. (ERROR_NOERROR - нет ошибки, ERROR_PAUSE - пропустить
     * запуск следующего сообщения, используется для продолжения очереди сторонним методом).
     * @see SessionQueue#ERROR_ABORT
     * @see SessionQueue#ERROR_NOERROR
     * @see SessionQueue#ERROR_PAUSE
     */
    public int messageReceived(Message msg, SessionQueue sq);
  }
  
  /**
   * Контейнер для сообщения.
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

