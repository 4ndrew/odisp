package com.novel.stdmsg;

import com.novel.odisp.common.Message;
/** Запрос диспетчера на захват ресурса.
 * Запрос должен содержать минимум 1 параметр - имя ресурсного объекта.
 * @author <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @author (C) 2003, НПП "Новел-ИЛ"
 * @version $Id: ODAcquireMessage.java,v 1.11 2004/06/09 14:19:38 valeks Exp $
 */

public class ODAcquireMessage {
  /** Символьное имя сообщения. */
  public static final String NAME = "od_acquire";
  /** Индекс имения ресурса в списке полей. */
  private static final String NAME_IDX = "resname";
  /** Настройка сообщения.
   * @param msg настраиваемое сообщение
   */
  public final static void setup(final Message msg) {
    msg.setAction(NAME);
    msg.setDestination("dispatcher");
    msg.setRoutable(false);
    msg.setCorrect(false);
  }

  /** Вернуть имя ресурса для захвата.
   * @return имя ресурса
   */
  public final static String getResourceName(final Message msg) {
    return (String) msg.getField(NAME_IDX);
  }

  /** Установить имя ресурса.
   * @param newName новое имя
   * @return ссылка на текущее сообщение
   */
  public final static void setResourceName(final Message msg, final String newName) {
    assert newName != null;
    assert msg.getAction() != null;
    msg.addField(NAME_IDX, newName);
    msg.setCorrect(true);
  }

  public final static boolean equals(final Message msg) {
    return msg.getAction().equals(NAME);
  }
} // ODAcquireMessage
