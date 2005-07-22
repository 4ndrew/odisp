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
