package org.valabs.stdmsg;

import org.valabs.odisp.common.Message;
import org.valabs.odisp.common.Resource;

/** Ответ на запрос о захвате ресурса.
 * @author <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @author (C) 2003, НПП "Новел-ИЛ"
 * @version $Id: ODResourceAcquiredMessage.java,v 1.15 2004/08/23 07:42:36 valeks Exp $
 */
public class ODResourceAcquiredMessage extends StandartMessage {
  /** Символьное имя сообщения. */
  public static final String NAME = "od_resource_acquired";
  /** Индекс имени ресурса. */
  private static final String RESNAME_IDX = "resname";
  /** Индекс ресурса. */
  private static final String RES_IDX = "resobj";

  /** Конструктор сообщения.
   * @param destination получатель сообщения
   * @param replyId сообщение на которое производится ответ
   */
  public static final void setup(final Message msg,
				 final String destination,
				 final int replyId) {
    msg.setAction(NAME);
    msg.setOrigin("dispatcher");
    msg.setDestination(destination);
    msg.setReplyTo(replyId);
    msg.setRoutable(false);
    msg.setCorrect(false);
  }
  /** Вернуть имя класса ресурса.
   * @return имя класса
   */
  public static final String getResourceName(final Message msg) {
    return (String) msg.getField(RESNAME_IDX);
  }

  /** Установить имя класса ресурса.
   * @param newResourceName новое имя класса
   * @return ссылка на сообщение
   */
  public final static void setResourceName(final Message msg, final String newResourceName) {
    msg.addField(RESNAME_IDX, newResourceName);
    if (msg.getContents().size() == 2) {
      msg.setCorrect(true);
    }
  }

  /** Вернуть ссылку на ресурс.
   * @return ссылка на ресурс
   */
  public final static Resource getResource(final Message msg) {
    return (Resource) msg.getField(RES_IDX);
  }

  /** Установить ссылку на ресурс.
   * @param newResource новое значение ссылки
   * @return ссылка на сообщение
   */
  public final static void setResource(final Message msg, final Resource newResource) {
    msg.addField(RES_IDX, newResource);
    if (msg.getContents().size() == 2) {
      msg.setCorrect(true);
    }
  }

  public static final boolean equals(final Message msg) {
    return msg.getAction().equals(NAME);
  }
} // ODResourceAcquiredMessage
