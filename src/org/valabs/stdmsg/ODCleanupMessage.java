package com.novel.stdmsg;

/** Сообщение о прекращении работы объекта.
 * Необязательный параметр reason определяет код причины выхода
 * @author <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @author (C) 2003, НПП "Новел-ИЛ"
 * @version $Id: ODCleanupMessage.java,v 1.3 2003/12/15 14:02:43 valeks Exp $
 */

public class ODCleanupMessage extends StandartMessage {
  /** Символьное имя сообщение */
  public static final String name = "od_cleanup";
  private transient int reason = 0;
  /** Конструктор сообщения
   * @param destination получатель сообщения
   * @param replyId индекс сообщения на которое производится ответ
   */
  public ODCleanupMessage(String destination, int replyId) {
    super("od_cleanup", destination, "stddispatcher", replyId);
  }

  /** Вернуть текущее значение причины выхода
   * @return код выхода
   */
  public int getReason() {
    if (ce) {
      return ((Integer) getField(0)).intValue();
    }
    return reason;
  }

  /** Установить новое значение кода выхода
   * @param newReason новое значение кода
   * @return ссылка на сообщение
   */
  public ODCleanupMessage setReason(int newReason) {
    reason = newReason;
    return this;
  }
  
  public boolean isCorrect() {
    if (ce) {
      return true;
    }
    fields.clear();
    addField(new Integer(reason));
    ce = true;
    return true;
  }
}// ODCleanupMessage
