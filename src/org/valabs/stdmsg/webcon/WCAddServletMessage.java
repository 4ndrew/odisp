package com.novel.stdmsg.webcon;

import com.novel.stdmsg.StandartMessage;
import com.novel.odisp.common.Message;

/** Добавление сервлета в контейнер WebCon.
 * <p> Для того, что бы не далеть дополнительных зависимостей в качестве обработчика на данный момент
 * можно указать произвольный объект. Проверка на соответствие типов производится при обработке, а не
 * во время isCorrect().</p>
 * <p>Сообщение не является маршрутизируемым, так как не имеет смысл добавлять сервлеты в удаленный контейнер.</p>
 * @author <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @author (C) 2004, НПП "Новел-ИЛ"
 * @version $Id: WCAddServletMessage.java,v 1.1 2004/03/27 21:27:40 valeks Exp $
 */

public class WCAddServletMessage extends StandartMessage {
  /** Символтическое имя сообщения. */
  public static final String NAME = "wc_add_servlet";
  /** Маска запроса. */
  private transient String servletMask = null;
  /** Имя поля. */
  private static final String SERVLETMASK_IDX = "0";
  /** Объект-обработчик запроса. */
  private transient Object servletHandler = null;
  /** Имя поля. */
  private static final String SERVLETHANDLER_IDX = "1";
  /** Конструктор сообщения.
   * @param webConName имя объекта webcon. в случае если null используется сервис <tt>webcon</tt>.
   * @param objectName источник сообщения
   * @param msgId индекс сообщения на которое производится ответ
   */
  public WCAddServletMessage(String webConName, final String objectName, final int msgId) {
    super(NAME, webConName, objectName, msgId);
    if (webConName == null) {
      // работаем через Discovery
      setDestination("webcon");
    }
  }

  /** Копирующий конструктор.
   * @param msg сообщение для приведения
   */
  public WCAddServletMessage(final Message msg) {
    super(msg);
  }

  /** Установка маски. */
  public final void setServletMask(final String newServletMask) {
    servletMask = newServletMask;
  }

  /** Доступ к маске. */
  public final String getServletMask() {
    if (isCE()) {
      return (String) getField(SERVLETMASK_IDX);
    }
    return servletMask;
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
    if (servletMask != null && servletHandler != null) {
      addField(SERVLETMASK_IDX, servletMask);
      addField(SERVLETHANDLER_IDX, servletHandler);
      setCE(true);
    }
    return isCE();
  }

  /** Является ли сообщение маршрутизируемым. */
  public final boolean isRoutable() {
    return false;
  }
}// WCAddServletMessage
