package org.valabs.stdobj.translator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

import org.valabs.odisp.common.Resource;

/** �����-���������� �����
 *
 * @author <a href="mailto:dron@novel-il.ru">������ �. �������</a>
 * @author (C) 2004 ��� "�����-��"
 * @version $Id: Translator.java,v 1.14 2004/09/11 13:35:45 dron Exp $
 */
public class Translator extends Properties implements Resource {
  /** ���� � �������� ����� ��������� */
  public static final String LanguageRootDir 	= "LanguageRootDir";
  /** ������������� ����� */
  public static final String LanguageId 			= "LanguageId";
  /** ������������ */
  public static final String AutoLoad  				= "Autoload";
  /** ���� � �������� ����� ��������� ��-��������� */
  private final String DefaultLanguageDir = "resources/language";
  /** ������������� ����� ��-��������� */
  private final String DefaultLanguageId = "en";
  /** �������� ������� ��� ���������� */
  private String rootDir = null;
  /** ������������� ����� */
  private String langId = null;
  
  /** ������! */
  private static final Logger logger = Logger.getLogger(Translator.class.getName()); 
  
  /** ����������� �����������
   */
  public Translator() {
    /* empty constructor */
  }
  
  /** ������� �������������� ������ � ����������. ����������� ���� UTF
   * "\\uxxxx", ������� ������ "\\n", ��������� "\\t", ������� ������� "\\r".
   * ��� ������������� �� Properties.java �� Sun.
   *
   * @param string ������� ������.
   * @return ��������������� ������.
   */
  public final static String convertUTFString(String string) {
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

  /** �������� �������� ������ ��� ������̣����� �����. ������������ ���
   * �������������� ����� � ���������. ������������ ������������������ ����� -
   * �������������� ������������� � ���� �����̣����� ������� ��������������
   * �������.
   *
   * @param key ���� ��� �������.
   * @param defaultValue �������� ��-���������, ���� �� ��������� ����� ���
   * �����������.
   * @return ������, �������������� ��������� �����, ���� �������� ��
   * ���������.
   */
  public synchronized String translate(String key, String defaultValue) {
    String tmp = defaultValue;
    if (key != null)
      tmp = getProperty(key, defaultValue);
    return tmp;
  }
  
  /** ������� �������
   *
   * @param type ��� ������
   */
  public int cleanUp(int type) {
    return 0;
  }
  
  /** ����������� � ����������
   */
  public int getMaxReferenceCount() {
    return 0;
  }
  
  /** ��������� �������� ��������� ��������.
   * 
   * @return ���� � ��������� �������� ����������. �������� null, ������
   * ���� � dispatcher'� ����� ������.
   */
  public String getRootDir() {
    return rootDir;
  }
  
  /** ��������� �������� �������������� �����.
   * 
   * @return 2 ������� ��� ����������. 
   */
  public String getLangId() {
    return langId;
  }
  
  /** ��������� ������������ �������.
   * 
   * @param cfg ������������
   */
  public void setConfiguration(final Map cfg) {
    rootDir = DefaultLanguageDir;
    langId = DefaultLanguageId;
    boolean autoLoad = true;
    if (cfg != null) {
      rootDir = (cfg.get(LanguageRootDir) != null)
      	? (String) cfg.get(LanguageRootDir) : rootDir;
      langId = (cfg.get(LanguageId) != null)
        ? (String) cfg.get(LanguageId) : langId;
      String tmp = (String) cfg.get(AutoLoad);
      if (tmp != null)
        autoLoad = tmp.equals("1") || tmp.equals("yes") || tmp.equals("yo"); 
    }
    
    if (!autoLoad) return;
    
    logger.info("i'm here!");
    File f = new File(rootDir + File.separator + langId);
    if (f.canRead() && f.isDirectory()) {
      String[] files = f.list(new FilenameFilter() {
        public boolean accept(final File dir, final String name) {
          return name.endsWith("lng");
        }
      });
      for (int i = 0; i < files.length; i++) {
        try {
          load(new FileInputStream(f.getAbsolutePath() + File.separator
              + files[i]));
        } catch (FileNotFoundException e) {
          logger.warning("File not found: " + files[i]);
        } catch (IOException e) {
          logger.warning("Input/Output error while reading " + files[i]);
        }
      }
    } else {
      logger.warning("Directory could not be read: " + f);
    }
  }
}
