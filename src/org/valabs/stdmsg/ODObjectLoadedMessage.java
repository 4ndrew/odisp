package com.novel.stdmsg;

/** Класс реализующий сообщение о загрузке объекта.
 * Данное сообщение не имеет ни каких дополнительных полей,
 * по этому оно не расширяет свой базовый класс StandartMessage.
 * @author <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @author (C) 2003, НПП "Новел-ИЛ"
 * @version $Id: ODObjectLoadedMessage.java,v 1.6 2004/02/24 00:35:14 valeks Exp $
 */

public class ODObjectLoadedMessage extends StandartMessage {
  /** Символьное имя сообщения. */
  public static final String NAME = "od_object_loaded";
  /** Конструктор сообщения о загрузке объекта.
   * @param destination получатель сообщения
   */
  public ODObjectLoadedMessage(final String destination) {
    super("od_object_loaded", destination, "stddispatcher", 0);
  }

  public final boolean isRoutable() {
    return false;
  }

} // ODObjectLoaded
