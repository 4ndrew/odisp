package com.novel.odisp.common;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

/** Базовый класс реализующий почтовый ящик для сообщений
* посылаемых диспетчером ODISP.
* @author Валентин А. Алексеев
* @author (С) 2003-2004, НПП "Новел-ИЛ"
* @version $Id: PollingODObject.java,v 1.21 2004/05/11 09:55:39 valeks Exp $
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
