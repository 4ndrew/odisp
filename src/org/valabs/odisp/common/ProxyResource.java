package com.novel.odisp.common;

import java.lang.reflect.InvocationTargetException;
import java.util.logging.Logger;

/** ������-������ ��� ������� � ������������ ������� ��������.
* @author �������� �. ��������
* @author (C) 2003, ��� "�����-��"
* @version $Id: ProxyResource.java,v 1.3 2003/11/15 19:32:10 valeks Exp $
*/
public class ProxyResource implements Resource {
  /** ���������� ������ */
  private Object resource;
  /** �������� ������ � ������� 
   * @return ������ �� ������
   */
  public Object getResource() {
    return resource;
  }
  /** ��� ������ */
  private String className;
  /** ������� ��������� ������� */
  private boolean isAlive;
  /** ������ */
  private static Logger logger = Logger.getLogger("proxyresource");
  /** ������������ ���������� ������ ������������ �������������� �������� 
   * @return ������ ������
   */
  public int getMaxReferenceCount() {
    return 0;
  }
  /** ���������� ��� ������������� �������� ������� 
   * @param type ������� ������
   * @return ��� ��������
   */
  public int cleanUp(int type) {
    return 0;
  }
  /** ���������� ����� ����� �������
   * @param className ��� ������
   */
  public void setResource(String className) {
    this.className = className;
  }
  /** ��������� ����������� ������� 
   * @return ������� ���������� �������
   */
  public boolean isAlive() {
    return isAlive;
  }
  /** ������� ����� ��������� ������ �������
   * @param declParams ������ ����� ���������� ���������� ������������
   * @param params �������� ���������� ����������
   * @return ������ �� ������
   */
  public Object newInstance(Class[] declParams, Object[] params) {
    if (isAlive) {
      return resource;
    }
    try {
      resource = (Object) Class.forName(className).getConstructor(declParams).newInstance(params);
      isAlive = true;
    } catch (InvocationTargetException e) {
      logger.warning("failed: " + e);
    } catch (NoSuchMethodException e) {
      logger.warning("failed: " + e);
    } catch (ClassNotFoundException e) {
      logger.warning("failed: " + e);
    } catch (InstantiationException e) {
      logger.warning("failed: " + e);
    } catch (IllegalAccessException e) {
      logger.warning("failed: " + e);
    } catch (IllegalArgumentException e) {
      logger.warning("failed: " + e);
    }
    return resource;
  }
}
