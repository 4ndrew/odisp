package com.novel.stdmsg;

/** Класс реализующий сообщение диспетчеру ODISP об останове.
 * Это сообщение должно обрабатыватся только диспетчером.
 * В качестве параметра можно указать код выхода который будет рассылатся объектам.
 * @author <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @author (C) 2003, НПП "Новел-ИЛ"
 * @version $Id: ODShutdownMessage.java,v 1.5 2003/12/15 14:02:43 valeks Exp $
 */

public class ODShutdownMessage extends StandartMessage {
  /** Символьное имя сообщения*/
  public static final String name = "od_shutdown";
  
  private transient int exitCode = 0;

  /** Создает новое сообщение с заданными параметрами 
   * @param origin отправитель
   * @param replyTo индекс сообщения на которое производится ответ
   */
  public ODShutdownMessage(String origin, int replyTo) {
    super("od_shutdown", origin, "stddispatcher", replyTo);
  }

  /** Вернуть значение кода выхода
   * @return код выхода
   */
  public int getExitCode() {
    if (ce) {
      return ((Integer) getField(0)).intValue();
    }
    return exitCode;
  }

  /** Установить значение кода выхода
   * @param newExitCode новое значение кода выхода
   */
  public ODShutdownMessage setExitCode(int newExitCode) {
    exitCode = newExitCode;
    return this;
  }
  
  public boolean isCorrect() {
    if (ce) {
      return true;
    }
    fields.clear();
    addField(new Integer(exitCode));
    return true;
  }

}// ODShutdownMessage
