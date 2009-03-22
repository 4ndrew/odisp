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
package org.valabs.stdmsg;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.doomdark.uuid.UUID;
import org.doomdark.uuid.UUIDGenerator;
import org.valabs.odisp.common.Message;

/** Реализация стандартного сообщения для стандартного диспетчера ODISP.
 * 
 * @author (C) 2003-2005 <a href="mailto:valeks@novel-il.ru">Валентин А. Алексеев</a>
 * @author (C) 2003-2005 <a href="mailto:dron@novel-il.ru">Андрей А. Порохин</a>
 * @version $Id: StandartMessage.java,v 1.35 2005/11/21 15:02:54 dron Exp $
 */
public class StandartMessage implements Message, Serializable, Cloneable {
  static final long serialVersionUID = 1L;
  /** Флаг маршрутизации. */
  private boolean routable = true;
  /** Уникальный индекс сообщения в системе. */
  private UUID myId;
  /** Список полей сообщения. */
  private final Map fields = new Hashtable(5);
  /** Действие. */
  private String action = null;
  /** Точка назначения. */
  private String destination = null;
  /** Отправитель. */
  private String origin = null;
  /** Идентификатор сообщения на которое производится ответ. */
  private UUID inReplyTo = null;
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
			 final UUID newInReplyTo) {
    action = newAction;
    destination = newDestination;
    inReplyTo = newInReplyTo;
    origin = newOrigin;
    myId = UUIDGenerator.getInstance().generateTimeBasedUUID();
  }

  /** Конструктор по-умолчанию. */
  public StandartMessage() {
    myId = UUIDGenerator.getInstance().generateTimeBasedUUID();
  }

  /** Копирование сообщения. */
  private void copyFrom(final Message msg, final boolean noKeepId) {
    action = msg.getAction();
    destination = msg.getDestination();
    inReplyTo = msg.getReplyTo();
    origin = msg.getOrigin();
    fields.clear();
    fields.putAll(msg.getContents());
    routable = msg.isRoutable();
    if (noKeepId) {
      myId = UUIDGenerator.getInstance().generateTimeBasedUUID();
    } else {
      myId = msg.getId();
    }
    setCorrect(msg.isCorrect());
  }

  /** Копирующий конструктор.
   * @param msg сообещние с которого снимается копия
   * @param noKeepId не восстанавливать идентификатор сообщения
   */
  public StandartMessage(final Message msg, final boolean noKeepId) {
    copyFrom(msg, noKeepId);
  }

  /** Копирующий конструктор сохрянющий номер сообщения. 
   * @param msg исходное сообщение
   */
  public StandartMessage(final Message msg) {
    copyFrom(msg, false);
  }

  /** Возвращает действие которое несет сообщение.
   * @return действие
   */
  public final String getAction() {
  	assert action != null : "Action is null";
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
  public final String getDestination() {
  	assert destination != null : "Destination is null";
  	return destination;
  }

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
  	assert origin != null : "Origin is null";
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
  public final UUID getReplyTo() {
    return inReplyTo;
  }

  /** Устанавливает идентификатор сообщения на которое производится ответ.
   * @param newId идентификатор
   */
  public final void setReplyTo(final UUID newId) {
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
  public final UUID getId() {
    return myId;
  }

  /** Устанавливает уникальный идентификатор сообщения.
   * @param newId идентификатор
   */
  public final void setId(final UUID newId) {
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
   * @param doStackTrace выводить или нет дамп вызовов методов
   */
  public final String toString(final boolean doStackTrace) {
    String result = toString();
    if (doStackTrace) {
      // небольшой хак для того, что бы получить список вызовов методов
      StackTraceElement[] stea = {};
      try {
        throw new IllegalArgumentException();
      } catch (Exception e) {
        stea = e.getStackTrace();
      }
      result += "\nStack trace:";
      for (int i = 1; i < 10 && i < stea.length; i++) {
        result += stea[i] + "\n";
      }
      String fieldDump = "\nField dump:";
      final Iterator it = fields.keySet().iterator();
      while (it.hasNext()) {
        final String key = (String) it.next();
        fieldDump += "\n(" + key + ") " + getField(key);
      }
      result += fieldDump;
    }
    return result;
  }

  /** Проверка корректности сообщения.
   * @return флаг корректности
   */
  public boolean isCorrect() {
    return ce;
  }

  public final Map getContents() {
    return fields;
  }

  /** Установка флага корректности.
   * @param newCE true -- сообщение корректно, false -- сообщение некорректно
   */
  public final void setCorrect(final boolean newCE) {
    ce = newCE;
  }

  public boolean isRoutable() {
    return routable;
  }
  public void setRoutable(final boolean newRoutable) {
    routable = newRoutable;
  }

  public void addField(final String name, final Object value) {
    if (value == null) {
      return;
    }
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
//    debugMGW.logMessage(this);
    return oob;
  }
  
  /** Установка флага OOB.
   * @param newValue новое значение.
   */
  public void setOOB(boolean newValue) {
    oob = newValue;
  }

  /* (non-Javadoc)
   * @see org.valabs.odisp.common.Message#cloneMessage()
   */
  public final Message cloneMessage() {
    Message result = null;
    try {
      result = (Message) this.clone();
    } catch (CloneNotSupportedException e) { /* игнорируется */ }
    return result;
  }
}
