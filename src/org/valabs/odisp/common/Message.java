/* ODISP -- Message Oriented Middleware
 * Copyright (C) 2003-2005 Valentin A. Alekseev
 * Copyright (C) 2003-2005 Andrew A. Porohin 
 * 
 * ODISP is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, version 2.1 of the License.
 * 
 * ODISP is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with ODISP.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.valabs.odisp.common;

import java.util.List;
import java.util.Map;

import org.doomdark.uuid.UUID;

/** Интерфейс сообщения ядра ODISP системы.
 * @author (C) 2004 <a href="mailto:valeks@novel-il.ru">Валентин А. Алексеев</a>
 * @version $Id: Message.java,v 1.20 2005/02/27 12:37:29 valeks Exp $
 */
public interface Message {

  Message cloneMessage();
  String RECIPIENT_ALL = ".*";
  String RECIPIENT_CATCHALL = "catch-all-service";
  
  /** Выбор поля по имени.
   * @param name имя поля
   * @return содержимое поля
   */
  Object getField(String name);

  /** Количество объектов в теле сообщения.
   * @return кол-во объектов
   */
  int getFieldsCount();

  /** Уникальный идентификатор сообщения.
   * @return идентификатор
   */
  UUID getId();

  /** Установить уникальный идентификатор сообщения.
   * @param newId идентификатор
   */
  void setId(UUID newId);

  /** Идентификатор сообщения на который идет ответ.
   * @return идентификатор
   */
  UUID getReplyTo();

  /** Действие которое несет сообщение.
   * @return действие
   */
  String getAction();

  /** Получатель сообщения.
   * @return получатель
   */
  String getDestination();

  /** Установить получателя сообщения.
   * @param newDest новое значение
   */
  void setDestination(String newDest);

  /** Установить индекс ответа.
   * @param nrpt индекс сообщения на которое производится ответ
   */
  void setReplyTo(UUID nrpt);

  /** Установить действие.
   * @param newAction новое действие
   */
  void setAction(String newAction);

  /** Вернуть имя отправителя.
   * @return имя отправителя
   */
  String getOrigin();

  /** Заменить отправитея.
   * @param newOrigin новое имя отправителя
   */
  void setOrigin(String newOrigin);

  /** Проверка корректности сообщения.
   * @return флаг корректности
   */
  boolean isCorrect();

  /** Установка флага корректности сообщения.
   * @param newCorrectFlag новое значение
   */
  void setCorrect(boolean newCorrectFlag);

  /** Вернуть описание сообщения включая список последних 5 вызовов.
   * @param willStackTrace будет ли генерироваться более полный дамп сообщения
   * @return сформатированное сообщение
   */
  String toString(boolean willStackTrace);

  /** Возможно ли маршрутизировать сообщение между диспетчерами (приложениями).
   * @return флаг маршрутизации
   */
  boolean isRoutable();

  /** Возможно ли маршрутизировать сообщение между диспетчерами (приложениями). 
   * @param newRoutable новое значение флага маршрутизации
   */
  void setRoutable(boolean newRoutable);

  /** Доступ к содержимому сообщения напрямую.
   * @return таблица полей
   */
  Map getContents();

  /** Добавление поля с заданным именем.
   * @param name имя поля
   * @param value значение поля
   */
  void addField(String name, Object value);

  /** Доступ к подсообщениям.
   * @return список подсообщений
   */
  List getEnvelope();

  /** Добавление подсообщения.
   * @param envelopeMessage подсообщение
   */
  void addToEnvelope(Message envelopeMessage);

  /** Проверка на OOB.
   * @return true в случае если сообщение с повышеным приоритетом.
   */
  boolean isOOB();
  
  /** Установка флага OOB.
   * @param newValue новое значение OOB флага.
   */
  void setOOB(boolean newValue);
}
