package com.novel.odisp;

import com.novel.odisp.common.Message;
import java.util.List;
import java.util.ArrayList;
import java.io.Serializable;

/** Реализация стандартного сообщения для стандартного диспетчера ODISP
 * @author Валентин А. Алексеев
 * @author (C) 2003, НПП "Новел-ИЛ"
 * @version $Id: StandartMessage.java,v 1.6 2003/11/15 18:48:57 valeks Exp $
 */
public class StandartMessage implements Message, Serializable {
  /** Внутренний уникальный счетчик сообщения */
  private static int id = 0;
  /** Уникальный индекс сообщения в системе */
  private int myId;
  /** Список полей сообщения */
  private List fields = new ArrayList();
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
  /** Возвращает адресата сообщения 
   * @return адресат
   */
  public String getDestination() { return destination; }
  /** Возвращает автора сообщения 
   * @return автор
   */
  public String getOrigin() {
    return origin;
  }
  /** Возвращает идентификатор сообщения на которое производится ответ 
   * @return идентификатор
   */
  public int getReplyTo() {
    return inReplyTo;
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
  /** Представление сообщения в виде текстовой строки 
   * @return строчное представление сообщения
   */
  public String toString() {
    return "stdmessage id=" + myId + " replyto=" + inReplyTo + " action=" + action + ", destination=" + destination + ", origin=" + origin + ", fields.size()=" + fields.size();
  }
}
