package com.novel.stdobj.translator;

import java.lang.String;
import java.lang.Exception;
import java.util.Properties;
import java.util.logging.Logger;
import java.io.FileInputStream;

import com.novel.odisp.common.Resource;

/** �����-���������� �����
 *
 * @author <a href="mailto:dron@novel-il.ru">������ �. �������</a>
 * @author (C) 2004 ��� "�����-��"
 * @version $Id: Translator.java,v 1.2 2004/02/18 16:03:34 dron Exp $
 */
public class Translator extends Properties implements Resource {
  /** ���������� ��� */
  private static final Logger logger = Logger.getLogger("com.novel.nms.client.strings");
  /** ��� �����, � ���������� ����������� ������������� ����������
   * ���������������� �������� Odisp
   */
  private final String LanguageFileName = "resources/language/Default.lng";
  
  /** ����������� �����������
   */
  public Translator() {
    super();
    // �������� �����
    try {
      FileInputStream inputStream = new FileInputStream(LanguageFileName);
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
    return getProperty(key, defaultValue);
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
}
