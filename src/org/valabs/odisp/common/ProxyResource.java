package com.novel.odisp.common;

import java.lang.reflect.InvocationTargetException;
import java.util.logging.Logger;

/** Прокси-ресурс для доступа к произвольным внешним объектам.
* @author (C) 2003 <a href="mailto:valeks@novel-il.ru">Валентин А. Алексеев</a>
* @version $Id: ProxyResource.java,v 1.6 2004/08/18 12:48:40 valeks Exp $
*/
public class ProxyResource implements Resource {
  /** Собственно ресурс. */
  private Object resource;
  /** Получить доступ к ресурсу.
   * @return ссылка на ресурс
   */
  public final Object getResource() {
    return resource;
  }
  /** Имя класса. */
  private String className;
  /** Признак подгрузки ресурса. */
  private boolean isAlive;
  /** Журнал. */
  private static Logger logger = Logger.getLogger("proxyresource");
  /** Максимальное количество ссылок одновременно поддерживаемых объектом.
   * @return список ссылок
   */
  public final int getMaxReferenceCount() {
    return 0;
  }
  /** Вызывается при необходимости очистить ресурсы.
   * @param type признак выхода
   * @return код возврата
   */
  public final int cleanUp(final int type) {
    return 0;
  }
  /** Установить новый класс ресурса.
   * @param cName имя класса
   */
  public final void setResource(final String cName) {
    className = cName;
  }
  /** Проверить доступность ресурса.
   * @return признак готовности ресурса
   */
  public final boolean isAlive() {
    return isAlive;
  }
  /** Создать новый экземпляр класса ресурса.
   * @param declParams список типов формальных параметров конструктора
   * @param params значения формальных параметров
   * @return ссылка на объект
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
  public void setConfiguration(java.util.Map cfg) {
  
  }
}
