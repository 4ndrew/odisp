package com.novel.stdobj.translator;

import java.lang.String;
import java.util.Properties;
import java.io.FileInputStream;

import com.novel.odisp.commom.Resource;

/** �����-���������� �����
 *
 * @author <a href="mailto:dron@novel-il.ru">������ �. �������</a>
 * @author (C) 2004 ��� "�����-��"
 * @version $Id: Translator.java,v 1.1 2004/02/18 08:52:39 dron Exp $
 */
public class Translator implements Resource extends Properties {
  /** ��� �����, � ���������� ����������� ������������� ����������
   * ���������������� �������� Odisp
   */
  private final String LanguageFileName = "resources/translator/Default.lng";
  
  /** ����������� �����������
   */
  public Translator() {
    super();
    // �������� �����
    FileInputStream inputStream = new FileInputStream(LanguageFileName);
    if (inputStream != null)
      load(inputStream);
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
