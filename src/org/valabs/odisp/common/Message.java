package com.novel.odisp.common;

/** Интерфейс сообщения ядра ODISP системы
 * @author Валентин А. Алексеев
 * @author (C) 2003, НПП "Новел-ИЛ"
 * @version $Id: Message.java,v 1.2 2003/12/15 13:02:35 valeks Exp $
 */
public interface Message {
  /** Добавление произволього объекта в тело сообщения
   * @param field объект для добавления
   */
  void addField(Object field);
  /**Выборка объекта по индексу
   * @param field индекс объекта в теле сообщения
   * @return Object содержимое поля
   */
  Object getField(int field);
  /** Количество объектов в теле сообщения 
   * @return кол-во объектов
   */
  int getFieldsCount();
  /** Уникальный идентификатор сообщения 
   * @return идентификатор
   */
  int getId();
  /** Установить уникальный идентификатор сообщения 
   * @param id идентификатор
   */
  void setId(int id);
  /** Идентификатор сообщения на который идет ответ 
   * @return идентификатор
   */
  int getReplyTo();
  /** Действие которое несет сообщение 
   * @return действие
   */
  String getAction();
  /** Получатель сообщения
   * @return получатель
   */
  String getDestination();
  void setDestination(String nd);
  void setReplyTo(int nrpt);
  void setAction(String na);
  /** Вернуть имя отправителя */
  String getOrigin();
  /** Заменить отправитея */
  void setOrigin(String newOrigin);

  /** Проверка корректности сообщения*/
  boolean isCorrect();
}
