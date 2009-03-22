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

/** Аннотация метода, являющегося обработчиком ODISP сообщения.
 * @author (C) <a href="mailto:valeks@valabs.spb.ru">Алексеев Валентин А.</a>, 2005
 * @version $Id: ODISPMessageHandler.java,v 1.2 2005/12/24 17:31:09 valeks Exp $
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface ODISPMessageHandler {
  /** Тип сообщения с которым связан данный метод. */
  String value();
  /** Названия полей сообщения в порядке их отображения в параметры метода. */
  String[] mapping() default {};
}
