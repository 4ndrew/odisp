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

import org.doomdark.uuid.UUID;

/** ����������� ��������� ����������, ������� ������ �������������
 * �����, ������� ��������� � �������� ODISP ����������.
 * @author (C) 2003 <a href="mailto:valeks@novel-il.ru">�������� �. ��������</a>
 * @version $Id: Dispatcher.java,v 1.18 2005/07/22 15:32:02 valeks Exp $
 */
public interface Dispatcher {
  /** ������� ���������� ���������.
   * @param message ���������
   */
  void send(Message message);
  /** ������� ������ ���������.
   * @param messageList ������ ���������
   */
  void send(Message[] messageList);
  /** ������� ������ ���������.
   * @param messageList ������ ���������
   */
  void send(List messageList);


  /** ��������� ������ ��������� � ��������� �����������.
   * @param action ��������
   * @param destination ����������
   * @param origin �����������
   * @param inReplyTo ��������� �� ������� ������������ �����
   * @return ���������
   */
  Message getNewMessage(String action, String destination,
			String origin, UUID inReplyTo);
  /** ������� ������ ���������.
   * @return ������ �� ���������
   */
  Message getNewMessage();

  /** ������ � ��������� ��������. 
   * @return ������ �� �������� ��������
   */
  ObjectManager getObjectManager();
  /** ������ � ��������� ��������. 
   * @return ������ �� �������� ��������
   */
  ResourceManager getResourceManager();
  /** ������ � ��������� ������������.
   * @return ������ �� �������� ������������
   */
  SecurityManager getSecurityManager();
  /** ������ � ��������� ������������.
   * @return ������ �� �������� ������������
   */
  ConfigurationManager getConfigurationManager();
  /** ������ � ����������� ����������. 
   * @return ������ �� ���������� ����������
   */
  ExceptionHandler getExceptionHandler();
  /** ���������� ��������� ������������. 
   * @param additionalSecurityManager ����� �������� ������������
   */
  void addSecurityManager(SecurityManager additionalSecurityManager);
  /** ��������� ������ ����������� ����������.
   * @param exception ����� ���������� ����������
   */
  void addExceptionHandler(ExceptionHandler exception);
  /** ���������� ��������� ������������. 
   * @param cman ����� �������� ������������
   */
  void addConfigurationManager(ConfigurationManager cman);
}
