package com.novel.stdmsg;

import com.novel.odisp.common.Message;
import java.util.List;
import java.util.ArrayList;
import java.io.Serializable;

/** ���������� ������������ ��������� ��� ������������ ���������� ODISP
 * @author �������� �. ��������
 * @author (C) 2003, ��� "�����-��"
 * @version $Id: StandartMessage.java,v 1.2 2003/12/15 14:01:45 valeks Exp $
 */
public class StandartMessage implements Message, Serializable {
  /** ���������� ���������� ������� ��������� */
  private static int id = 0;
  /** ���������� ������ ��������� � ������� */
  private int myId;
  /** ������ ����� ��������� */
  protected List fields = new ArrayList();
  /** �������� */
  private String action;
  /** ����� ���������� */
  private String destination;
  /** ����������� */
  private String origin;
  /** ������������� ��������� �� ������� ������������ ����� */
  private int inReplyTo;
  /** ���� ���������� �������� */
  protected boolean ce = false;
  /** ���������� ������������ ���������
   * @param action �������� ������� ����� ���������
   * @param destination ������� ���������
   * @param origin ����������� ���������
   * @param inReplyTo ������������� ��������� �� ������� ������������ �����
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

  /** ���������� ������������� ������� � ���� ���������
   * @param field ������ ������� ����� �������� ���������
   */
  public void addField(Object field) {
    fields.add(field);
  }
  /** ������� ������������ � ���� ��������� ������� �� �������
   * @param field ������ �������
   * @return ���� ���������
   */
  public Object getField(int field) {
    return fields.get(field);
  }
  /** ���������� �������� ������� ����� ��������� 
   * @return ��������
   */
  public String getAction() { 
    return action; 
  }
  /** ������������� �������� ������� ����� ��������� 
   * @param newAction ��������
   */
  public void setAction(String newAction) { 
    action = newAction; 
  }
  /** ���������� �������� ��������� 
   * @return �������
   */
  public String getDestination() { return destination; }
  /** ������������� �������� ��������� 
   * @param newDest �������
   */
  public void setDestination(String newDest) { destination = newDest; }
  /** ���������� ������ ��������� 
   * @return �����
   */
  public String getOrigin() {
    return origin;
  }
  /** �������� ����������� �� ���� 
   * @param newOrigin ����� �������� ���� �����������
   */
  public void setOrigin(String newOrigin) {
    this.origin = newOrigin;
  }
  /** ���������� ������������� ��������� �� ������� ������������ ����� 
   * @return �������������
   */
  public int getReplyTo() {
    return inReplyTo;
  }
  /** ������������� ������������� ��������� �� ������� ������������ ����� 
   * @param newId �������������
   */
  public void setReplyTo(int newId) {
    inReplyTo = newId;
  }
  /** ���������� ���������� ����� � ��������� 
   * @return ���-�� �����
   */
  public int getFieldsCount() { 
    return fields.size();
  }
  /** ���������� ���������� ������������� ��������� 
   * @return �������������
   */
  public int getId() { 
    return myId; 
  }
  /** ������������� ���������� ������������� ��������� 
   * @param newId �������������
   */
  public void setId(int newId) { 
    myId = newId; 
  }
  /** ������������� ��������� � ���� ��������� ������ 
   * @return �������� ������������� ���������
   */
  public String toString() {
    return "stdmessage id=" + myId + " replyto=" + inReplyTo + " action=" + action + ", destination=" + destination + ", origin=" + origin + ", fields.size()=" + fields.size();
  }

  public boolean isCorrect() {
    ce = true;
    return true;
  }
}
