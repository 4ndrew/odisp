package com.novel.stdmsg;

import com.novel.odisp.common.Message;
import java.util.List;
import java.util.ArrayList;
import java.io.Serializable;

/** Реализация стандартного сообщения для стандартного диспетчера ODISP.
 * @author Валентин А. Алексеев
 * @author (C) 2003, НПП "Новел-ИЛ"
 * @version $Id: StandartMessage.java,v 1.6 2004/02/17 10:56:00 valeks Exp $
 */
public class StandartMessage implements Message, Serializable {
  /** Внутренний уникальный счетчик сообщения. */
  private static int id = 0;
  /** Уникальный индекс сообщения в системе. */
  private int myId;
  /** Список полей сообщения. */
  private List fields = new ArrayList();
  /** Действие. */
  private String action;
  /** Точка назначения. */
  private String destination;
  /** Отправитель. */
  private String origin;
  /** Идентификатор сообщения на которое производится ответ. */
  private int inReplyTo;
  /** Флаг проведения проверки. */
  private boolean ce = false;
  /** Реализация конструктора сообщения.
   * @param newAction действие которое несет сообщение
   * @param newDestination адресат сообщения
   * @param newOrigin отправитель сообщения
   * @param newInReplyTo идентификатор сообщения на которое производится ответ
   */
  public StandartMessage(final String newAction,
			 final String newDestination,
			 final String newOrigin,
			 final int newInReplyTo) {
    action = newAction;
    destination = newDestination;
    inReplyTo = newInReplyTo;
    origin = newOrigin;
    myId = id++;
  }

  /** Конструктор по-умолчанию. */
  public StandartMessage() {
    myId = id++;
  }

  /** Добавление произвольного объекта в тело сообщения.
   * @param field объект который будет добавлен сообщение
   */
  public final void addField(final Object field) {
    fields.add(field);
  }

  /** Выборка сохраненного в теле сообщения объекта по индексу.
   * @param field индекс объекта
   * @return поле сообщения
   */
  public final Object getField(final int field) {
    return fields.get(field);
  }

  /** Возвращает действие которое несет сообщение.
   * @return действие
   */
  public final String getAction() {
    return action;
  }

  /** Устанавливает действие которое несет сообщение.
   * @param newAction действие
   */
  public final void setAction(final String newAction) {
    action = newAction;
  }

  /** Возвращает адресата сообщения.
   * @return адресат
   */
  public final String getDestination() { return destination; }

  /** Устанавливает адресата сообщения.
   * @param newDest адресат
   */
  public final void setDestination(final String newDest) {
    destination = newDest;
  }

  /** Возвращает автора сообщения.
   * @return автор
   */
  public final String getOrigin() {
    return origin;
  }

  /** Изменить отправителя на лету.
   * @param newOrigin новое значение поля отправителя
   */
  public final void setOrigin(final String newOrigin) {
    this.origin = newOrigin;
  }

  /** Возвращает идентификатор сообщения на которое производится ответ.
   * @return идентификатор
   */
  public final int getReplyTo() {
    return inReplyTo;
  }

  /** Устанавливает идентификатор сообщения на которое производится ответ.
   * @param newId идентификатор
   */
  public final void setReplyTo(final int newId) {
    inReplyTo = newId;
  }

  /** Возвращает количество полей в сообщении.
   * @return кол-во полей
   */
  public final int getFieldsCount() {
    return fields.size();
  }

  /** Возвращает уникальный идентификатор сообщения.
   * @return идентификатор
   */
  public final int getId() {
    return myId;
  }

  /** Устанавливает уникальный идентификатор сообщения.
   * @param newId идентификатор
   */
  public final void setId(final int newId) {
    myId = newId;
  }

  /** Представление сообщения в виде текстовой строки.
   * @return строчное представление сообщения
   */
  public final String toString() {
    return "stdmessage id=" + myId + " replyto=" + inReplyTo
      + " action=" + action + ", destination=" + destination
      + ", origin=" + origin + ", fields.size()=" + fields.size();
  }

  /** Представление сообщения в виде текстовой строки с дампом пяти последних вызово.
   * @return строчное представление сообщения
   */
  public final String toString(boolean doStackTrace) {
    if (!doStackTrace) {
      return toString();
    }
    // небольшой хак для того, что бы получить список вызовов методов
    StackTraceElement[] stea = {};
    try {
      throw new Exception();
    } catch (Exception e) {
      stea = e.getStackTrace();
    }
    String stackTraceMessage = "";
    for(int i = 1; i < 10 && i < stea.length; i++) {
      stackTraceMessage += "\n" + stea[i];
    }
    return "stdmessage id=" + myId + " replyto=" + inReplyTo
      + " action=" + action + ", destination=" + destination
      + ", origin=" + origin + ", fields.size()=" + fields.size()
      + ", stacktrace: " + stackTraceMessage;
  }

  /** Проверка корректности сообщения.
   * @return флаг корректности
   */
  public boolean isCorrect() {
    ce = true;
    return true;
  }

  /** Доступ ко всему списку полей.
   * @return список полей
   */
  protected final List getFields() {
    return fields;
  }

  /** Установить флаг корректности.
   * @param newCE новое значение
   */
  public final void setCE(final boolean newCE) {
    ce = newCE;
  }

  /** Проверить флаг корректности.
   * @return значение флага
   */
  public final boolean isCE() {
    return ce;
  }
}
