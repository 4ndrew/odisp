package com.novel.stdmsg;

/** Запрос диспетчера на захват ресурса.
 * <p>Запрос должен содержать минимум 1 параметр - имя ресурсного объекта.
 * Необязательный дополнительный параметр (по умолчанию - ложь) определяет
 * будет ли объект считатся блокированным до тех пор пока он не возвратит ресурс.</p>
 * <p><b>ВНИМАНИЕ</b>: текущая реализация стандартного диспетчера не позволяет корректно 
 * отслеживать быль ли высвобожден ресурс, который использовался с блокировкой или нет.
 * Для того, что бы избежать некорректного поведения, рекомендуется одновременно производить 
 * захват не более одного ресурса с установлением режима блокировки.</p>
 * @author <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @author (C) 2003, НПП "Новел-ИЛ"
 * @version $Id: ODAcquireMessage.java,v 1.2 2003/12/03 19:17:53 valeks Exp $
 */

public class ODAcquireMessage extends StandartMessage {
  /** Создать новое сообщение диспетчеру с запросом на захват ресурса
   * @param origin автор
   * @param replyTo в ответ на сообщение No.
   */
  public ODAcquireMessage(String origin, int replyTo) {
    super("od_acquire", "stddispatcher", origin, replyTo);
  }

  /** Вернуть имя ресурса для захвата
   * @return имя ресурса
   */
  public String getResourceName() {
    return (String) getField(0);
  }

  /** Установить имя ресурса
   * @param newName новое имя
   * @return ссылка на текущее сообщение
   */
  public ODAcquireMessage setResourceName(String newName) {
    fields.add(0, newName);
    return this;
  }

  /** Вернуть режим захвата
   * по умолчанию - неблокирующий захват
   * @return режим захвата
   */
  public boolean getWillBlock() {
    boolean result = false;
    if (getFieldsCount() == 2) {
      result = ((Boolean) getField(1)).booleanValue();
    }
    return result;
  }

  /** Установить режим блокировки
   * @param newBlock новый режим
   * @return ссылка на текущее сообщение
   */
  public ODAcquireMessage setWillBlock(boolean newBlock) {
    fields.add(1, new Boolean(newBlock));
    return this;
  }
  
}// ODAcquireMessage
