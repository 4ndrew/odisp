package com.novel.stdmsg.webcon;

import com.novel.odisp.common.Message;

/** Получение списка сервлетов в контейнере.
 * @author <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @author (C) 2004, НПП "Новел-ИЛ"
 * @version $Id: WCListServletsMessage.java,v 1.3 2004/06/09 21:07:36 valeks Exp $
 */

public class WCListServletsMessage {
  /** Символтическое имя сообщения. */
  public static final String NAME = "wc_list_servlets";
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
    msg.setCorrect(true);
  }

  public static final boolean equals(final Message msg) {
    return msg.getAction().equals(msg);
  }
}// WCRemoveServletMessage
