package org.valabs.stdmsg;

import org.valabs.odisp.common.Message;
import org.doomdark.uuid.UUID;
import java.util.List;

/** Запрос списка доступных сервисов.
 * @author (C) 2004 <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @version $Id: ODGetProvidingReplyMessage.java,v 1.10 2005/03/03 08:47:49 valeks Exp $
 */
public final class ODGetProvidingReplyMessage {
  /** Строковое представление сообщения. */
  public static final String NAME = "od_get_providing_reply";
  /** Индекс для поля ProvidingList. */
  private static String idxPROVIDINGLIST = "providinglist";

  /** Запрет на создание объекта. */
  private ODGetProvidingReplyMessage() { /* Single-model. Не позволяется создавать объект. */ }
  /** Проверка сообщения на корректность.
   * @param msg Сообщение
   */
  private static void checkMessage(final Message msg) {
    try {
      assert getProvidingList(msg) != null : "Message has invalid field 'ProvidingList'";
    } catch (AssertionError e) {
      System.err.println("Message assertion :" + e.toString());
      e.printStackTrace();
    }
    msg.setCorrect(
      getProvidingList(msg) != null
    );
  }
  /** Инициализация основных свойств сообщения.
   * @param msg Сообщение.
   * @param destination Точка назначения.
   * @param origin Точка отправления.
   * @param replyTo Идентификатор сообщения, на которое это является ответом.
   * @return ссылка на инициализированное сообщение
   */
  public static Message setup(final Message msg
,
                              final String destination,
                              final String origin,
                              final UUID replyTo) {
    msg.setAction(NAME);
    msg.setDestination(destination);
    msg.setOrigin(origin);
    msg.setReplyTo(replyTo);
    msg.setRoutable(false);
    checkMessage(msg);
    return msg;
  }

  /** Установить ProvidingList.
   * Список предоставляемых сервисов.
   * @param msg Сообщение над которым производится действие.
   * @param newValue Новое значение для поля.
   * @return ссылка на сообщение
   */

  public static Message setProvidingList(final Message msg, final List newValue) {
    msg.addField(idxPROVIDINGLIST, newValue);
    checkMessage(msg);
    return msg;
  }
  /** Получить ProvidingList.
   *Список предоставляемых сервисов.
   * @param msg Сообщение над которым производится действие.
   * @return значение поля
   */
  public static List getProvidingList(final Message msg) {
    return (List)msg.getField(idxPROVIDINGLIST);
  }

  /** Является ли экземпляр сообщением этого типа.
   * @param msg Сообщение.
   * @return true - если является, false - иначе.
   */
  public static boolean equals(final Message msg) {
    return msg.getAction().equals(NAME);
  }
  /** Копирование полей из одного сообщения в другое.
  * @param dest Получатель.
  * @param src Источник.
  */
  public static void copyFrom(final Message dest, final Message src) {
    setProvidingList(dest, getProvidingList(src));
  }
  /** Генерирование уникального hash-кода сообщения.
   * Всегда равен 0.
   * @return hash-код сообщения.
   */
  public int hashCode() {
    return 0;
  }
  /** Короткий способ заполнения всех полей сообщения сразу.
   * @return ссылку на сообщение
   * @param providinglist Список предоставляемых сервисов.
  */
  public static Message initAll(final Message m,
                                final List providinglist) {
    setProvidingList(m, providinglist);
    return m;
  }

} // ODGetProvidingReplyMessage 
