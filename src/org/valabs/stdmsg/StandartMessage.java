package org.valabs.stdmsg;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.valabs.odisp.common.Message;

/** Реализация стандартного сообщения для стандартного диспетчера ODISP.
 * 
 * @author (C) 2003-2004 <a href="mailto:valeks@novel-il.ru">Валентин А. Алексеев</a>
 * @author (C) 2003-2004 <a href="mailto:dron@novel-il.ru">Андрей А. Порохин</a>
 * @version $Id: StandartMessage.java,v 1.22 2004/08/30 10:07:12 valeks Exp $
 */
public class StandartMessage implements Message, Serializable {
  /** Флаг маршрутизации. */
  private boolean routable = true;
  /** Внутренний уникальный счетчик сообщения. */
  private static int id = 0;
  /** Уникальный индекс сообщения в системе. */
  private int myId = -1;
  /** Список полей сообщения. */
  private Map fields = new HashMap();
  /** Действие. */
  private String action = null;
  /** Точка назначения. */
  private String destination = null;
  /** Отправитель. */
  private String origin = null;
  /** Идентификатор сообщения на которое производится ответ. */
  private int inReplyTo = -1;
  /** Индекс последнего добавленного поля. */
  private int lastIdx = 0;
  /** Флаг проведения проверки. */
  private boolean ce = false;
  /** Список подсообщений. */
  private List envelope = null;
  /** Признак OOB. */
  private boolean oob = false;
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

  /** Копирование сообщения. */
  private void copyFrom(final Message msg, final boolean noKeepId) {
    action = msg.getAction();
    destination = msg.getDestination();
    inReplyTo = msg.getReplyTo();
    origin = msg.getOrigin();
    fields = new HashMap(msg.getContents());
    routable = msg.isRoutable();
    if (noKeepId) {
      myId = id++;
    } else {
      myId = msg.getId();
    }
    setCE(msg.isCorrect());
  }

  /** Копирующий конструктор.
   * @param msg сообещние с которого снимается копия
   * @param noKeepId не восстанавливать идентификатор сообщения
   */
  public StandartMessage(final Message msg, final boolean noKeepId) {
    copyFrom(msg, noKeepId);
  }

  /** Копирующий конструктор сохрянющий номер сообщения. */
  public StandartMessage(final Message msg) {
    copyFrom(msg, false);
  }

  /** Добавление произвольного объекта в тело сообщения.
   * @param field объект который будет добавлен сообщение
   * @deprecated используйте addField(String, Object)
   */
  protected final void addField(final Object field) {
    addField((new Integer(lastIdx++)).toString(), field);
  }

  /** Выборка сохраненного в теле сообщения объекта по индексу.
   * @param field индекс объекта
   * @return поле сообщения
   * @deprecated используйте getField(String)
   */
  protected final Object getField(final int field) {
    return getField((new Integer(field)).toString());
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
      + ", origin=" + origin + ", fields.size()=" + fields.size() + (isRoutable() ? " routable" :"");
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
    String stackTraceMessage = "\nStack trace:";
    for(int i = 1; i < 10 && i < stea.length; i++) {
      stackTraceMessage += stea[i] + "\n";
    }
    String fieldDump = "\nField dump:";
    Iterator it = fields.keySet().iterator();
    while (it.hasNext()) {
      String key = (String) it.next();
      fieldDump += "\n(" + key + ") " + getField(key);
    }
    return toString() + stackTraceMessage + fieldDump;
  }

  /** Проверка корректности сообщения.
   * @return флаг корректности
   */
  public boolean isCorrect() {
    return ce;
  }

  /** Доступ ко всему списку полей.
   * @return список полей
   * @deprecated Используйте getContents()
   */
  protected final List getFields() {
    return new ArrayList(fields.values());
  }

  public final Map getContents() {
    return fields;
  }

  /** Установить флаг корректности.
   * @param newCE новое значение
   * @deprecated move to new-styled Typed Messages, use setCorrect
   */
  public final void setCE(final boolean newCE) {
    setCorrect(newCE);
  }

  /** Установка флага корректности.
   */
  public final void setCorrect(final boolean newCE) {
    ce = newCE;
  }

  /** Проверить флаг корректности.
   * @return значение флага
   * @deprecated move to new-styled Typed Messages, use isCorrect
   */
  public final boolean isCE() {
    return ce;
  }
  public boolean isRoutable() {
    return routable;
  }
  public void setRoutable(final boolean newRoutable) {
    routable = newRoutable;
  }

  public void addField(final String name, final Object value) {
    if (fields.containsKey(name)) {
      fields.remove(name);
    }
    fields.put(name, value);
  }

  public Object getField(final String name) {
    return fields.get(name);
  }
  
  public final List getEnvelope() {
  	return envelope;
  }
  
  public final void addToEnvelope(final Message envelopeMessage) {
  	if (envelope == null) {
  		envelope = new ArrayList();
  	}
  	envelope.add(envelopeMessage);
  }
  
  /** Проверка на OOB.
   * @return true если сообщение OOB.
   */
  public boolean isOOB() {
  	return oob;
  }
  
  /** Установка флага OOB.
   * @param newValue новое значение.
   */
  public void setOOB(boolean newValue) {
  	oob = newValue;
  }
}
