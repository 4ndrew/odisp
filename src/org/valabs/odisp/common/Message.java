package com.novel.odisp.common;

import java.util.List;
import java.util.Map;

/** Интерфейс сообщения ядра ODISP системы.
 * @author (C) 2004 <a href="mailto:valeks@novel-il.ru">Валентин А. Алексеев</a>
 * @version $Id: Message.java,v 1.15 2004/08/18 12:48:39 valeks Exp $
 */
public interface Message {

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
  int getId();

  /** Установить уникальный идентификатор сообщения.
   * @param id идентификатор
   */
  void setId(int id);

  /** Идентификатор сообщения на который идет ответ.
   * @return идентификатор
   */
  int getReplyTo();

  /** Действие которое несет сообщение.
   * @return действие
   */
  String getAction();

  /** Получатель сообщения.
   * @return получатель
   */
  String getDestination();

  /** Установить получателя сообщения.
   * @param nd новое значение
   */
  void setDestination(String nd);

  /** Установить индекс ответа.
   * @param nrpt индекс сообщения на которое производится ответ
   */
  void setReplyTo(int nrpt);

  /** Установить действие.
   * @param na новое действие
   */
  void setAction(String na);

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
}
