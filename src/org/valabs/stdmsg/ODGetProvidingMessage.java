package com.novel.stdmsg;

import com.novel.odisp.common.Message;

/** Запрос списка доступных сервисов диспетчера.
 * @author <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @author (C) 2004, НПП "Новел-ИЛ"
 * @version $Id: ODGetProvidingMessage.java,v 1.3 2004/06/09 18:55:57 valeks Exp $
 */

public class ODGetProvidingMessage {
  /** Символическое имя сообщения. */
  public static final String NAME = "od_get_providing";
  /** Копирующий конструктор.
   * @param msg приводимое сообщение
   */
  public static final void setup(final Message msg) {
    msg.setAction(NAME);
    msg.setDestination("dispatcher");
    msg.setRoutable(false);
    msg.setCorrect(true);
  }
  
  public static final boolean equals(final Message msg) {
    return msg.getAction().equals(NAME);
  }
} // ODGetProvidingMessage
