package org.valabs.odisp.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.valabs.odisp.SessionManager;

/** Стандартный объект ODISP.
 * @author (C) 2004 <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @version $Id: StandartODObject.java,v 1.11 2004/10/24 19:09:26 valeks Exp $
 */

public abstract class StandartODObject implements ODObject {
  /** Журнал. */
  protected Logger logger;
  /** Диспетчер работающий с этим объектом. */
  protected Dispatcher dispatcher;
  /** Список сообщений к обработке. */
  private List messages;
  /** Таблица конфигурационных параметров. */
  private Map configuration;
  /** Regex маска принимаемых сообщений.
   * По умолчанию инициализируется именем объекта. */
  private String match;
  /** Внутреннее имя объекта в ядре ODISP. */
  private String name;
  /** Карта обработчиков сообщений. */
  private Map handlers;
  /** Признак блокировки объекта. */
  private boolean blockedState = false;
  /** Изменить маску принимаемых сообщений.
   * @param newMatch новая маска
   */
  protected final void setMatch(final String newMatch) {
    match = newMatch;
  }

  /** Доступ к RegEx выражению совпадения адреса получателя.
   * @return строка с regex
   */
  public final String getMatch() {
    return match;
  }

  /** Конструктор инициализирующий почтовый ящик.
   * @param newName имя объекта
   */
  public StandartODObject(final String newName) {
    messages = new ArrayList();
    name = newName;
    match = newName;
    logger = Logger.getLogger(newName);
    logger.setLevel(java.util.logging.Level.ALL);
    handlers = new HashMap();
    registerHandlers();
  }

  /** Доступ к диспетчеру.
   * @return ссылка на диспетчер
   */
  protected final Dispatcher getDispatcher() {
    return dispatcher;
  }

  /** Возвращает внутреннее ODISP имя объекта.
   * @return ODISP имя объекта
   */
  public final String getObjectName() {
    return name;
  }

  /** Устанавливает диспетчера для текущего объекта.
   * @param d диспетчер работающий с этим объектом
   */
  public final void setDispatcher(final Dispatcher d) {
    this.dispatcher = d;
  }

  /** Установить таблицу параметров.
   * @param cfg новая таблица
   */
  public void setConfiguration(final Map cfg) {
    configuration = cfg;
  }
  /** Получить значение параметра конфигурации.
   * @param name имя параметра
   */
  protected final String getParameter(final String name) {
    if (configuration != null && configuration.containsKey(name)) {
      return (String) configuration.get(name);
    }
    return null;
  }
  /** Получить значение параметра конфигурации с учетом значения по-умолчанию.
   * @param name имя параметра
   * @param defValue значение по умолчанию
   */
  protected final String getParameter(final String name, final String defValue) {
    return getParameter(name) == null ? defValue : getParameter(name);
  }

  /** Регистрация обработчика сообщения.
   * @param message символьное имя сообщения
   * @param handler обработчик
   */
  protected final void addHandler(final String message,
				  final MessageHandler handler) {
    if (handlers.containsKey(message)) {
      return;
    }
    handlers.put(message, handler);
  }

  /** Обработка сообщения.
   * @param msg сообщение для обработки
   */
  public void handleMessage(final Message msg) {
  	if (blockedState && !msg.isOOB()) {
  		// пропускать лишь OOB сообщения
  		messages.add(msg);
  		return;
  	}
    if (handlers.containsKey(msg.getAction())) {
      ((MessageHandler) handlers.get(msg.getAction())).messageReceived(msg);
    } else {
      if (!SessionManager.getSessionManager().processMessage(msg)) {
        logger.finer(" (" + getObjectName() + ") there is no handler for message " + msg.getAction());
      }
    }
  }

  /** Наследующие классы должны перегрузить этот метод
   * для регистрации обработчиков. */
  protected void registerHandlers() {
    /* DO NOTHING BY DEFAULT */
  }

  /** Метод вызываемый для очистки данных класса.
   * @param type признак выхода
   * @return код возврата
   */
  public int cleanUp(final int type) {
    return 0;
  }
  
  /** Установить состояние блокировки.
   * В этом состоянии все сообщения, которые были получены объектом (кроме
   * сообщений с префиксом "od_"), сохраняются для последующей обработки.
   * При смене состояния на "неблокирующее" сохраненные сообщения передаются
   * на обработку отдельному Sender-потоку.
   * @param newState новое состояние
   */
  protected void setBlockedState(final boolean newState) {
  	blockedState = newState;
  	if (!blockedState) {
  		new Thread("sender-message-flush") {
  		List localMessages;
  		public void run() {
  			synchronized (messages) {
  				localMessages = new ArrayList(messages);
  				messages.clear();
  			}
  			Iterator it = localMessages.iterator();
  			while (it.hasNext()) {
  				Message elt = (Message) it.next();
  				handleMessage(elt);
  			}
  		}
  		}.start();
  	}
  }
} // StandartODObject
