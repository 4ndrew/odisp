package com.novel.stdobj.translator;

import java.io.FileInputStream;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

import com.novel.odisp.common.Resource;

/** �����-���������� �����
 *
 * @author <a href="mailto:dron@novel-il.ru">������ �. �������</a>
 * @author (C) 2004 ��� "�����-��"
 * @version $Id: Translator.java,v 1.6 2004/04/21 14:26:24 dron Exp $
 */
public class Translator extends Properties implements Resource {
  /** ���������� ��� */
  private static final Logger logger = Logger.getLogger("com.novel.nms.client.strings");
  /** ��� ���������, ������� �������� ��� ����� ��� �������� */
  public final static String LanguageFile = "LanguageFile";
  /** ��� �����, � ���������� ����������� ������������� ����������
   * ���������������� �������� Odisp
   */
  private final String DefaultLanguageFile = "resources/language/Default_koi8-r.lng";
  
  /** ������ ������� */
  private Map config = null;
  
  /** ����������� �����������
   */
  public Translator() {
    super();
    // TODO: �������� ��� �� �ޣ� ������������������...
    try {
      FileInputStream inputStream = new FileInputStream
        (getParameter(LanguageFile, DefaultLanguageFile));
      load(inputStream);
    } catch (Exception e) {
      logger.warning("Exception: " + e);
    }
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
    String tmp = getProperty(key, defaultValue);
    logger.fine("Translated: " + tmp);
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
  
  /** ��������� ���������� �������.
   * 
   * @param key ����.
   * @param defValue �������� ��-���������.
   * @return ���� ���������� �������� ������������� �����, �� ���
   * ������������, ����� ������������ �������� ��-���������.
   */
  private String getParameter(String key, String defValue) {
    if (config != null) {
      Object value = config.get(key); 
      return (value != null) ? (String) value : defValue; 
    }
    logger.warning("---------- Config in resources is null");
    return defValue; 
  }
  
  /** ��������� ������������ �������.
   * 
   * @param cfg ������������
   */
  public void setConfiguration(final Map cfg) {
    config = cfg;
  }
}
