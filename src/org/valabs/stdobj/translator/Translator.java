package com.novel.stdobj.translator;

import java.io.FileInputStream;
import java.util.Map;
import java.util.Properties;

import com.novel.odisp.common.Resource;

/** Класс-транслятор строк
 *
 * @author <a href="mailto:dron@novel-il.ru">Андрей А. Порохин</a>
 * @author (C) 2004 НПП "Новел-ИЛ"
 * @version $Id: Translator.java,v 1.7 2004/04/23 13:32:23 dron Exp $
 */
public class Translator extends Properties implements Resource {
  /** Имя параметра, который содержит имя файла для загрузки */
  public final static String LanguageFile = "LanguageFile";
  /** Имя файла, в дальнейшем пранируется использование начального
   * конфигурирования объектов Odisp
   */
  private final String DefaultLanguageFile = "resources/language/Default.lng";
  
  /** Конструктор транслятора
   */
  public Translator() {
    /* empty constructor */
  }

  /** Получить значение строки для определённого ключа. Используется для
   * интернализации строк в программе. Используется синхронизированный вызов -
   * рекоммендовано использование в виде разделённого ресурса одновременного
   * доступа.
   *
   * @param key Ключ хэш таблицы.
   * @param defaultValue Значение по-умолчанию, если по заданному ключу нет
   * соответсвий.
   * @return Строка, соотвествующая заданному ключу, либо значение по
   * умолчанию.
   */
  public synchronized String translate(String key, String defaultValue) {
    String tmp = getProperty(key, defaultValue);
    return tmp;
  }
  
  /** Очистка ресурса
   *
   * @param type Код выхода
   */
  public int cleanUp(int type) {
    return 0;
  }
  
  /** Комментарии в интерфейсе
   */
  public int getMaxReferenceCount() {
    return 0;
  }
  
  /** Установка конфигурации ресурса.
   * 
   * @param cfg конфигурация
   */
  public void setConfiguration(final Map cfg) {
    try {
      String fileName = DefaultLanguageFile;
      if (cfg != null) {
        fileName = config.get(LaguageFile, DefaultLanguageFile);
      }
      FileInputStream inputStream = new FileInputStream(fileName);
      load(inputStream);
    } catch (Exception e) {
      logger.warning("Exception: " + e);
    }
  }
}
