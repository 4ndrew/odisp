package com.novel.stdmsg;

import com.novel.odisp.common.Message;

/** Запрос списка доступных сервисов диспетчера.
 * @author <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @author (C) 2003, НПП "Новел-ИЛ"
 * @version $Id: ODGetProvidingMessage.java,v 1.1 2004/03/27 19:40:18 valeks Exp $
 */

public class ODGetProvidingMessage extends StandartMessage {
  /** Символическое имя сообщения. */
  public static final String NAME = "od_get_providing";
  /** Копирующий конструктор.
   * @param msg приводимое сообщение
   */
  public ODGetProvidingMessage(final Message msg) {
    super(msg);
  }

  /** Является ли сообщение маршрутизуемым. */
  public final boolean isRoutable() {
    return false;
  }
} // ODGetProvidingMessage
