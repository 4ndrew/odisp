package org.valabs.stdmsg;

import org.valabs.odisp.common.Message;
import org.doomdark.uuid.UUID;
import java.util.List;

/** ������ ������ ��������� ��������.
 * @author (C) 2004 <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @version $Id: ODGetProvidingReplyMessage.java,v 1.10 2005/03/03 08:47:49 valeks Exp $
 */
public final class ODGetProvidingReplyMessage {
  /** ��������� ������������� ���������. */
  public static final String NAME = "od_get_providing_reply";
  /** ������ ��� ���� ProvidingList. */
  private static String idxPROVIDINGLIST = "providinglist";

  /** ������ �� �������� �������. */
  private ODGetProvidingReplyMessage() { /* Single-model. �� ����������� ��������� ������. */ }
  /** �������� ��������� �� ������������.
   * @param msg ���������
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
  /** ������������� �������� ������� ���������.
   * @param msg ���������.
   * @param destination ����� ����������.
   * @param origin ����� �����������.
   * @param replyTo ������������� ���������, �� ������� ��� �������� �������.
   * @return ������ �� ������������������ ���������
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

  /** ���������� ProvidingList.
   * ������ ��������������� ��������.
   * @param msg ��������� ��� ������� ������������ ��������.
   * @param newValue ����� �������� ��� ����.
   * @return ������ �� ���������
   */

  public static Message setProvidingList(final Message msg, final List newValue) {
    msg.addField(idxPROVIDINGLIST, newValue);
    checkMessage(msg);
    return msg;
  }
  /** �������� ProvidingList.
   *������ ��������������� ��������.
   * @param msg ��������� ��� ������� ������������ ��������.
   * @return �������� ����
   */
  public static List getProvidingList(final Message msg) {
    return (List)msg.getField(idxPROVIDINGLIST);
  }

  /** �������� �� ��������� ���������� ����� ����.
   * @param msg ���������.
   * @return true - ���� ��������, false - �����.
   */
  public static boolean equals(final Message msg) {
    return msg.getAction().equals(NAME);
  }
  /** ����������� ����� �� ������ ��������� � ������.
  * @param dest ����������.
  * @param src ��������.
  */
  public static void copyFrom(final Message dest, final Message src) {
    setProvidingList(dest, getProvidingList(src));
  }
  /** ������������� ����������� hash-���� ���������.
   * ������ ����� 0.
   * @return hash-��� ���������.
   */
  public int hashCode() {
    return 0;
  }
  /** �������� ������ ���������� ���� ����� ��������� �����.
   * @return ������ �� ���������
   * @param providinglist ������ ��������������� ��������.
  */
  public static Message initAll(final Message m,
                                final List providinglist) {
    setProvidingList(m, providinglist);
    return m;
  }

} // ODGetProvidingReplyMessage 
