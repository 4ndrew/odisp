package com.novel.stdobj.translator;

import java.lang.String;
import java.lang.Exception;
import java.util.Properties;
import java.util.logging.Logger;
import java.io.FileInputStream;

import com.novel.odisp.common.Resource;

/** Класс-транслятор строк
 *
 * @author <a href="mailto:dron@novel-il.ru">Андрей А. Порохин</a>
 * @author (C) 2004 НПП "Новел-ИЛ"
 * @version $Id: Translator.java,v 1.2 2004/02/18 16:03:34 dron Exp $
 */
public class Translator extends Properties implements Resource {
  /** Отладочный лог */
  private static final Logger logger = Logger.getLogger("com.novel.nms.client.strings");
  /** Имя файла, в дальнейшем пранируется использование начального
   * конфигурирования объектов Odisp
   */
  private final String LanguageFileName = "resources/language/Default.lng";
  
  /** Конструктор транслятора
   */
  public Translator() {
    super();
    // Загрузка файла
    try {
      FileInputStream inputStream = new FileInputStream(LanguageFileName);
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
    return getProperty(key, defaultValue);
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
}
