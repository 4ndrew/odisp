package com.novel.odisp.common;

import java.lang.reflect.InvocationTargetException;
import java.util.logging.Logger;

/** ������-������ ��� ������� � ������������ ������� ��������.
* @author �������� �. ��������
* @author (C) 2003, ��� "�����-��"
* @version $Id: ProxyResource.java,v 1.4 2004/01/16 14:31:57 valeks Exp $
*/
public class ProxyResource implements Resource {
  /** ���������� ������. */
  private Object resource;
  /** �������� ������ � �������.
   * @return ������ �� ������
   */
  public final Object getResource() {
    return resource;
  }
  /** ��� ������. */
  private String className;
  /** ������� ��������� �������. */
  private boolean isAlive;
  /** ������. */
  private static Logger logger = Logger.getLogger("proxyresource");
  /** ������������ ���������� ������ ������������ �������������� ��������.
   * @return ������ ������
   */
  public final int getMaxReferenceCount() {
    return 0;
  }
  /** ���������� ��� ������������� �������� �������.
   * @param type ������� ������
   * @return ��� ��������
   */
  public final int cleanUp(final int type) {
    return 0;
  }
  /** ���������� ����� ����� �������.
   * @param cName ��� ������
   */
  public final void setResource(final String cName) {
    className = cName;
  }
  /** ��������� ����������� �������.
   * @return ������� ���������� �������
   */
  public final boolean isAlive() {
    return isAlive;
  }
  /** ������� ����� ��������� ������ �������.
   * @param declParams ������ ����� ���������� ���������� ������������
   * @param params �������� ���������� ����������
   * @return ������ �� ������
   */
  public final Object newInstance(final Class[] declParams, final Object[] params) {
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
