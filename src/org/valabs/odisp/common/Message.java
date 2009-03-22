/* ODISP -- Message Oriented Middleware
 * Copyright (C) 2003-2005 Valentin A. Alekseev
 * Copyright (C) 2003-2005 Andrew A. Porohin 
 * 
 * ODISP is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, version 2.1 of the License.
 * 
 * ODISP is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with ODISP.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.valabs.odisp.common;

import java.util.List;
import java.util.Map;

import org.doomdark.uuid.UUID;

/** ��������� ��������� ���� ODISP �������.
 * @author (C) 2004 <a href="mailto:valeks@novel-il.ru">�������� �. ��������</a>
 * @version $Id: Message.java,v 1.20 2005/02/27 12:37:29 valeks Exp $
 */
public interface Message {

  Message cloneMessage();
  String RECIPIENT_ALL = ".*";
  String RECIPIENT_CATCHALL = "catch-all-service";
  
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
   * @param newId �������������
   */
  void setId(UUID newId);

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
   * @param newDest ����� ��������
   */
  void setDestination(String newDest);

  /** ���������� ������ ������.
   * @param nrpt ������ ��������� �� ������� ������������ �����
   */
  void setReplyTo(UUID nrpt);

  /** ���������� ��������.
   * @param newAction ����� ��������
   */
  void setAction(String newAction);

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
