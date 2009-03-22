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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.doomdark.uuid.UUID;
import org.valabs.odisp.common.Message;
import org.valabs.odisp.common.MessageHandler;
import org.valabs.stdmsg.ReplyTimeoutMessage;
import org.valabs.stdmsg.StandartMessage;

/**
 * <h5>Использование ODsessionManager'а:</h5>
 * <p>
 * Менеджер сессий хранится в памяти один раз, в рамках одного пространства ODISP (для экономии использования
 * памяти). Примерный план использования ниже:
 * 
 * <pre>
 *       # После посылки сообщения: 
 *       Message newMsg = dispatcher.getNewMessage(...);
 *       ...
 *       ODSessionManager sm = ODSessionManager.getSessionManager();
 *       sm.add(newMsg.getId(), new MessageHandler() {
 *          public void messageReceived(Message msg) {
 *            System.out.println(&quot;Some reply-message received!&quot;);
 *          }
 *       });
 * </pre>
 * 
 * @author (C) 2004-2005 <a href="dron@novel-il.ru">Андрей А. Порохин </a>
 * @version $Id: SessionManager.java,v 1.20 2006/07/04 10:51:41 loki Exp $
 */
public class SessionManager {
  /** Период, через который запускается проверка превышения ожидания. */
  public static final int DELAY_PERIOD = 2000;
  
  /** Ссылка на ODSessionManager */
  private static SessionManager sessionManager = new SessionManager();
  /** Список обработчиков. */
  private final List handlers = Collections.synchronizedList(new ArrayList());
  /** */
  private Timer timer = new Timer(true);

  /**
   * Конструктор.
   */
  protected SessionManager() {
    super();
    
    TimerTask timerTask = new TimerTask() {
      public void run() {
        synchronized (handlers) {
          Iterator it = handlers.iterator();
          while (it.hasNext()) {
            SessionRecord el = (SessionRecord) it.next();
            if (el.cycle != -1) el.cycle--;
            if (el.cycle == 0) {
              /*
               * Не хочется протаскивать dispatcher в SessionManager, поэтому сообщение доносится в обход
               * dispatcher'а через обработчик.
               */
              Message timeout = new StandartMessage();
              ReplyTimeoutMessage.setup(timeout, "", "sessionManager", el.getMsgId());
              el.messageHandler.messageReceived(timeout);
              it.remove();
            }
          }
        }
      }
    };
    timer.schedule(timerTask, 1000, DELAY_PERIOD);
  }

  /**
   * Получить текущий менеджер.
   * 
   * @return Менеджер сессий.
   */
  public static SessionManager getSessionManager() {
    return sessionManager;
  }

  /**
   * Добавить обработчик в список. По-умолчанию -- добавление с автоматическим удаление после обработки.
   * 
   * @param messageId Идентификатор сообщения, которое было послано.
   * @see org.valabs.odisp.common.Message#getId()
   * @param messageHandler Обработчик для сообщения.
   */
  public void addMessageListener(final UUID messageId, final MessageHandler messageHandler) {
    addMessageListener(messageId, messageHandler, false);
  }

  /**
   * Добавление обработчика для указанного сообщения. В отличие от предыдущего варианта если параметр
   * (multiply установлен в true) после обработки сообщения обработчиком запись о сессии из SessionManager не
   * удаляется. Для того, что бы удалить её вручную необходимо воспользоваться методом removeMessageListener.
   * 
   * @param messageId идентификатор ответа сообщения.
   * @param messageHandler обработчик сообщения.
   * @param multiply признак множественности.
   * @see SessionManager#removeMessageListener(UUID, MessageHandler)
   */
  public final void addMessageListener(final UUID messageId, final MessageHandler messageHandler, final boolean multiply) {
    handlers.add(new SessionRecord(messageId, messageHandler, multiply, -1));
  }
  
  /**
   * 
   * @param messageId идентификатор ответа сообщения.
   * @param messageHandler обработчик сообщения.
   * @param timeoutCycle Количество проходом таймера перед тем, как пошлётся 
   */
  public final void addMessageListenerWithTimeout(final UUID messageId, final MessageHandler messageHandler, final int timeoutCycle) {
    handlers.add(new SessionRecord(messageId, messageHandler, false, timeoutCycle));
  }

  /**
   * Удаление обработчика сообщения сессии.
   * 
   * @param messageId идентификатор ответа сообщения.
   * @param messageHandler обработчик сообщения.
   * @see SessionManager#addMessageListener(UUID, MessageHandler) 
   * @see SessionManager#addMessageListener(UUID, MessageHandler, boolean)
   */
  public final void removeMessageListener(final UUID messageId, final MessageHandler messageHandler) {
    synchronized (handlers) {
      final Iterator handlerIt = handlers.iterator();
      while (handlerIt.hasNext()) {
        final SessionRecord element = (SessionRecord) handlerIt.next();
        if (element.getMsgId().equals(messageId) && element.getMessageHandler().equals(messageHandler)) {
          handlerIt.remove();
          break;
        }
      }
    }
  }

  /**
   * Функция обработки сообщения Odisp. рекоммендуется вызывать из messageRecevied и handleMessage.
   * 
   * @param msg Сообщение Odisp
   * @return Если в списке менеджера сессий существует обработчик для данного сообщения, то возвращает true, в
   *         противном случае пришло сообщение, которое не подошло ни под один ответ (необходима
   *         самостоятельная обработка).
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
    
    private int cycle;

    SessionRecord(UUID _msgId, MessageHandler _messageHandler, boolean _multiply, int _cycle) {
      msgId = _msgId;
      messageHandler = _messageHandler;
      multiply = _multiply;
      cycle = _cycle;
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
    
    int getCycle() {
      return cycle;
    }
  }
}