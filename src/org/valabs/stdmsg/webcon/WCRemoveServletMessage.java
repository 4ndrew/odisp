package com.novel.stdmsg.webcon;

import com.novel.stdmsg.StandartMessage;
import com.novel.odisp.common.Message;

/** Удаление сервлета из контейнера.
 * Производится удаление ссылки на обработчик запросов из записей контейнера.
 * @author <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @author (C) 2004, НПП "Новел-ИЛ"
 * @version $Id: WCRemoveServletMessage.java,v 1.1 2004/03/27 21:27:40 valeks Exp $
 */

public class WCRemoveServletMessage extends StandartMessage {
  /** Символтическое имя сообщения. */
  public static final String NAME = "wc_remove_servlet";
  /** Объект-обработчик запроса. */
  private transient Object servletHandler = null;
  /** Имя поля. */
  private static final String SERVLETHANDLER_IDX = "0";
  /** Конструктор сообщения.
   * @param webConName имя объекта webcon. в случае если null используется сервис <tt>webcon</tt>.
   * @param objectName источник сообщения
   * @param msgId индекс сообщения на которое производится ответ
   */
  public WCRemoveServletMessage(String webConName, final String objectName, final int msgId) {
    super(NAME, webConName, objectName, msgId);
    if (webConName == null) {
      // работаем через Discovery
      setDestination("webcon");
    }
  }

  /** Копирующий конструктор.
   * @param msg сообщение для приведения
   */
  public WCRemoveServletMessage(final Message msg) {
    super(msg);
  }

  /** Установка обработчика. */
  public final void setServletHandler(final String newServletHandler) {
    servletHandler = newServletHandler;
  }

  /** Доступ к обработчику. */
  public final Object getServletHandler() {
    if (isCE()) {
      return (Object) getField(SERVLETHANDLER_IDX);
    }
    return servletHandler;
  }

  /** Проверка корректности сообщения. */
  public final boolean isCorrect() {
    if (isCE()) {
      return isCE();
    }
    if (servletHandler != null) {
      addField(SERVLETHANDLER_IDX, servletHandler);
      setCE(true);
    }
    return isCE();
  }

  /** Является ли сообщение маршрутизируемым. */
  public final boolean isRoutable() {
    return false;
  }
}// WCRemoveServletMessage
