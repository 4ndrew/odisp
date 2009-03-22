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

/**
 * ��������� ��������� �������� ODISP.
 * @author (C) 2004 <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev
 *         </a>
 * @version $Id: ResourceManager.java,v 1.13 2005/07/22 15:32:02 valeks Exp $
 */

public interface ResourceManager {
  /**
   * �������� �������.
   * @param className ��� ������
   * @param mult ���������
   * @param params ������ ���������� ��������
   */
  void loadResource(String className, int mult, Map params);

  /**
   * �������� ������ ��������.
   * @return ����� ��������
   */
  Map getResources();

  /**
   * ������� ���������� �� ��������.
   * @return ������ �����.
   */
  List statRequest();

  /**
   * ����������� ������� ������� ������� ��������. <b>��������! </b> ��� ���,
   * ������, ������� ������������ � ��������� ������������ ��������� (�,
   * ��������������, � ����� Sender), ����������� ����� ������ ��������������
   * ����������� ��������� ����� �������� �������� ������ ���������� ������� �
   * ������ ������������� ������������ ��������. ��� ����, ��� �� ����������
   * �������� ���� �������, � ��� �� �� �������� ������� ����� Sender,
   * ���������� ���� ������������ ������������� �������, ���� �������������
   * ������ ������� � ��������� ����, �������� ���: 
   * <pre>
   * ...
   * SomeResource res = null;
   * new Thread() {
   *   public void run() {
   * 		setBlockedState(true);
   * 		res = (SomeResource) dispatcher.resourceAcquire(SomeResource.class.getName());
   * 		setBlockedState(false);
   * 	}
   * }.start();
   * ...
   * </pre>
   * ����� ������� ���������� ������ ������� ������������ � ��������� ������ ���
   * ��������������� ��������� �������.
   * @param className ��� �������
   * @return ������ �� ������
   */
  Resource resourceAcquire(String className);

  /**
   * ������������� ������� ������� ������� ��������.
   * @param className ��� �������
   * @return ������ �� ������ ��� null ���� ������ ������������
   */
  Resource resourceTryAcquire(String className);

  /**
   * ������������� �������.
   * @param className ��� �������
   * @param resource ������ �� ������
   */
  void releaseResource(String className, Resource resource);
} // ResourceManager
