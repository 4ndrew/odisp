package com.novel.stdobj.simpleconfig;

import com.novel.odisp.common.Resource;
import java.io.*;
import java.util.*;
import java.util.regex.*;
import java.util.logging.*;

/** Ресурс ODISP реализующий доступ к конфигурационным файлам формата [имя]=[значение]
* @author Валентин А. Алексеев
* @author (C) 2003, НПП "Новел-ИЛ"
* @version $Id: SimpleConfig.java,v 1.7 2003/11/15 20:03:59 valeks Exp $
*/
public class SimpleConfig implements Resource {
  String cfgName;
  Map contents = new HashMap();
  private static Logger logger = Logger.getLogger("simpleconfig");
  /** Чтение конфигурационного файла в память
   * @param cfgName имя файла конфигурации
   */
  public void readConfig(String cfgName) {
    logger.finest("SimpleConfig.readConfig(" + cfgName + ")");    
    try {
      BufferedReader in = new BufferedReader(new FileReader(cfgName));
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
	  logger.finer("[w] syntax error in line '" + s + "'. line ignored.");
	}
      }
      in.close();
    } catch (IOException e) { /*NOP*/ }
  }
  /** Возвращает значение переменной из конфигурационного файла 
   * @param name имя параметра
   * @return значение параметра или '-undef-' в случае если параметр не определен
   */
  public String getValue(String name) {
    logger.finer("SimpleConfig.getValue(" + name + ")");
    if (!contents.containsKey(name)) {
      return "-undef-";
    }
    return (String) contents.get(name);
  }
  /** Возвращает значение переменной из конфигурационного файла с учетом значения по умолчанию
   * @param name имя параметра
   * @param defaultValue значение по умолчанию
   * @return значение параметра или defaultValue если параметр не определен
   */
  public String getValue(String name, String defaultValue) {
    if (getValue(name).equals("-undef-")) {
      return defaultValue;
    }
    return getValue(name);
  }
  public int cleanUp(int type) {
    return 0;
  }
  public int getMaxReferenceCount() {
    return 0;
  }
}
