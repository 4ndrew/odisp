package com.novel.stdmsg;

import com.novel.odisp.common.Message;
/** Класс реализующий сообщение диспетчеру ODISP об останове.
 * Это сообщение должно обрабатыватся только диспетчером.
 * В качестве параметра можно указать код выхода который будет
 * рассылатся объектам.
 * @author <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @author (C) 2003-2004, НПП "Новел-ИЛ"
 * @version $Id: ODShutdownMessage.java,v 1.11 2004/06/09 18:23:12 valeks Exp $
 */

public class ODShutdownMessage {
  /** Символьное имя сообщения. */
  public static final String NAME = "od_shutdown";
  /** Индекс поля в сообщении. */
  private static final String EXITCODE_IDX = "0";

  /** Настраивает сообщение с заданными параметрами.
   * @param origin отправитель
   * @param replyTo индекс сообщения на которое производится ответ
   */
  public static final void setup(final Message msg, final String origin, final int replyTo) {
    msg.setAction(NAME);
    msg.setDestination("dispatcher");
    msg.setOrigin(origin);
    msg.setReplyTo(replyTo);
    msg.setRoutable(false);
    msg.setCorrect(true);
  }

  /** Вернуть значение кода выхода.
   * @return код выхода
   */
  public static final int getExitCode(final Message msg) {
    return ((Integer) msg.getField(EXITCODE_IDX)).intValue();
  }

  /** Установить значение кода выхода.
   * @param newExitCode новое значение кода выхода
   * @return ссылка на сообщение
   */
  public static final void setExitCode(final Message msg, final int newExitCode) {
    msg.addField(EXITCODE_IDX, new Integer(newExitCode));
  }

  public static final boolean equals(final Message msg) {
    return msg.getAction().equals(NAME);
  }
} // ODShutdownMessage
