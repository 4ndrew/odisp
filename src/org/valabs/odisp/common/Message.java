package org.valabs.odisp.common;

import java.util.List;
import java.util.Map;

import org.doomdark.uuid.UUID;

/** ��������� ��������� ���� ODISP �������.
 * @author (C) 2004 <a href="mailto:valeks@novel-il.ru">�������� �. ��������</a>
 * @version $Id: Message.java,v 1.18 2004/11/05 14:11:29 valeks Exp $
 */
public interface Message {

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
  UUID getId();

  /** ���������� ���������� ������������� ���������.
   * @param id �������������
   */
  void setId(UUID id);

  /** ������������� ��������� �� ������� ���� �����.
   * @return �������������
   */
  UUID getReplyTo();

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
  void setReplyTo(UUID nrpt);

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

  /** ��������� ����� ������������ ���������.
   * @param newCorrectFlag ����� ��������
   */
  void setCorrect(boolean newCorrectFlag);

  /** ������� �������� ��������� ������� ������ ��������� 5 �������.
   * @param willStackTrace ����� �� �������������� ����� ������ ���� ���������
   * @return ���������������� ���������
   */
  String toString(boolean willStackTrace);

  /** �������� �� ���������������� ��������� ����� ������������ (������������).
   * @return ���� �������������
   */
  boolean isRoutable();

  /** �������� �� ���������������� ��������� ����� ������������ (������������). 
   * @param newRoutable ����� �������� ����� �������������
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

  /** ������ � �������������.
   * @return ������ ������������
   */
  List getEnvelope();

  /** ���������� ������������.
   * @param envelopeMessage ������������
   */
  void addToEnvelope(Message envelopeMessage);

  /** �������� �� OOB.
   * @return true � ������ ���� ��������� � ��������� �����������.
   */
  boolean isOOB();
  
  /** ��������� ����� OOB.
   * @param newValue ����� �������� OOB �����.
   */
  void setOOB(boolean newValue);
}
