package com.novel.stdmsg.webcon;

import com.novel.stdmsg.StandartMessage;
import com.novel.odisp.common.Message;

/** Получение списка сервлетов в контейнере.
 * @author <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @author (C) 2004, НПП "Новел-ИЛ"
 * @version $Id: WCListServletsMessage.java,v 1.1 2004/03/27 21:27:40 valeks Exp $
 */

public class WCListServletsMessage extends StandartMessage {
  /** Символтическое имя сообщения. */
  public static final String NAME = "wc_list_servlets";
  /** Конструктор сообщения.
   * @param webConName имя объекта webcon. в случае если null используется сервис <tt>webcon</tt>.
   * @param objectName источник сообщения
   * @param msgId индекс сообщения на которое производится ответ
   */
  public WCListServletsMessage(String webConName, final String objectName, final int msgId) {
    super(NAME, webConName, objectName, msgId);
    if (webConName == null) {
      // работаем через Discovery
      setDestination("webcon");
    }
  }

  /** Копирующий конструктор.
   * @param msg сообщение для приведения
   */
  public WCListServletsMessage(final Message msg) {
    super(msg);
  }

  /** Является ли сообщение маршрутизируемым. */
  public final boolean isRoutable() {
    return false;
  }
}// WCRemoveServletMessage
