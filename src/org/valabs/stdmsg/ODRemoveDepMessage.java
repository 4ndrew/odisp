package com.novel.stdmsg;

/** Класс реализующий сообщение диспетчеру ODISP об изменении списка зависимостей.
 * Это сообщение должно обрабатыватся только диспетчером. 
 * В качестве параметра нужно указать имя зависимости.
 * @author <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @author (C) 2003, НПП "Новел-ИЛ"
 * @version $Id: ODRemoveDepMessage.java,v 1.2 2003/12/03 21:12:44 valeks Exp $
 */

public class ODRemoveDepMessage extends StandartMessage {
  /** Создает новое сообщение с заданными параметрами 
   * @param origin отправитель
   * @param replyTo индекс сообщения на которое производится ответ
   */
  public ODRemoveDepMessage(String origin, int replyTo) {
    super("od_remove_dep", origin, "stddispatcher", replyTo);
  }

  /** Вернуть значение кода выхода
   * @return код выхода
   */
  public String getDepName() {
    return (String) getField(0);
  }

  /** Установить значение имени зависимости
   * @param newDepName новое имя зависимости
   */
  public ODRemoveDepMessage setDepName(String newDepName) {
    fields.clear();
    addField(newDepName);
    return this;
  }
  
}// ODShutdownMessage
