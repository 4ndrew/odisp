package org.valabs.stdobj.translator;

import java.io.FileInputStream;
import java.util.Map;
import java.util.Properties;

import org.valabs.odisp.common.Resource;

/** Класс-транслятор строк
 *
 * @author <a href="mailto:dron@novel-il.ru">Андрей А. Порохин</a>
 * @author (C) 2004 НПП "Новел-ИЛ"
 * @version $Id: Translator.java,v 1.12 2004/08/23 07:42:38 valeks Exp $
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
  
  /** Перевод закодированной строки в нормальную. Переводятся коды UTF
   * "\\uxxxx", перевод строки "\\n", табуляция "\\t", перевод каретки "\\r".
   * Код позаимствован из Properties.java от Sun.
   *
   * @param string Входная строка.
   * @return Преобразованная строка.
   */
  public static String convertUTFString(String string) {
    char curr;
    int len = string.length();
    StringBuffer resultBuff = new StringBuffer(len);
    for (int x = 0; x < len; ) {
      curr = string.charAt(x++);
      if (curr == '\\') {
        curr = string.charAt(x++);
        if (curr == 'u') {
          int value = 0;
          for (int i = 0; i < 4; i++) {
            curr = string.charAt(x++);
            switch (curr) {
              case '0': case '1': case '2': case '3': case '4':
		          case '5': case '6': case '7': case '8': case '9':
                value = (value << 4) + curr - '0';
                break;
              case 'a': case 'b': case 'c':
              case 'd': case 'e': case 'f':
                value = (value << 4) + 10 + curr - 'a';
                break;
              case 'A': case 'B': case 'C':
              case 'D': case 'E': case 'F':
                value = (value << 4) + 10 + curr - 'A';
                break;
              default:
                throw new IllegalArgumentException(
                  "UTF syntax error: \\uxxxx .");
            } // switch
          } // for
          resultBuff.append((char) value);
        } else {
          if (curr == 't') curr = '\t';
          else if (curr == 'r') curr = '\r';
          else if (curr == 'n') curr = '\n';
          else if (curr == 'f') curr = '\f';
          resultBuff.append(curr);
        }
     } else
       resultBuff.append(curr);
     }
     return resultBuff.toString();
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
      int counter = 0;
      if (cfg != null) {
        while (cfg.containsKey(LanguageFile + counter)) {
          fileName = (String) cfg.get(LanguageFile + counter++);
          FileInputStream inputStream = new FileInputStream(fileName);
          load(inputStream);
          inputStream.close();
        }
        
        // Совместимость с прошлыми версиями.
        if (counter == 0 && cfg.containsKey(LanguageFile)) {
          fileName = (String) cfg.get(LanguageFile);
          FileInputStream inputStream = new FileInputStream(fileName);
          load(inputStream);
          inputStream.close();
        }
      } else {
        FileInputStream inputStream = new FileInputStream(fileName);
        load(inputStream);
        inputStream.close();
      }
    } catch (Exception e) {
//      logger.warning("Exception: " + e);
    }
  }
}
