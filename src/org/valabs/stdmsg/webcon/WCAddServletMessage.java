package com.novel.stdmsg.webcon;

import com.novel.odisp.common.Message;

/** Добавление сервлета в контейнер WebCon.
 * <p> Для того, что бы не далеть дополнительных зависимостей в качестве обработчика на данный момент
 * можно указать произвольный объект. Проверка на соответствие типов производится при обработке, а не
 * во время isCorrect().</p>
 * <p>Сообщение не является маршрутизируемым, так как не имеет смысл добавлять сервлеты в удаленный контейнер.</p>
 * @author <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @author (C) 2004, НПП "Новел-ИЛ"
 * @version $Id: WCAddServletMessage.java,v 1.4 2004/06/09 21:07:36 valeks Exp $
 */

public class WCAddServletMessage {
  /** Символтическое имя сообщения. */
  public static final String NAME = "wc_add_servlet";
  /** Имя поля. */
  private static final String SERVLETMASK_IDX = "servletmask";
  /** Имя поля. */
  private static final String SERVLETHANDLER_IDX = "servlethandler";
  /** Конструктор сообщения.
   * @param webConName имя объекта webcon. в случае если null используется сервис <tt>webcon</tt>.
   * @param objectName источник сообщения
   * @param msgId индекс сообщения на которое производится ответ
   */
  public static final void setup(final Message msg, final String webConName, final String objectName, final int msgId) {
    msg.setAction(NAME);
    if (webConName == null) {
      msg.setDestination("webcon");      
    } else {
      msg.setDestination(webConName);
    }
    msg.setOrigin(objectName);
    msg.setReplyTo(msgId);
    msg.setRoutable(false);
    msg.setCorrect(false);
  }

  /** Установка маски. */
  public static final void setServletMask(final Message msg, final String newServletMask) {
    msg.addField(SERVLETMASK_IDX, newServletMask);
    if (msg.getContents().size() == 2) {
      msg.setCorrect(true);
    }
  }

  /** Доступ к маске. */
  public static final String getServletMask(final Message msg) {
    return (String) msg.getField(SERVLETMASK_IDX);
  }

  /** Установка обработчика. */
  public static final void setServletHandler(final Message msg, final Object newServletHandler) {
    msg.addField(SERVLETHANDLER_IDX, newServletHandler);
    if (msg.getContents().size() == 2) {
      msg.setCorrect(true);
    }
  }

  /** Доступ к обработчику. */
  public static final Object getServletHandler(final Message msg) {
    return (Object) msg.getField(SERVLETHANDLER_IDX);
  }

  public static final boolean equals(final Message msg) {
    return msg.getAction().equals(NAME);
  }
} // WCAddServletMessage
