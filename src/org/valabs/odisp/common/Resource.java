package org.valabs.odisp.common;

import java.util.Map;

/**
 * Интерфейс ресурсного объекта ODISP ядра.
 * 
 * @author (C) 2003, <a href="mailto:valeks@novel-il.ru">Валентин А. Алексеев</a>
 * @version $Id: Resource.java,v 1.8 2005/07/22 13:06:56 dron Exp $
 */
public interface Resource {
  /**
   * Максимальное количество ссылок одновременно поддерживаемых объектом.
   * 
   * @return максимальное кол-во ссылок.
   */
  int getMaxReferenceCount();

  /**
   * Вызывается при необходимости очистить ресурсы.
   * 
   * @param type признак выхода.
   * @return код возврата.
   */
  int cleanUp(int type);
  
  /**
   * Установить конфигурацию.
   * 
   * @param config конфигурация.
   */
  void setConfiguration(Map config);
}
