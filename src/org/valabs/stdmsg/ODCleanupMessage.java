package com.novel.stdmsg;

/** Сообщение о прекращении работы объекта.
 * Необязательный параметр reason определяет код причины выхода
 * @author <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @author (C) 2003, НПП "Новел-ИЛ"
 * @version $Id: ODCleanupMessage.java,v 1.2 2003/12/04 09:48:20 valeks Exp $
 */

public class ODCleanupMessage extends StandartMessage {
  /** Символьное имя сообщение */
  public static final String name = "od_cleanup";
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
    if (getFieldsCount() == 1) {
      return ((Integer) getField(0)).intValue();
    }
    return 0;
  }

  /** Установить новое значение кода выхода
   * @param newReason новое значение кода
   * @return ссылка на сообщение
   */
  public ODCleanupMessage setReason(int newReason) {
    fields.add(0, new Integer(newReason));
    return this;
  }
  
}// ODCleanupMessage
