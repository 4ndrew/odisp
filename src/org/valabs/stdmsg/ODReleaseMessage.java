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
 * @version $Id: ODReleaseMessage.java,v 1.4 2003/12/15 14:02:43 valeks Exp $
 */

public class ODReleaseMessage extends StandartMessage {
  /** Символьное имя сообщения*/
  public static final String name = "od_object_loaded";

  private transient String resourceName = "";
  private transient Resource resource;

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
    if (ce) { 
      return (String) getField(0);
    }
    return resourceName;
  }

  /** Установить имя ресурса
   * @param newName новое имя
   * @return ссылка на текущее сообщение
   */
  public ODReleaseMessage setResourceName(String newName) {
    resourceName = newName;
    return this;
  }

  /** Вернуть ссылку на ресурс
   * @return ссылка на ресурс
   */
  public Resource getResource() {
    if (ce) {
      return (Resource) getField(1);
    }
    return resource;
  }

  /** Установить новое значение ссылки на ресурс
   * @param newResource новое значение ссылки
   * @return ссылка на текущее сообщение
   */
  public ODReleaseMessage setResource(Resource newResource) {
    resource = newResource;
    return this;
  }
  
  public boolean isCorrect() {
    if (ce) {
      return true;
    }
    if (resourceName != "" && resource != null) {
      fields.clear();
      addField(resourceName);
      addField(resource);
      ce = true;
      return true;
    } else {
      return false;
    }
  }

}// ODReleaseMessage
