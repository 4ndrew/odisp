package com.novel.stdmsg;

import com.novel.odisp.common.Message;
import java.util.List;
import java.util.ArrayList;
import java.io.Serializable;

/** Реализация стандартного сообщения для стандартного диспетчера ODISP
 * @author Валентин А. Алексеев
 * @author (C) 2003, НПП "Новел-ИЛ"
 * @version $Id: StandartMessage.java,v 1.1 2003/12/03 19:16:52 valeks Exp $
 */
public class StandartMessage implements Message, Serializable {
  /** Внутренний уникальный счетчик сообщения */
  private static int id = 0;
  /** Уникальный индекс сообщения в системе */
  private int myId;
  /** Список полей сообщения */
  protected List fields = new ArrayList();
  /** Действие */
  private String action;
  /** Точка назначения */
  private String destination;
  /** Отправитель */
  private String origin;
  /** Идентификатор сообщения на которое производится ответ */
  private int inReplyTo;
  /** Реализация конструктора сообщения
   * @param action действие которое несет сообщение
   * @param destination адресат сообщения
   * @param origin отправитель сообщения
   * @param inReplyTo идентификатор сообщения на которое производится ответ
   */
  public StandartMessage(String action, String destination, String origin, int inReplyTo) {
    this.action = action;
    this.destination = destination;
    this.inReplyTo = inReplyTo;
    this.origin = origin;
    myId = id++;
  }
  public StandartMessage(){
    myId = id++;
  }

  /** Добавление произвольного объекта в тело сообщения
   * @param field объект который будет добавлен сообщение
   */
  public void addField(Object field) {
    fields.add(field);
  }
  /** Выборка сохраненного в теле сообщения объекта по индексу
   * @param field индекс объекта
   * @return поле сообщения
   */
  public Object getField(int field) {
    return fields.get(field);
  }
  /** Возвращает действие которое несет сообщение 
   * @return действие
   */
  public String getAction() { 
    return action; 
  }
  /** Устанавливает действие которое несет сообщение 
   * @param newAction действие
   */
  public void setAction(String newAction) { 
    action = newAction; 
  }
  /** Возвращает адресата сообщения 
   * @return адресат
   */
  public String getDestination() { return destination; }
  /** Устанавливает адресата сообщения 
   * @param newDest адресат
   */
  public void setDestination(String newDest) { destination = newDest; }
  /** Возвращает автора сообщения 
   * @return автор
   */
  public String getOrigin() {
    return origin;
  }
  /** Изменить отправителя на лету 
   * @param newOrigin новое значение поля отправителя
   */
  public void setOrigin(String newOrigin) {
    this.origin = newOrigin;
  }
  /** Возвращает идентификатор сообщения на которое производится ответ 
   * @return идентификатор
   */
  public int getReplyTo() {
    return inReplyTo;
  }
  /** Устанавливает идентификатор сообщения на которое производится ответ 
   * @param newId идентификатор
   */
  public void setReplyTo(int newId) {
    inReplyTo = newId;
  }
  /** Возвращает количество полей в сообщении 
   * @return кол-во полей
   */
  public int getFieldsCount() { 
    return fields.size();
  }
  /** Возвращает уникальный идентификатор сообщения 
   * @return идентификатор
   */
  public int getId() { 
    return myId; 
  }
  /** Устанавливает уникальный идентификатор сообщения 
   * @param newId идентификатор
   */
  public void setId(int newId) { 
    myId = newId; 
  }
  /** Представление сообщения в виде текстовой строки 
   * @return строчное представление сообщения
   */
  public String toString() {
    return "stdmessage id=" + myId + " replyto=" + inReplyTo + " action=" + action + ", destination=" + destination + ", origin=" + origin + ", fields.size()=" + fields.size();
  }
}
