package com.novel.stdmsg;

import com.novel.odisp.*;
import com.novel.odisp.common.*;

/** Класс реализующий сообщение диспетчеру ODISP об останове.
 * Это сообщение должно обрабатыватся только диспетчером.
 * В качестве параметра можно указать код выхода который будет рассылатся объектам.
 * @author <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @author (C) 2003, НПП "Новел-ИЛ"
 * @version $Id: ODShutdownMessage.java,v 1.2 2003/12/02 14:47:20 valeks Exp $
 */

public class ODShutdownMessage extends StandartMessage {
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
    int exitCode = 0;
    if (getFieldsCount() == 1) {
      exitCode = ((Integer) getField(0)).intValue();
    }
    return exitCode;
  }

  /** Установить значение кода выхода
   * @param newExitCode новое значение кода выхода
   */
  public void setExitCode(int newExitCode) {
    fields.removeAllItems();
    addField(new Integer(newExitCode));
  }
  
}// ODShutdownMessage
