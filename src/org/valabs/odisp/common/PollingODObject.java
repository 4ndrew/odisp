package com.novel.odisp.common;

import java.util.regex.Pattern;

/** Базовый класс реализующий почтовый ящик для сообщений
* посылаемых диспетчером ODISP.
* @author Валентин А. Алексеев
* @author (С) 2003-2004, НПП "Новел-ИЛ"
* @version $Id: PollingODObject.java,v 1.23 2004/05/12 13:40:50 dron Exp $
*/
public abstract class PollingODObject extends ODObject {
  /** Конструктор задающий ODISP имя объекта.
   * @param name имя объекта
   */
  public PollingODObject(final String name) {
    super(name);
  }
  /** Интерфейс добавления сообщения в ящик.
   * @param msg сообщение
   */
  public final void addMessage(final Message msg) {
    if (!Pattern.matches(match, msg.getDestination()) &&
        !Pattern.matches(msg.getDestination(), getObjectName())) {    
      return;    
    }
    handleMessage(msg);
  }
  /** Метод вызываемый для очистки данных класса.
   * @param type признак выхода
   * @return код возврата
   */
  public int cleanUp(final int type) {
    return 0;
  }
}
