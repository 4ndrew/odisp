package com.novel.stdmsg;

/** Класс реализующий сообщение диспетчеру ODISP об изменении
 * списка зависимостей.
 * Это сообщение должно обрабатыватся только диспетчером.
 * В качестве параметра нужно указать имя зависимости.
 * @author <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @author (C) 2003, НПП "Новел-ИЛ"
 * @version $Id: ODRemoveDepMessage.java,v 1.6 2004/02/15 20:58:57 valeks Exp $
 */

public class ODRemoveDepMessage extends StandartMessage {
  /** Символьное имя сообщения. */
  public static final String NAME = "od_remove_dep";
  /** Имя зависимости. */
  private transient String depName = "";
  /** Индекс имени. */
  private static final int DEPNAME_IDX = 0;
  /** Создает новое сообщение с заданными параметрами.
   * @param origin отправитель
   * @param replyTo индекс сообщения на которое производится ответ
   */
  public ODRemoveDepMessage(final String origin, final int replyTo) {
    super("od_remove_dep", "stddispatcher", origin, replyTo);
  }

  /** Вернуть значение кода выхода.
   * @return код выхода
   */
  public final String getDepName() {
    if (isCE()) {
      return (String) getField(DEPNAME_IDX);
    }
    return depName;
  }

  /** Установить значение имени зависимости.
   * @param newDepName новое имя зависимости
   * @return ссылка на сообщение
   */
  public final ODRemoveDepMessage setDepName(final String newDepName) {
    depName = newDepName;
    return this;
  }

  /** Проверка корректности сообщения.
   * @return флаг корректности
   */
  public final boolean isCorrect() {
    if (isCE()) {
      return true;
    }
    if (depName != "") {
      getFields().clear();
      addField(depName);
      setCE(true);
    }
    return isCE();
  }
} // ODShutdownMessage
