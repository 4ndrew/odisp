package com.novel.stdobj.simpleconfig;

import com.novel.odisp.common.Resource;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.FileInputStream;
import java.util.logging.Logger;
import java.util.Properties;

/** Ресурс ODISP реализующий доступ к конфигурационным файлам  формата [имя]=[значение].
* @author Валентин А. Алексеев
* @author (C) 2003, НПП "Новел-ИЛ"
* @version $Id: SimpleConfig.java,v 1.10 2004/02/12 17:50:23 valeks Exp $
*/
public class SimpleConfig extends Properties implements Resource {
  /** Имя конфигурационного файла по-умолчанию.*/
  public static final String DEFAULT_CONFIG = "simpleconfig.dat";
  /** Журнал. */
  private static Logger logger = Logger.getLogger("simpleconfig");
  /** Чтение конфигурационного файла.
   * @param newCfgName имя файла конфигурации
   * @deprecated необходимо использовать load(new FileInputStream(String))
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
  /** Возвращает значение переменной из конфигурационного файла.
   * @param name имя параметра
   * @return значение параметра или '-undef-' если параметр не определен
   * @deprecated необходимо использовать getProperty(String)
   */
  public final String getValue(final String name) {
    String result = getProperty(name);
    if (result == null) {
      return "-undef-";
    }
    return result;
  }
  /** Возвращает значение переменной из конфигурационного.
   * файла с учетом значения по умолчанию.
   * @param name имя параметра
   * @param defaultValue значение по умолчанию
   * @deprecated необходимо использовать getProperty(String, String)
   * @return значение параметра или defaultValue если параметр не определен
   */
  public final String getValue(final String name,
			       final String defaultValue) {
    return getProperty(name, defaultValue);
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
