package com.novel.odisp.common;

import java.util.List;
import java.util.Map;

/** Интерфейс сообщения ядра ODISP системы.
 * @author Валентин А. Алексеев
 * @author (C) 2003, НПП "Новел-ИЛ"
 * @version $Id: Message.java,v 1.7 2004/03/26 21:53:37 valeks Exp $
 */
public interface Message {
  /** Добавление произволього объекта в тело сообщения.
   * @param field объект для добавления
   * @deprecated необходимо использовать #addField(String, Object)
   */
  void addField(Object field);

  /** Выборка объекта по индексу.
   * @param field индекс объекта в теле сообщения
   * @deprecated необходимо использовать #getField(String)
   * @return содержимое поля
   */
  Object getField(int field);

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

  /** Вернуть описание сообщения включая список последних 5 вызовов.
   * @param willStackTrace
   * @return сформатированное сообщение
   */
  String toString(boolean willStackTrace);

  /** Доступ ко всему списку полей.
   * @return список полей
   * @deprecated необходимо использовать #getContents()
   */
  List getFields();

  /** Возможно ли маршрутизировать сообщение между диспетчерами (приложениями).
   * @return флаг маршрутизации
   */
  boolean isRoutable();

  /** Возможно ли маршрутизировать сообщение между диспетчерами (приложениями)
   * @return флаг маршрутизации
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
}
