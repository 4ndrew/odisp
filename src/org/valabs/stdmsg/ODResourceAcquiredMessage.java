package com.novel.stdmsg;

import com.novel.odisp.common.Resource;

/** Ответ на запрос о захвате ресурса.
 * @author <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @author (C) 2003, НПП "Новел-ИЛ"
 * @version $Id: ODResourceAcquiredMessage.java,v 1.10 2004/03/31 17:01:40 valeks Exp $
 */
public class ODResourceAcquiredMessage extends StandartMessage {
  /** Символьное имя сообщения. */
  public static final String NAME = "od_resource_acquired";

  /** Имя ресурса. */
  private transient String resourceName = "";
  /** Индекс имени ресурса. */
  private static final String RESNAME_IDX = "0";
  /** Ресурс. */
  private transient Resource resource;
  /** Индекс ресурса. */
  private static final String RES_IDX = "1";

  /** Конструктор сообщения.
   * @param destination получатель сообщения
   * @param replyId сообщение на которое производится ответ
   */
  public ODResourceAcquiredMessage(final String destination,
				   final int replyId) {
    super("od_resource_acquired", destination, "stddispatcher", replyId);
  }
  /** Вернуть имя класса ресурса.
   * @return имя класса
   */
  public final String getResourceName() {
    if (isCE()) {
      return (String) getField(RESNAME_IDX);
    }
    return resourceName;
  }

  /** Установить имя класса ресурса.
   * @param newResourceName новое имя класса
   * @return ссылка на сообщение
   */
  public final ODResourceAcquiredMessage setResourceName
    (final String newResourceName) {
    resourceName = newResourceName;
    return this;
  }

  /** Вернуть ссылку на ресурс.
   * @return ссылка на ресурс
   */
  public final Resource getResource() {
    if (isCE()) {
      return (Resource) getField(RES_IDX);
    }
    return resource;
  }
  /** Установить ссылку на ресурс.
   * @param newResource новое значение ссылки
   * @return ссылка на сообщение
   */
  public final ODResourceAcquiredMessage setResource
    (final Resource newResource) {
    resource = newResource;
    return this;
  }

  /** Проверка корректности сообщения.
   * @return флаг корректности
   */
  public final boolean isCorrect() {
    if (isCE()) {
      return true;
    }
    if (resourceName != "" && resource != null) {
      getContents().clear();
      addField(RESNAME_IDX, resourceName);
      addField(RES_IDX, resource);
      setCE(true);
    }
    return isCE();
  }
  public final boolean isRoutable() {
    return false;
  }
} // ODResourceAcquiredMessage
