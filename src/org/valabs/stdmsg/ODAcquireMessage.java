package com.novel.stdmsg;

/** Запрос диспетчера на захват ресурса.
 * <p>Запрос должен содержать минимум 1 параметр - имя ресурсного объекта.
 * Необязательный дополнительный параметр (по умолчанию - ложь) определяет
 * будет ли объект считатся блокированным до тех пор пока он не возвратит
 * ресурс.</p>
 * @author <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @author (C) 2003, НПП "Новел-ИЛ"
 * @version $Id: ODAcquireMessage.java,v 1.9 2004/04/02 09:54:49 valeks Exp $
 */

public class ODAcquireMessage extends StandartMessage {
  /** Символьное имя сообщения. */
  public static final String NAME = "od_acquire";
  /** Имя ресурса. */
  private transient String resourceName;
  /** Индекс блокировки в списке полей. */
  private static final String NAME_IDX = "0";
  /** Флаг блокировки. */
  private transient boolean willBlock = false;
  /** Индекс флага блокировки. */
  private static final String BLOCK_IDX = "1";
  /** Создать новое сообщение диспетчеру с запросом на захват ресурса.
   * @param origin автор
   * @param replyTo в ответ на сообщение No.
   */
  public ODAcquireMessage(final String origin, final int replyTo) {
    super("od_acquire", "dispatcher", origin, replyTo);
  }

  /** Вернуть имя ресурса для захвата.
   * @return имя ресурса
   */
  public final String getResourceName() {
    if (isCE()) {
      return (String) getField(NAME_IDX);
    }
    return resourceName;
  }

  /** Установить имя ресурса.
   * @param newName новое имя
   * @return ссылка на текущее сообщение
   */
  public final ODAcquireMessage setResourceName(final String newName) {
    resourceName = newName;
    return this;
  }

  /** Вернуть режим захвата.
   * по умолчанию - неблокирующий захват
   * @return режим захвата
   */
  public final boolean getWillBlock() {
    if (isCE()) {
      return ((Boolean) getField(BLOCK_IDX)).booleanValue();
    }
    return willBlock;
  }

  /** Установить режим блокировки.
   * @param newBlock новый режим
   * @return ссылка на текущее сообщение
   */
  public final ODAcquireMessage setWillBlock(final boolean newBlock) {
    willBlock = newBlock;
    return this;
  }

  /** Проверка корректности сообщения.
   * @return флаг корректности
   */
  public final boolean isCorrect() {
    if (isCE()) {
      return true;
    }
    if (resourceName != "") {
      getContents().clear();
      addField(NAME_IDX, resourceName);
      addField(BLOCK_IDX, new Boolean(willBlock));
      setCE(true);
      return true;
    } else {
      return false;
    }
  }

  public boolean isRoutable() {
    return false;
  }
} // ODAcquireMessage
