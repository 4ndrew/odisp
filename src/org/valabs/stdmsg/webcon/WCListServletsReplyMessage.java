package org.valabs.stdmsg.webcon;

import java.util.List;

import org.valabs.odisp.common.Message;

/** Запрос на динамическое изменение списка сервисов.
 * @author (C) 2004 <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @version $Id: WCListServletsReplyMessage.java,v 1.5 2004/08/23 07:42:36 valeks Exp $
 */

public class WCListServletsReplyMessage {
  /** Символическое имя сообщения. */
  public static final String NAME = "wc_list_servlets_reply";
  /** Имя параметра с названием. */
  private static String SERVLETSLIST_IDX = "servletlist";
  /** Конструктор сообщения.
   * @param webCon объект-отправитель
   * @param objectName имя объекта-получателя
   * @param replyId номер сообщения на которое производится ответ
   */
  public static final void setup(final Message msg, final String webCon, final String objectName, final int replyId) {
    msg.setAction(NAME);
    msg.setDestination(objectName);
    msg.setOrigin(webCon);
    msg.setReplyTo(replyId);
    msg.setRoutable(false);
    msg.setCorrect(false);
  }
  /** Доступ к имени сервиса. */
  public static final List getServletsList(final Message msg) {
    return (List) msg.getField(SERVLETSLIST_IDX);
  }
  /** Установка имени сервиса. */
  public static final void setServletsList(final Message msg, final List newServletsList) {
    msg.addField(SERVLETSLIST_IDX, newServletsList);
    msg.setCorrect(true);
  }

  public static final boolean equals(final Message msg) {
    return msg.getAction().equals(NAME);
  }
} // WCListServletsReplyMessage

