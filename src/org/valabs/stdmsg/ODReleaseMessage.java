package com.novel.stdmsg;

import com.novel.odisp.common.Resource;

/** Запрос диспетчера на высвобождение ресурса.
 * <p>Запрос должен содержать минимум 1 параметр - имя ресурсного объекта.
 * Необязательный дополнительный параметр (по умолчанию - ложь) определяет
 * будет ли объект считатся блокированным до тех пор пока он не возвратит ресурс.</p>
 * <p><b>ВНИМАНИЕ</b>: текущая реализация стандартного диспетчера не позволяет корректно 
 * отслеживать быль ли высвобожден ресурс, который использовался с блокировкой или нет.
 * Для того, что бы избежать некорректного поведения, рекомендуется одновременно производить 
 * захват не более одного ресурса с установлением режима блокировки.</p>
 * @author <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @author (C) 2003, НПП "Новел-ИЛ"
 * @version $Id: ODReleaseMessage.java,v 1.2 2003/12/03 21:12:44 valeks Exp $
 */

public class ODReleaseMessage extends StandartMessage {
  /** Создать новое сообщение диспетчеру с запросом на захват ресурса
   * @param origin автор
   * @param replyTo в ответ на сообщение No.
   */
  public ODReleaseMessage(String origin, int replyTo) {
    super("od_release", "stddispatcher", origin, replyTo);
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
  public ODReleaseMessage setResourceName(String newName) {
    fields.add(0, newName);
    return this;
  }

  /** Вернуть ссылку на ресурс
   * @return ссылка на ресурс
   */
  public Resource getResource() {
    return (Resource) getField(1);
  }

  /** Установить новое значение ссылки на ресурс
   * @param newResource новое значение ссылки
   * @return ссылка на текущее сообщение
   */
  public ODReleaseMessage setResource(Resource newResource) {
    fields.add(1, newResource);
    return this;
  }
  
}// ODReleaseMessage
