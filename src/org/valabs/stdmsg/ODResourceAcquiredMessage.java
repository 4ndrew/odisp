package com.novel.stdmsg;

import com.novel.odisp.common.Resource;

/** Ответ на запрос о захвате ресурса
 * @author <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @author (C) 2003, НПП "Новел-ИЛ"
 * @version $Id: ODResourceAcquiredMessage.java,v 1.4 2003/12/04 09:48:20 valeks Exp $
 */
public class ODResourceAcquiredMessage extends StandartMessage {
  /** Символьное имя сообщения*/
  public static final String name = "od_resource_acquired";
  /** Конструктор сообщения
   * @param destination получатель сообщения
   * @param replyId сообщение на которое производится ответ
   */
  public ODResourceAcquiredMessage(String destination, int replyId) {
    super("od_resource_acquired", destination, "stddispatcher", replyId);
  }
  /** Вернуть имя класса ресурса
   * @return имя класса
   */
  public String getClassName() {
    return (String) getField(0);
  }

  /** Установить имя класса ресурса
   * @param newClassName новое имя класса
   * @return ссылка на сообщение
   */
  public ODResourceAcquiredMessage setClassName(String newClassName) {
    fields.add(0, newClassName);
    return this;
  }

  /** Вернуть ссылку на ресурс
   * @return ссылка на ресурс
   */
  public Resource getResource() {
    return (Resource) getField(1);
  }
  /** Установить ссылку на ресурс
   * @param newResource новое значение ссылки
   * @return ссылка на сообщение
   */
  public ODResourceAcquiredMessage setResource(Resource newResource) {
    fields.add(1, newResource);
    return this;
  }

}// ODResourceAcquiredMessage
