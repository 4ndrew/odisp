package com.novel.stdmsg;

import com.novel.odisp.common.Resource;

/** Запрос диспетчера на высвобождение ресурса.
 * <p>Запрос должен содержать минимум 1 параметр - имя ресурсного объекта.
 * Необязательный дополнительный параметр (по умолчанию - ложь) определяет
 * будет ли объект считатся блокированным до тех пор пока он не возвратит
 * ресурс.</p>
 * <p><b>ВНИМАНИЕ</b>: текущая реализация стандартного диспетчера не
 * позволяет корректно отслеживать быль ли высвобожден ресурс, который
 * использовался с блокировкой или нет. Для того, что бы избежать
 * некорректного поведения, рекомендуется одновременно производить
 * захват не более одного ресурса с установлением режима блокировки.</p>
 * @author <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @author (C) 2003, НПП "Новел-ИЛ"
 * @version $Id: ODReleaseMessage.java,v 1.8 2004/03/31 17:01:40 valeks Exp $
 */

public class ODReleaseMessage extends StandartMessage {
  /** Символьное имя сообщения. */
  public static final String NAME = "od_release";

  /** Имя ресурса. */
  private transient String resourceName = "";
  /** Индекс имени в сообщении. */
  private static final String NAME_IDX = "0";
  /** Ресурс. */
  private transient Resource resource;
  /** Индекс ресурса в сообщении. */
  private static final String RES_IDX = "1";

  /** Создать новое сообщение диспетчеру с запросом на захват ресурса.
   * @param origin автор
   * @param replyTo в ответ на сообщение No.
   */
  public ODReleaseMessage(final String origin, final int replyTo) {
    super("od_release", "stddispatcher", origin, replyTo);
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
  public final ODReleaseMessage setResourceName(final String newName) {
    resourceName = newName;
    return this;
  }

  /** Вернуть ссылку на ресурс.
   * @return ссылка на ресурс
   */
  public final Resource getResource() {
    if (isCE()) {
      return (Resource) getField(RES_IDX);
    }
    return resource;
  }

  /** Установить новое значение ссылки на ресурс.
   * @param newResource новое значение ссылки
   * @return ссылка на текущее сообщение
   */
  public final ODReleaseMessage setResource(final Resource newResource) {
    resource = newResource;
    return this;
  }

  /** Проверка корректности сообщения.
   * @return флаг корректности
   */
  public final boolean isCorrect() {
    if (isCE()) {
      return true;
    }
    if (resourceName != "" && resource != null) {
      getContents().clear();
      addField(NAME_IDX, resourceName);
      addField(RES_IDX, resource);
      setCE(true);
      return true;
    } else {
      return false;
    }
  }
  public final boolean isRoutable() {
    return false;
  }
} // ODReleaseMessage
