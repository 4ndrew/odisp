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

/** Ресурс ODISP реализующий доступ к конфигурационным файлам формата [имя]=[значение].
* @author Валентин А. Алексеев
* @author (C) 2003, НПП "Новел-ИЛ"
* @version $Id: SimpleConfig.java,v 1.9 2004/01/16 14:31:57 valeks Exp $
*/
public class SimpleConfig implements Resource {
  /** Имя конфигурационного файла. */
  private String cfgName;
  /** Содержимое конфигурации. */
  private Map contents = new HashMap();
  /** Журнал. */
  private static Logger logger = Logger.getLogger("simpleconfig");
  /** Чтение конфигурационного файла в память.
   * @param newCfgName имя файла конфигурации
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
  /** Возвращает значение переменной из конфигурационного файла.
   * @param name имя параметра
   * @return значение параметра или '-undef-' если параметр не определен
   */
  public final String getValue(final String name) {
    if (!contents.containsKey(name)) {
      return "-undef-";
    }
    return (String) contents.get(name);
  }
  /** Возвращает значение переменной из конфигурационного.
   * файла с учетом значения по умолчанию.
   * @param name имя параметра
   * @param defaultValue значение по умолчанию
   * @return значение параметра или defaultValue если параметр не определен
   */
  public final String getValue(final String name,
			       final String defaultValue) {
    if (getValue(name).equals("-undef-")) {
      return defaultValue;
    }
    return getValue(name);
  }
  /** Выход.
   * @param type тип выхода
   * @return код выхода
   */
  public final int cleanUp(final int type) {
    return 0;
  }

  /** Вернуть максимальное кол-во экземпляров.
   * @return максимальное число экземпляров
   */
  public final int getMaxReferenceCount() {
    return 0;
  }
}
