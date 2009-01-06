/*
 * This is a part of odisp.
 * See LICENSE for licensing details.
 */
package org.valabs.odisp.common;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** ��������� ������, ����������� ������������ ODISP ���������.
 * @author (C) <a href="mailto:valeks@valabs.spb.ru">�������� �������� �.</a>, 2005
 * @version $Id: ODISPMessageHandler.java,v 1.2 2005/12/24 17:31:09 valeks Exp $
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface ODISPMessageHandler {
  /** ��� ��������� � ������� ������ ������ �����. */
  String value();
  /** �������� ����� ��������� � ������� �� ����������� � ��������� ������. */
  String[] mapping() default {};
}
