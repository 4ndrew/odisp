package com.novel.stdmsg;

import com.novel.odisp.common.Message;
import java.util.List;

/** Запрос на динамическое изменение списка сервисов.
 * @author <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @author (C) 2004, НПП "Новел-ИЛ"
 * @version $Id: ODGetProvidingReplyMessage.java,v 1.2 2004/03/27 20:07:56 valeks Exp $
 */

public class ODGetProvidingReplyMessage extends StandartMessage {
  /** Символическое имя сообщения. */
  public static final String NAME = "od_get_providing_reply";
  /** Название сервиса. */
  private transient List providingList = null;
  /** Имя параметра с названием. */
  private static String PROVIDINGLIST_IDX = "0";
  /** Копирующий конструктор.
   * @param msg приводимое сообщение
   */
  public ODGetProvidingReplyMessage(final Message msg) {
    super(msg);
  }
  /** Конструктор сообщения.
   * @param objectName имя объекта
   * @param replyId номер сообщения на которое производится ответ
   */
  public ODGetProvidingReplyMessage(final String objectName, final int replyId) {
    // отправлять может только диспетчер
    super(NAME, objectName, "stddispatcher", replyId);
  }
  /** Доступ к имени сервиса. */
  public final List getProvidingList() {
    if (isCE()) {
      return (List) getField(PROVIDINGLIST_IDX);
    }
    return providingList;
  }
  /** Установка имени сервиса. */
  public final void setProvidingList(final List newProvidingList) {
    providingList = newProvidingList;
  }
  /** Проверка корректности сообщения. */
  public final boolean isCorrect() {
    if (isCE()) {
      return true;
    }
    if (providingList != null) {
      addField(PROVIDINGLIST_IDX, providingList);
      setCE(true);
    }
    return isCE();
  }

  /** Является ли сообщение маршрутизуемым. */
  public final boolean isRoutable() {
    return false;
  }
} // ODGetProvidingReplyMessage
