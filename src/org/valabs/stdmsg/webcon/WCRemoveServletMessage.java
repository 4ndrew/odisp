package com.novel.stdmsg.webcon;

import com.novel.odisp.common.Message;

/** Удаление сервлета из контейнера.
 * Производится удаление ссылки на обработчик запросов из записей контейнера.
 * @author (C) 2004 <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @version $Id: WCRemoveServletMessage.java,v 1.4 2004/08/18 12:48:39 valeks Exp $
 */

public class WCRemoveServletMessage {
  /** Символтическое имя сообщения. */
  public static final String NAME = "wc_remove_servlet";
  /** Имя поля. */
  private static final String SERVLETHANDLER_IDX = "servlethandler";
  /** Конструктор сообщения.
   * @param webConName имя объекта webcon. в случае если null используется сервис <tt>webcon</tt>.
   * @param objectName источник сообщения
   * @param msgId индекс сообщения на которое производится ответ
   */
  public static final void setup(final Message msg, final String webConName, final String objectName, final int msgId) {
    msg.setAction(NAME);
    msg.setDestination(webConName);
    msg.setOrigin(objectName);
    msg.setReplyTo(msgId);
    msg.setRoutable(false);
    msg.setCorrect(false);
  }

  /** Установка обработчика. */
  public static final void setServletHandler(final Message msg, final String newServletHandler) {
    msg.addField(SERVLETHANDLER_IDX, newServletHandler);
    msg.setCorrect(true);
  }

  /** Доступ к обработчику. */
  public static final Object getServletHandler(final Message msg) {
    return (Object) msg.getField(SERVLETHANDLER_IDX);
  }

  public static final boolean equals(final Message msg) {
    return msg.getAction().equals(NAME);
  }
}// WCRemoveServletMessage
