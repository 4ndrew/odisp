package com.novel.stdmsg;

import com.novel.odisp.common.Resource;

/** Ответ на запрос о захвате ресурса
 * @author <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @author (C) 2003, НПП "Новел-ИЛ"
 * @version $Id: ODResourceAcquiredMessage.java,v 1.6 2003/12/15 14:02:43 valeks Exp $
 */
public class ODResourceAcquiredMessage extends StandartMessage {
  /** Символьное имя сообщения*/
  public static final String name = "od_resource_acquired";

  private transient String resourceName = "";
  private transient Resource resource;

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
  public String getResourceName() {
    if (ce) {
      return (String) getField(0);
    }
    return resourceName;
  }

  /** Установить имя класса ресурса
   * @param newClassName новое имя класса
   * @return ссылка на сообщение
   */
  public ODResourceAcquiredMessage setResourceName(String newResourceName) {
    resourceName = newResourceName;
    return this;
  }

  /** Вернуть ссылку на ресурс
   * @return ссылка на ресурс
   */
  public Resource getResource() {
    if (ce) {
      return (Resource) getField(1);
    }
    return resource;
  }
  /** Установить ссылку на ресурс
   * @param newResource новое значение ссылки
   * @return ссылка на сообщение
   */
  public ODResourceAcquiredMessage setResource(Resource newResource) {
    resource = newResource;
    return this;
  }

  public boolean isCorrect() {
    if (ce) {
      return true;
    }
    if (resourceName != "" && resource != null) {
      fields.clear();
      addField(resourceName);
      addField(resource);
      return true;
    } else {
      return false;
    }
  }

}// ODResourceAcquiredMessage
