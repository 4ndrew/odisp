package com.novel.stdobj.simpleconfig;

import com.novel.odisp.common.Resource;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.FileInputStream;
import java.util.logging.Logger;
import java.util.Properties;

/** ������ ODISP ����������� ������ � ���������������� ������  ������� [���]=[��������].
* @author �������� �. ��������
* @author (C) 2003, ��� "�����-��"
* @version $Id: SimpleConfig.java,v 1.10 2004/02/12 17:50:23 valeks Exp $
*/
public class SimpleConfig extends Properties implements Resource {
  /** ��� ����������������� ����� ��-���������.*/
  public static final String DEFAULT_CONFIG = "simpleconfig.dat";
  /** ������. */
  private static Logger logger = Logger.getLogger("simpleconfig");
  /** ������ ����������������� �����.
   * @param newCfgName ��� ����� ������������
   * @deprecated ���������� ������������ load(new FileInputStream(String))
   */
  public final void readConfig(final String newCfgName) {
    try {
      load(new FileInputStream(newCfgName));
    } catch (FileNotFoundException e) {
      logger.warning("file not found: " + newCfgName);
    } catch (IOException e) {
      logger.warning("unable to read config file " + newCfgName);
    } catch (IllegalArgumentException e) {
      logger.warning("unparsable Unicode sequence appeared in " + newCfgName);
    }
  }
  /** ���������� �������� ���������� �� ����������������� �����.
   * @param name ��� ���������
   * @return �������� ��������� ��� '-undef-' ���� �������� �� ���������
   * @deprecated ���������� ������������ getProperty(String)
   */
  public final String getValue(final String name) {
    String result = getProperty(name);
    if (result == null) {
      return "-undef-";
    }
    return result;
  }
  /** ���������� �������� ���������� �� �����������������.
   * ����� � ������ �������� �� ���������.
   * @param name ��� ���������
   * @param defaultValue �������� �� ���������
   * @deprecated ���������� ������������ getProperty(String, String)
   * @return �������� ��������� ��� defaultValue ���� �������� �� ���������
   */
  public final String getValue(final String name,
			       final String defaultValue) {
    return getProperty(name, defaultValue);
  }
  /** �����.
   * @param type ��� ������
   * @return ��� ������
   */
  public final int cleanUp(final int type) {
    return 0;
  }

  /** ������� ������������ ���-�� �����������.
   * @return ������������ ����� �����������
   */
  public final int getMaxReferenceCount() {
    return 0;
  }
}
