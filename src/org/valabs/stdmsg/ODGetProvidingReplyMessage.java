package com.novel.stdmsg;

import java.util.List;

import com.novel.odisp.common.Message;

/** Запрос на динамическое изменение списка сервисов.
 * @author <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @author (C) 2004, НПП "Новел-ИЛ"
 * @version $Id: ODGetProvidingReplyMessage.java,v 1.5 2004/06/09 18:55:57 valeks Exp $
 */

public class ODGetProvidingReplyMessage {
  /** Символическое имя сообщения. */
  public static final String NAME = "od_get_providing_reply";

  /** Имя параметра с названием. */
  private static String PROVIDINGLIST_IDX = "0";

  /** Конструктор сообщения.
   * @param objectName имя объекта
   * @param replyId номер сообщения на которое производится ответ
   */
  public static final void setup(final Message msg, final String objectName, final int replyId) {
    // отправлять может только диспетчер
    msg.setAction(NAME);
    msg.setOrigin("dispatcher");
    msg.setDestination(objectName);
    msg.setReplyTo(replyId);
    msg.setRoutable(false);
  }
  /** Доступ к имени сервиса. */
  public static final List getProvidingList(final Message msg) {
    return (List) msg.getField(PROVIDINGLIST_IDX);
  }
  /** Установка имени сервиса. */
  public final static void setProvidingList(final Message msg, final List newProvidingList) {
    msg.addField(PROVIDINGLIST_IDX, newProvidingList);
  }

  public final static boolean equals(final Message msg) {
    return msg.getAction().equals(NAME);
  }
} // ODGetProvidingReplyMessage
