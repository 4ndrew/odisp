package com.novel.odisp.common;

import java.util.Map;

/** ��������� ��������� ���� ODISP �������.
 * @author �������� �. ��������
 * @author (C) 2003, ��� "�����-��"
 * @version $Id: Message.java,v 1.10 2004/05/21 11:15:46 valeks Exp $
 */
public interface Message extends Cloneable {

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
