package com.novel.stdmsg;

import com.novel.odisp.common.Message;
/** Класс реализующий сообщение о загрузке объекта.
 * Данное сообщение не имеет ни каких дополнительных полей,
 * по этому оно не расширяет свой базовый класс StandartMessage.
 * @author <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @author (C) 2003, НПП "Новел-ИЛ"
 * @version $Id: ODObjectLoadedMessage.java,v 1.8 2004/06/09 00:28:48 valeks Exp $
 */

public class ODObjectLoadedMessage {
  /** Символьное имя сообщения. */
  public static final String NAME = "od_object_loaded";
  /** Подготовка сообщения о загрузке.
   * @param destination получатель сообщения
   */
  public static void setup(final Message msg) {
    msg.setAction(NAME);
    msg.setOrigin("dispatcher");
    msg.setId(0);
    msg.setRoutable(false);
  }

  public static boolean equals(final Message msg) {
    return msg.getAction().equals(NAME);
  }
} // ODObjectLoaded
