package com.novel.stdobj.translator;

import java.io.FileInputStream;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

import com.novel.odisp.common.Resource;

/** Класс-транслятор строк
 *
 * @author <a href="mailto:dron@novel-il.ru">Андрей А. Порохин</a>
 * @author (C) 2004 НПП "Новел-ИЛ"
 * @version $Id: Translator.java,v 1.6 2004/04/21 14:26:24 dron Exp $
 */
public class Translator extends Properties implements Resource {
  /** Отладочный лог */
  private static final Logger logger = Logger.getLogger("com.novel.nms.client.strings");
  /** Имя параметра, который содержит имя файла для загрузки */
  public final static String LanguageFile = "LanguageFile";
  /** Имя файла, в дальнейшем пранируется использование начального
   * конфигурирования объектов Odisp
   */
  private final String DefaultLanguageFile = "resources/language/Default_koi8-r.lng";
  
  /** Конфиг ресурса */
  private Map config = null;
  
  /** Конструктор транслятора
   */
  public Translator() {
    super();
    // TODO: Подумать тут на счёт конфигурабельности...
    try {
      FileInputStream inputStream = new FileInputStream
        (getParameter(LanguageFile, DefaultLanguageFile));
      load(inputStream);
    } catch (Exception e) {
      logger.warning("Exception: " + e);
    }
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
    logger.fine("Translated: " + tmp);
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
  
  /** Получение параметров ресурса.
   * 
   * @param key ключ.
   * @param defValue Значение по-умолчанию.
   * @return Если существует значение сопоставенное ключу, то оно
   * возвращается, иначе возвращается значение по-умолчанию.
   */
  private String getParameter(String key, String defValue) {
    if (config != null) {
      Object value = config.get(key); 
      return (value != null) ? (String) value : defValue; 
    }
    logger.warning("---------- Config in resources is null");
    return defValue; 
  }
  
  /** Установка конфигурации ресурса.
   * 
   * @param cfg конфигурация
   */
  public void setConfiguration(final Map cfg) {
    config = cfg;
  }
}
