package com.novel.stdmsg;

import com.novel.odisp.common.Message;

/** Запрос на динамическое изменение списка сервисов.
 * @author <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @author (C) 2003, НПП "Новел-ИЛ"
 * @version $Id: ODAddProviderMessage.java,v 1.1 2004/03/27 19:40:18 valeks Exp $
 */

public class ODAddProviderMessage extends StandartMessage {
  /** Символическое имя сообщения. */
  public static final String NAME = "od_add_provider";
  /** Название сервиса. */
  private transient String serviceName = null;
  /** Имя параметра с названием. */
  private static String SERVICENAME_IDX = "0";
  /** Копирующий конструктор.
   * @param msg приводимое сообщение
   */
  public ODAddProviderMessage(final Message msg) {
    super(msg);
  }
  /** Конструктор сообщения.
   * @param objectName имя объекта
   * @param replyId номер сообщения на которое производится ответ
   */
  public ODAddProviderMessage(final String objectName, final int replyId) {
    super(NAME, "stddispatcher", objectName, replyId);
  }
  /** Доступ к имени сервиса. */
  public final String getServiceName() {
    if (isCE()) {
      return (String) getField(SERVICENAME_IDX);
    }
    return serviceName;
  }
  /** Установка имени сервиса. */
  public final void setSerivceName(final String newServiceName) {
    serviceName = newServiceName;
  }
  /** Проверка корректности сообщения. */
  public final boolean isCorrect() {
    if (isCE()) {
      return true;
    }
    if (serviceName != null) {
      addField(SERVICENAME_IDX, serviceName);
      setCE(true);
    }
    return isCE();
  }

  /** Является ли сообщение маршрутизуемым. */
  public final boolean isRoutable() {
    return false;
  }
} // ODAddProviderMessage
