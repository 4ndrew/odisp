package com.novel.stdmsg;

import com.novel.odisp.common.Message;
import com.novel.odisp.common.Resource;

/** Запрос диспетчера на высвобождение ресурса.
 * @author <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @author (C) 2003, НПП "Новел-ИЛ"
 * @version $Id: ODReleaseMessage.java,v 1.12 2004/07/16 12:11:27 boris Exp $
 */

public class ODReleaseMessage {
  /** Символьное имя сообщения. */
  public static final String NAME = "od_release";
  /** Индекс имени в сообщении. */
  private static final String NAME_IDX = "resname";
  /** Индекс ресурса в сообщении. */
  private static final String RES_IDX = "resobj";

  /** Инициализация сообщения диспетчеру с запросом на высвобождение ресурса.
   * @param msg сообщение
   * @param origin автор
   * @param replyTo в ответ на сообщение No.
   */
  public static final void setup(final Message msg, final String origin, final int replyTo) {
    msg.setAction(NAME);
    msg.setDestination("dispatcher");
    msg.setReplyTo(replyTo);
    msg.setOrigin(origin);
    msg.setRoutable(false);
    msg.setCorrect(false);
  }

  /** Вернуть имя ресурса для захвата.
   * @param msg сообщение
   * @return имя ресурса
   */
  public static final String getResourceName(final Message msg) {
    return (String) msg.getField(NAME_IDX);
  }

  /** Установить имя ресурса.
   * @param msg сообщение
   * @param newName новое имя
   * @return ссылка на текущее сообщение
   */
  public static final void setResourceName(final Message msg, final String newName) {
    msg.addField(NAME_IDX, newName);
    if (msg.getContents().size() == 2) {
      msg.setCorrect(true);
    }
  }

  /** Вернуть ссылку на ресурс.
   * @param msg сообщение
   * @return ссылка на ресурс
   */
  public static final Resource getResource(final Message msg) {
    return (Resource) msg.getField(RES_IDX);
  }

  /** Установить новое значение ссылки на ресурс.
   * @param msg сообщение
   * @param newResource новое значение ссылки
   * @return ссылка на текущее сообщение
   */
  public static final void setResource(final Message msg, final Resource newResource) {
    msg.addField(RES_IDX, newResource);
    if (msg.getContents().size() == 2) {
      msg.setCorrect(true);
    }
  }
  public static final boolean equals(final Message msg) {
    return msg.getAction().equals(NAME);
  }
} // ODReleaseMessage
