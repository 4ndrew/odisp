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
 * @version $Id: ODAcquireMessage.java,v 1.4 2003/12/15 14:02:43 valeks Exp $
 */

public class ODAcquireMessage extends StandartMessage {
  /** Символьное имя сообщения*/
  public static final String name = "od_acquire";
  private transient String resourceName;
  private transient boolean willBlock = false;
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
    if (ce) {
      return (String) getField(0);
    }
    return resourceName;
  }

  /** Установить имя ресурса
   * @param newName новое имя
   * @return ссылка на текущее сообщение
   */
  public ODAcquireMessage setResourceName(String newName) {
    resourceName = newName;
    return this;
  }

  /** Вернуть режим захвата
   * по умолчанию - неблокирующий захват
   * @return режим захвата
   */
  public boolean getWillBlock() {
    if (ce) {
      return ((Boolean) getField(1)).booleanValue();
    }
    return willBlock;
  }

  /** Установить режим блокировки
   * @param newBlock новый режим
   * @return ссылка на текущее сообщение
   */
  public ODAcquireMessage setWillBlock(boolean newBlock) {
    willBlock = newBlock;
    return this;
  }
  
  public boolean isCorrect() {
    if (ce) {
      return true;
    }
    if (resourceName != "") {
      fields.clear();
      addField(resourceName);
      addField(new Boolean(willBlock));
      ce = true;
      return true;
    } else {
      return false;
    }
  }
}// ODAcquireMessage
