package com.novel.odisp.common;

import java.util.List;

/** ��������� ��������� ���� ODISP �������.
 * @author �������� �. ��������
 * @author (C) 2003, ��� "�����-��"
 * @version $Id: Message.java,v 1.5 2004/02/24 00:35:14 valeks Exp $
 */
public interface Message {
  /** ���������� ������������ ������� � ���� ���������.
   * @param field ������ ��� ����������
   */
  void addField(Object field);

  /** ������� ������� �� �������.
   * @param field ������ ������� � ���� ���������
   * @return Object ���������� ����
   */
  Object getField(int field);

  /** ���������� �������� � ���� ���������.
   * @return ���-�� ��������
   */
  int getFieldsCount();

  /** ���������� ������������� ���������.
   * @return �������������
   */
  int getId();

  /** ���������� ���������� ������������� ���������.
   * @param id �������������
   */
  void setId(int id);

  /** ������������� ��������� �� ������� ���� �����.
   * @return �������������
   */
  int getReplyTo();

  /** �������� ������� ����� ���������.
   * @return ��������
   */
  String getAction();

  /** ���������� ���������.
   * @return ����������
   */
  String getDestination();

  /** ���������� ���������� ���������.
   * @param nd ����� ��������
   */
  void setDestination(String nd);

  /** ���������� ������ ������.
   * @param nrpt ������ ��������� �� ������� ������������ �����
   */
  void setReplyTo(int nrpt);

  /** ���������� ��������.
   * @param na ����� ��������
   */
  void setAction(String na);

  /** ������� ��� �����������.
   * @return ��� �����������
   */
  String getOrigin();

  /** �������� ����������.
   * @param newOrigin ����� ��� �����������
   */
  void setOrigin(String newOrigin);

  /** �������� ������������ ���������.
   * @return ���� ������������
   */
  boolean isCorrect();

  /** ������� �������� ��������� ������� ������ ��������� 5 �������.
   * @param willStackTrace
   * @return ���������������� ���������
   */
  String toString(boolean willStackTrace);
  /** ������ �� ����� ������ �����.
   * @return ������ �����
   */
  List getFields();

  /** �������� �� ���������������� ��������� ����� ������������ (������������).
   * @return ���� �������������
   */
  boolean isRoutable();

  /** �������� �� ���������������� ��������� ����� ������������ (������������)
   * @return ���� �������������
   */
  void setRoutable(boolean newRoutable);
}
