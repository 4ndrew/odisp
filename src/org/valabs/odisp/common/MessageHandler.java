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

/** ��������� ����������� ��������� ���� ODISP �������.
 * @author (C) <a href="mailto:valeks@novel-il.ru">�������� �. ��������</a>
 * @version $Id: MessageHandler.java,v 1.5 2004/08/23 07:42:37 valeks Exp $
 */
public interface MessageHandler {
  /** ����� ���������� ��� ��������� ������� ������� ���������.
   * ������������������� ����.
   * @param msg ���������� ���������
   */
  void messageReceived(Message msg);
}
