package com.novel.stdmsg;

import com.novel.odisp.common.Message;
/** Сообщение о прекращении работы объекта.
 * Необязательный параметр reason определяет код причины выхода
 * @author <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @author (C) 2003, НПП "Новел-ИЛ"
 * @version $Id: ODCleanupMessage.java,v 1.8 2004/06/09 00:28:48 valeks Exp $
 */

public class ODCleanupMessage {
  /** Символьное имя сообщение. */
  public static final String NAME = "od_cleanup";
  /** Индек поля с причиной. */
  private static final String REASON_IDX = "reason";
  /** Настройка сообщения.
   * @param destination получатель сообщения
   * @param replyId индекс сообщения на которое производится ответ
   */
  public static void setup(final Message msg) {
    msg.setAction(NAME);
    msg.setOrigin("dispatcher");
    msg.setRoutable(false);
  }

  /** Вернуть текущее значение причины выхода.
   * @return код выхода
   */
  public static final int getReason(final Message msg) {
    return ((Integer) msg.getField(REASON_IDX)).intValue();
  }

  /** Установить новое значение кода выхода.
   * @param newReason новое значение кода
   * @return ссылка на сообщение
   */
  public static final void setReason(final Message msg, final int newReason) {
    msg.addField(REASON_IDX, new Integer(newReason));
  }
} // ODCleanupMessage
