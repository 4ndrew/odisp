package com.novel.odisp.common;

import java.util.List;
import java.util.Map;

/** ��������� ��������� ���� ODISP �������.
 * @author �������� �. ��������
 * @author (C) 2003, ��� "�����-��"
 * @version $Id: Message.java,v 1.7 2004/03/26 21:53:37 valeks Exp $
 */
public interface Message {
  /** ���������� ������������ ������� � ���� ���������.
   * @param field ������ ��� ����������
   * @deprecated ���������� ������������ #addField(String, Object)
   */
  void addField(Object field);

  /** ������� ������� �� �������.
   * @param field ������ ������� � ���� ���������
   * @deprecated ���������� ������������ #getField(String)
   * @return ���������� ����
   */
  Object getField(int field);

  /** ����� ���� �� �����.
   * @param name ��� ����
   * @return ���������� ����
   */
  Object getField(String name);

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
   * @deprecated ���������� ������������ #getContents()
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
  
  /** ������ � ����������� ��������� ��������.
   * @return ������� �����
   */
  Map getContents();

  /** ���������� ���� � �������� ������.
   * @param name ��� ����
   * @param value �������� ����
   */
  void addField(String name, Object value);
}
