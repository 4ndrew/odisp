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


/** ��������� ��������� ������������ ���������� ODISP.
 * @author (C) 2004 <a href="mailto:valeks@valabs.spb.ru">�������� �������� �.</a>
 * @version $Id: ConfigurationManager.java,v 1.4 2005/07/22 15:32:02 valeks Exp $
 */
public interface ConfigurationManager {
  /** ������� ���������� ���������� ������.
   * @param args ������ ����������
   */
  public void setCommandLineArguments(List args);
  /** ������������ �� �������� ������������ ������ ������ �������� � �������� ��� ��������.
   * @return ������������ �� �������� ������� ���������
   */
  public boolean supportComponentListing();
  
  /** �������� ������ �������� ��� ��������.
   * @return ������ ������� �� ������� ���� ComponentConfiguration.  
   */
  public List getResourceList();

  /** �������� ������ �������� ��� ��������.
   * @return ������ ������� �� ������� ���� ComponentConfiguration. 
   */
  public List getObjectList();

  /** ������������ �� �������� ������������ ������ ����������.
   * @return ������������ �� �������� ������ ���������� ������������
   */
  public boolean supportParameterFetching();
  
  /** ��������� �������� ��������� � ��������� ������.
   * @param domain ����� ���������
   * @param paramName �������� ���������
   * @return �������� ���������
   */
  public String getParameter(String domain, String paramName);
  
  /** ������ � ������������ ���������� ����������. */
  class ComponentConfiguration {
    /** ��� ������. */
    private final String className;
    /** ������������. */
    private final Map configuration;
    /** ������������� ����� ������.
     * @param _className ��� ������
     * @param _configuration ������������
     */
    public ComponentConfiguration(String _className, Map _configuration) {
      className = _className;
      configuration = _configuration;
    }
    /** ������� �����Σ���� ��� ������.
     * @return �������� ������
     */
    public String getClassName() {
      return className;
    }
    
    /** ������� �����Σ���� ������������. 
     * @return ����� ���������� ����������
     */
    public Map getConfiguration() {
      return configuration;
    }
  }
}
