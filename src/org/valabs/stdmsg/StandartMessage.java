package com.novel.stdmsg;

import com.novel.odisp.common.Message;
import java.util.List;
import java.util.ArrayList;
import java.io.Serializable;

/** ���������� ������������ ��������� ��� ������������ ���������� ODISP.
 * @author �������� �. ��������
 * @author (C) 2003, ��� "�����-��"
 * @version $Id: StandartMessage.java,v 1.8 2004/02/25 08:36:58 dron Exp $
 */
public class StandartMessage implements Message, Serializable {
  /** ���� �������������. */
  private boolean routable = true;
  /** ���������� ���������� ������� ���������. */
  private static int id = 0;
  /** ���������� ������ ��������� � �������. */
  private int myId;
  /** ������ ����� ���������. */
  private List fields = new ArrayList();
  /** ��������. */
  private String action;
  /** ����� ����������. */
  private String destination;
  /** �����������. */
  private String origin;
  /** ������������� ��������� �� ������� ������������ �����. */
  private int inReplyTo;
  /** ���� ���������� ��������. */
  private boolean ce = false;
  /** ���������� ������������ ���������.
   * @param newAction �������� ������� ����� ���������
   * @param newDestination ������� ���������
   * @param newOrigin ����������� ���������
   * @param newInReplyTo ������������� ��������� �� ������� ������������ �����
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

  /** ����������� ��-���������. */
  public StandartMessage() {
    myId = id++;
  }

  /** ���������� ����������� ��������� ��������� ������������� ���� �������.
   * @param msg ��������� � �������� ��������� �����
   */
  public StandartMessage(final Message msg) {
    action = msg.getAction();
    destination = msg.getDestination();
    inReplyTo = msg.getReplyTo();
    origin = msg.getOrigin();
    fields = new ArrayList(msg.getFields());
    routable = msg.isRoutable();
    myId = id++;
    setCE(msg.isCorrect());
  }

  /** ���������� ������������� ������� � ���� ���������.
   * @param field ������ ������� ����� �������� ���������
   */
  public final void addField(final Object field) {
    fields.add(field);
  }

  /** ������� ������������ � ���� ��������� ������� �� �������.
   * @param field ������ �������
   * @return ���� ���������
   */
  public final Object getField(final int field) {
    return fields.get(field);
  }

  /** ���������� �������� ������� ����� ���������.
   * @return ��������
   */
  public final String getAction() {
    return action;
  }

  /** ������������� �������� ������� ����� ���������.
   * @param newAction ��������
   */
  public final void setAction(final String newAction) {
    action = newAction;
  }

  /** ���������� �������� ���������.
   * @return �������
   */
  public final String getDestination() { return destination; }

  /** ������������� �������� ���������.
   * @param newDest �������
   */
  public final void setDestination(final String newDest) {
    destination = newDest;
  }

  /** ���������� ������ ���������.
   * @return �����
   */
  public final String getOrigin() {
    return origin;
  }

  /** �������� ����������� �� ����.
   * @param newOrigin ����� �������� ���� �����������
   */
  public final void setOrigin(final String newOrigin) {
    this.origin = newOrigin;
  }

  /** ���������� ������������� ��������� �� ������� ������������ �����.
   * @return �������������
   */
  public final int getReplyTo() {
    return inReplyTo;
  }

  /** ������������� ������������� ��������� �� ������� ������������ �����.
   * @param newId �������������
   */
  public final void setReplyTo(final int newId) {
    inReplyTo = newId;
  }

  /** ���������� ���������� ����� � ���������.
   * @return ���-�� �����
   */
  public final int getFieldsCount() {
    return fields.size();
  }

  /** ���������� ���������� ������������� ���������.
   * @return �������������
   */
  public final int getId() {
    return myId;
  }

  /** ������������� ���������� ������������� ���������.
   * @param newId �������������
   */
  public final void setId(final int newId) {
    myId = newId;
  }

  /** ������������� ��������� � ���� ��������� ������.
   * @return �������� ������������� ���������
   */
  public final String toString() {
    return "stdmessage id=" + myId + " replyto=" + inReplyTo
      + " action=" + action + ", destination=" + destination
      + ", origin=" + origin + ", fields.size()=" + fields.size() + (isRoutable() ? " routable" :"");
  }

  /** ������������� ��������� � ���� ��������� ������ � ������ ���� ��������� ������.
   * @return �������� ������������� ���������
   */
  public final String toString(boolean doStackTrace) {
    if (!doStackTrace) {
      return toString();
    }
    // ��������� ��� ��� ����, ��� �� �������� ������ ������� �������
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

  /** �������� ������������ ���������.
   * @return ���� ������������
   */
  public boolean isCorrect() {
    ce = true;
    return true;
  }

  /** ������ �� ����� ������ �����.
   * @return ������ �����
   */
  public final List getFields() {
    return fields;
  }

  /** ���������� ���� ������������.
   * @param newCE ����� ��������
   */
  public final void setCE(final boolean newCE) {
    ce = newCE;
  }

  /** ��������� ���� ������������.
   * @return �������� �����
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
}
