package com.novel.stdmsg.webcon;

import com.novel.odisp.common.Message;
import com.novel.stdmsg.StandartMessage;
import java.util.List;

/** Запрос на динамическое изменение списка сервисов.
 * @author <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @author (C) 2004, НПП "Новел-ИЛ"
 * @version $Id: WCListServletsReplyMessage.java,v 1.1 2004/03/27 21:27:40 valeks Exp $
 */

public class WCListServletsReplyMessage extends StandartMessage {
  /** Символическое имя сообщения. */
  public static final String NAME = "wc_list_servlets_reply";
  /** Название сервиса. */
  private transient List servletsList = null;
  /** Имя параметра с названием. */
  private static String SERVLETSLIST_IDX = "0";
  /** Копирующий конструктор.
   * @param msg приводимое сообщение
   */
  public WCListServletsReplyMessage(final Message msg) {
    super(msg);
  }
  /** Конструктор сообщения.
   * @param webCon объект-отправитель
   * @param objectName имя объекта-получателя
   * @param replyId номер сообщения на которое производится ответ
   */
  public WCListServletsReplyMessage(final String webCon, final String objectName, final int replyId) {
    super(NAME, objectName, webCon, replyId);
  }
  /** Доступ к имени сервиса. */
  public final List getServletsList() {
    if (isCE()) {
      return (List) getField(SERVLETSLIST_IDX);
    }
    return servletsList;
  }
  /** Установка имени сервиса. */
  public final void setServletsList(final List newServletsList) {
    servletsList = newServletsList;
  }
  /** Проверка корректности сообщения. */
  public final boolean isCorrect() {
    if (isCE()) {
      return true;
    }
    if (servletsList != null) {
      addField(SERVLETSLIST_IDX, servletsList);
      setCE(true);
    }
    return isCE();
  }

  /** Является ли сообщение маршрутизуемым. */
  public final boolean isRoutable() {
    return false;
  }
} // WCListServletsReplyMessage
