package com.novel.stdmsg;

import java.util.List;

import com.novel.odisp.common.Message;

/** Запрос на динамическое изменение списка сервисов.
 * @author <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @author (C) 2004, НПП "Новел-ИЛ"
 * @version $Id: ODGetProvidingReplyMessage.java,v 1.6 2004/07/20 11:55:24 valeks Exp $
 */

public final class ODGetProvidingReplyMessage {
  /** Символическое имя сообщения. */
  public static final String NAME = "od_get_providing_reply";

  /** Имя параметра с названием. */
  private static String PROVIDINGLIST_IDX = "0";

  /** Конструктор сообщения.
   * @param objectName имя объекта
   * @param replyId номер сообщения на которое производится ответ
   */
  public static void setup(final Message msg, final String objectName, final int replyId) {
    // отправлять может только диспетчер
    msg.setAction(NAME);
    msg.setOrigin("dispatcher");
    msg.setDestination(objectName);
    msg.setReplyTo(replyId);
    msg.setRoutable(false);
  }
  /** Доступ к имени сервиса. */
  public static List getProvidingList(final Message msg) {
    return (List) msg.getField(PROVIDINGLIST_IDX);
  }
  /** Установка имени сервиса. */
  public final void setProvidingList(final Message msg, final List newProvidingList) {
    msg.addField(PROVIDINGLIST_IDX, newProvidingList);
  }

  public final boolean equals(final Message msg) {
    return msg.getAction().equals(NAME);
  }
} // ODGetProvidingReplyMessage
