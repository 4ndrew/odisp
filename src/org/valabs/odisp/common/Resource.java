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

import java.util.Map;

/**
 * ��������� ���������� ������� ODISP ����.
 * 
 * @author (C) 2003, <a href="mailto:valeks@novel-il.ru">�������� �. ��������</a>
 * @version $Id: Resource.java,v 1.8 2005/07/22 13:06:56 dron Exp $
 */
public interface Resource {
  /**
   * ������������ ���������� ������ ������������ �������������� ��������.
   * 
   * @return ������������ ���-�� ������.
   */
  int getMaxReferenceCount();

  /**
   * ���������� ��� ������������� �������� �������.
   * 
   * @param type ������� ������.
   * @return ��� ��������.
   */
  int cleanUp(int type);
  
  /**
   * ���������� ������������.
   * 
   * @param config ������������.
   */
  void setConfiguration(Map config);
}
