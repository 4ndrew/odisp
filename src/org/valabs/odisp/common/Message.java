package com.novel.odisp.common;

/** ��������� ��������� ���� ODISP �������
 * @author �������� �. ��������
 * @author (C) 2003, ��� "�����-��"
 * @version $Id: Message.java,v 1.2 2003/12/15 13:02:35 valeks Exp $
 */
public interface Message {
  /** ���������� ������������ ������� � ���� ���������
   * @param field ������ ��� ����������
   */
  void addField(Object field);
  /**������� ������� �� �������
   * @param field ������ ������� � ���� ���������
   * @return Object ���������� ����
   */
  Object getField(int field);
  /** ���������� �������� � ���� ��������� 
   * @return ���-�� ��������
   */
  int getFieldsCount();
  /** ���������� ������������� ��������� 
   * @return �������������
   */
  int getId();
  /** ���������� ���������� ������������� ��������� 
   * @param id �������������
   */
  void setId(int id);
  /** ������������� ��������� �� ������� ���� ����� 
   * @return �������������
   */
  int getReplyTo();
  /** �������� ������� ����� ��������� 
   * @return ��������
   */
  String getAction();
  /** ���������� ���������
   * @return ����������
   */
  String getDestination();
  void setDestination(String nd);
  void setReplyTo(int nrpt);
  void setAction(String na);
  /** ������� ��� ����������� */
  String getOrigin();
  /** �������� ���������� */
  void setOrigin(String newOrigin);

  /** �������� ������������ ���������*/
  boolean isCorrect();
}
