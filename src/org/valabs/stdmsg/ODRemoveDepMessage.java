package com.novel.stdmsg;

/** Класс реализующий сообщение диспетчеру ODISP об изменении списка зависимостей.
 * Это сообщение должно обрабатыватся только диспетчером. 
 * В качестве параметра нужно указать имя зависимости.
 * @author <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @author (C) 2003, НПП "Новел-ИЛ"
 * @version $Id: ODRemoveDepMessage.java,v 1.4 2003/12/15 14:02:43 valeks Exp $
 */

public class ODRemoveDepMessage extends StandartMessage {
  /** Символьное имя сообщения*/
  public static final String name = "od_remove_dep";

  private transient String depName = "";
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
    if (ce) {
      return (String) getField(0);
    }
    return depName;
  }

  /** Установить значение имени зависимости
   * @param newDepName новое имя зависимости
   */
  public ODRemoveDepMessage setDepName(String newDepName) {
    depName = newDepName;
    return this;
  }
  
  public boolean isCorrect() {
    if (ce) {
      return true;
    }
    if (depName != "") {
      fields.clear();
      addField(depName);
      return true;
    } else {
      return false;
    }
  }

}// ODShutdownMessage
