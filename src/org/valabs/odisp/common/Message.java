package com.novel.odisp.common;

/** Интерфейс сообщения ядра ODISP системы.
 * @author Валентин А. Алексеев
 * @author (C) 2003, НПП "Новел-ИЛ"
 * @version $Id: Message.java,v 1.4 2004/02/17 10:56:00 valeks Exp $
 */
public interface Message {
  /** Добавление произволього объекта в тело сообщения.
   * @param field объект для добавления
   */
  void addField(Object field);

  /** Выборка объекта по индексу.
   * @param field индекс объекта в теле сообщения
   * @return Object содержимое поля
   */
  Object getField(int field);

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
}
