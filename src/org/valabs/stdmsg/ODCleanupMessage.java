package com.novel.stdmsg;

/** Сообщение о прекращении работы объекта.
 * Необязательный параметр reason определяет код причины выхода
 * @author <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @author (C) 2003, НПП "Новел-ИЛ"
 * @version $Id: ODCleanupMessage.java,v 1.5 2004/02/24 00:35:14 valeks Exp $
 */

public class ODCleanupMessage extends StandartMessage {
  /** Символьное имя сообщение. */
  public static final String NAME = "od_cleanup";
  /** Причина выхода. */
  private transient int reason = 0;
  /** Индек поля с причиной. */
  private static final int REASON_IDX = 0;
  /** Конструктор сообщения.
   * @param destination получатель сообщения
   * @param replyId индекс сообщения на которое производится ответ
   */
  public ODCleanupMessage(final String destination, final int replyId) {
    super("od_cleanup", destination, "stddispatcher", replyId);
  }

  /** Вернуть текущее значение причины выхода.
   * @return код выхода
   */
  public final int getReason() {
    if (isCE()) {
      return ((Integer) getField(REASON_IDX)).intValue();
    }
    return reason;
  }

  /** Установить новое значение кода выхода.
   * @param newReason новое значение кода
   * @return ссылка на сообщение
   */
  public final ODCleanupMessage setReason(final int newReason) {
    reason = newReason;
    return this;
  }

  /** Проверка корректности сообщения.
   * @return флаг корректности
   */
  public final boolean isCorrect() {
    if (isCE()) {
      return true;
    }
    getFields().clear();
    addField(new Integer(reason));
    setCE(true);
    return true;
  }
  public final boolean isRoutable() {
    return false;
  }
} // ODCleanupMessage
