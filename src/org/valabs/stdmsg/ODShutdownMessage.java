package com.novel.stdmsg;

/** Класс реализующий сообщение диспетчеру ODISP об останове.
 * Это сообщение должно обрабатыватся только диспетчером.
 * В качестве параметра можно указать код выхода который будет
 * рассылатся объектам.
 * @author <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @author (C) 2003, НПП "Новел-ИЛ"
 * @version $Id: ODShutdownMessage.java,v 1.6 2004/01/16 10:29:15 valeks Exp $
 */

public class ODShutdownMessage extends StandartMessage {
  /** Символьное имя сообщения. */
  public static final String NAME = "od_shutdown";

  /** Код выхода. */
  private transient int exitCode = 0;
  /** Индекс поля в сообщении. */
  private static final int EXITCODE_IDX = 0;

  /** Создает новое сообщение с заданными параметрами.
   * @param origin отправитель
   * @param replyTo индекс сообщения на которое производится ответ
   */
  public ODShutdownMessage(final String origin, final int replyTo) {
    super("od_shutdown", origin, "stddispatcher", replyTo);
  }

  /** Вернуть значение кода выхода.
   * @return код выхода
   */
  public final int getExitCode() {
    if (isCE()) {
      return ((Integer) getField(EXITCODE_IDX)).intValue();
    }
    return exitCode;
  }

  /** Установить значение кода выхода.
   * @param newExitCode новое значение кода выхода
   * @return ссылка на сообщение
   */
  public final ODShutdownMessage setExitCode(final int newExitCode) {
    exitCode = newExitCode;
    return this;
  }

  /** Проверка корректности сообщения.
   * @return флаг корректности
   */
  public final boolean isCorrect() {
    if (isCE()) {
      return true;
    }
    getFields().clear();
    addField(new Integer(exitCode));
    setCE(true);
    return true;
  }

} // ODShutdownMessage
