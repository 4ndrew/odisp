package com.novel.stdobj.simpleconfig;

import com.novel.odisp.common.Resource;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.logging.Logger;

/** ������ ODISP ����������� ������ � ���������������� ������ ������� [���]=[��������].
* @author �������� �. ��������
* @author (C) 2003, ��� "�����-��"
* @version $Id: SimpleConfig.java,v 1.9 2004/01/16 14:31:57 valeks Exp $
*/
public class SimpleConfig implements Resource {
  /** ��� ����������������� �����. */
  private String cfgName;
  /** ���������� ������������. */
  private Map contents = new HashMap();
  /** ������. */
  private static Logger logger = Logger.getLogger("simpleconfig");
  /** ������ ����������������� ����� � ������.
   * @param newCfgName ��� ����� ������������
   */
  public final void readConfig(final String newCfgName) {
    try {
      BufferedReader in = new BufferedReader(new FileReader(newCfgName));
      String s;
      Pattern p = Pattern.compile("^(\\w+)=(.*)");
      while ((s = in.readLine()) != null) {
	if (s.startsWith("#")) {
	  continue;
	}
	Matcher m = p.matcher(s);
	m.find();
	if (m.groupCount() == 2) {
	  contents.put(m.group(1), m.group(2));
	} else {
	  logger.finer("syntax error in line '" + s + "'. line ignored.");
	}
      }
      in.close();
    } catch (IOException e) {
      logger.warning("unable to read config file " + newCfgName);
    }
  }
  /** ���������� �������� ���������� �� ����������������� �����.
   * @param name ��� ���������
   * @return �������� ��������� ��� '-undef-' ���� �������� �� ���������
   */
  public final String getValue(final String name) {
    if (!contents.containsKey(name)) {
      return "-undef-";
    }
    return (String) contents.get(name);
  }
  /** ���������� �������� ���������� �� �����������������.
   * ����� � ������ �������� �� ���������.
   * @param name ��� ���������
   * @param defaultValue �������� �� ���������
   * @return �������� ��������� ��� defaultValue ���� �������� �� ���������
   */
  public final String getValue(final String name,
			       final String defaultValue) {
    if (getValue(name).equals("-undef-")) {
      return defaultValue;
    }
    return getValue(name);
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
