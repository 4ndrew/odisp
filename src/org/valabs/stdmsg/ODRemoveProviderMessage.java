package com.novel.stdmsg;

import com.novel.odisp.common.Message;

/** Запрос на динамическое изменение списка сервисов.
 * @author <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @author (C) 2004, НПП "Новел-ИЛ"
 * @version $Id: ODRemoveProviderMessage.java,v 1.4 2004/04/02 09:54:49 valeks Exp $
 */

public class ODRemoveProviderMessage extends StandartMessage {
  /** Символическое имя сообщения. */
  public static final String NAME = "od_remove_provider";
  /** Название сервиса. */
  private transient String serviceName = null;
  /** Имя параметра с названием. */
  private static String SERVICENAME_IDX = "0";
  /** Копирующий конструктор.
   * @param msg приводимое сообщение
   */
  public ODRemoveProviderMessage(final Message msg) {
    super(msg);
  }
  /** Конструктор сообщения.
   * @param objectName имя объекта
   * @param replyId номер сообщения на которое производится ответ
   */
  public ODRemoveProviderMessage(final String objectName, final int replyId) {
    super(NAME, "dispatcher", objectName, replyId);
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
      getContents().clear();
      addField(SERVICENAME_IDX, serviceName);
      setCE(true);
    }
    return isCE();
  }

  /** Является ли сообщение маршрутизуемым. */
  public final boolean isRoutable() {
    return false;
  }
} // ODRemoveProviderMessage
