package org.valabs.odisp.common;

import java.util.List;
import java.util.Map;


/** ��������� ��������� ������������ ���������� ODISP.
 * @author (C) 2004 <a href="mailto:valeks@valabs.spb.ru">�������� �������� �.</a>
 * @version $Id: ConfigurationManager.java,v 1.2 2005/01/27 14:15:42 valeks Exp $
 */
public interface ConfigurationManager {
  /** ������� ���������� ���������� ������.
   */
  public void setCommandLineArguments(List args);
  /** ������������ �� �������� ������������ ������ ������ �������� � �������� ��� ��������.
   */
  public boolean supportComponentListing();
  
  /** �������� ������ �������� ��� ��������.
   * ������ ������� �� ������� ���� ComponentConfiguration. 
   */
  public List getResourceList();

  /** �������� ������ �������� ��� ��������.
   * ������ ������� �� ������� ���� ComponentConfiguration. 
   */
  public List getObjectList();

  /** ������������ �� �������� ������������ ������ ����������. */
  public boolean supportParameterFetching();
  
  /** ��������� �������� ��������� � ��������� ������.
   * @param domain ����� ���������
   * @param paramName �������� ���������
   */
  public String getParameter(String domain, String paramName);
  
  /** ������ � ������������ ���������� ����������. */
  public class ComponentConfiguration {
    /** ��� ������. */
    private String className;
    /** ������������. */
    private Map configuration;
    /** ������������� ����� ������.
     * @param _className ��� ������
     * @param _configuration ������������
     */
    public ComponentConfiguration(String _className, Map _configuration) {
      className = _className;
      configuration = _configuration;
    }
    /** ������� �����Σ���� ��� ������. */
    public String getClassName() {
      return className;
    }
    
    /** ������� �����Σ���� ������������. */
    public Map getConfiguration() {
      return configuration;
    }
  }
}
