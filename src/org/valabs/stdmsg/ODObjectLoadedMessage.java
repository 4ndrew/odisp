package com.novel.stdmsg;

/** Класс реализующий сообщение о загрузке объекта.
 * Данное сообщение не имеет ни каких дополнительных полей, по этому оно не расширяет
 * свой базовый класс StandartMessage.
 * @author <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @author (C) 2003, НПП "Новел-ИЛ"
 * @version $Id: ODObjectLoadedMessage.java,v 1.3 2003/12/04 09:48:20 valeks Exp $
 */

public class ODObjectLoadedMessage extends StandartMessage {
  /** Символьное имя сообщения*/
  public static final String name = "od_object_loaded";
  /** Конструктор сообщения о загрузке объекта
   * @param destination получатель сообщения
   */
  public ODObjectLoadedMessage(String destination) {
    super("od_object_loaded", destination, "stddispatcher", 0);
  }
}// ODObjectLoaded
