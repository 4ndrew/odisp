package com.novel.odisp;

import com.novel.odisp.common.Message;
import java.util.List;
import java.util.ArrayList;
import java.io.Serializable;

/** ���������� ������������ ��������� ��� ������������ ���������� ODISP
 * @author �������� �. ��������
 * @author (C) 2003, ��� "�����-��"
 * @version $Id: StandartMessage.java,v 1.6 2003/11/15 18:48:57 valeks Exp $
 */
public class StandartMessage implements Message, Serializable {
  /** ���������� ���������� ������� ��������� */
  private static int id = 0;
  /** ���������� ������ ��������� � ������� */
  private int myId;
  /** ������ ����� ��������� */
  private List fields = new ArrayList();
  /** �������� */
  private String action;
  /** ����� ���������� */
  private String destination;
  /** ����������� */
  private String origin;
  /** ������������� ��������� �� ������� ������������ ����� */
  private int inReplyTo;
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
  /** ���������� �������� ��������� 
   * @return �������
   */
  public String getDestination() { return destination; }
  /** ���������� ������ ��������� 
   * @return �����
   */
  public String getOrigin() {
    return origin;
  }
  /** ���������� ������������� ��������� �� ������� ������������ ����� 
   * @return �������������
   */
  public int getReplyTo() {
    return inReplyTo;
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
  /** ������������� ��������� � ���� ��������� ������ 
   * @return �������� ������������� ���������
   */
  public String toString() {
    return "stdmessage id=" + myId + " replyto=" + inReplyTo + " action=" + action + ", destination=" + destination + ", origin=" + origin + ", fields.size()=" + fields.size();
  }
}
