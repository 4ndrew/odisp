package org.valabs.odisp.common;

import java.util.List;
import java.util.Map;


/** Интерфейс менеджера конфигурации диспетчера ODISP.
 * @author (C) 2004 <a href="mailto:valeks@valabs.spb.ru">Алексеев Валентин А.</a>
 * @version $Id: ConfigurationManager.java,v 1.3 2005/02/27 12:37:29 valeks Exp $
 */
public interface ConfigurationManager {
  /** Задание параметров коммандной строки.
   */
  public void setCommandLineArguments(List args);
  /** Поддерживает ли менеджер конфигурации выдачу списка ресурсов и объектов для загрузки.
   */
  public boolean supportComponentListing();
  
  /** Получить список ресурсов для загрузки.
   * Список состоит из классов типа ComponentConfiguration. 
   */
  public List getResourceList();

  /** Получить список объектов для загрузки.
   * Список состоит из классов типа ComponentConfiguration. 
   */
  public List getObjectList();

  /** Поддерживает ли менеджер конфигурации чтение параметров. */
  public boolean supportParameterFetching();
  
  /** Получение значения параметра с указанным именем.
   * @param domain домен параметра
   * @param paramName название параметра
   */
  public String getParameter(String domain, String paramName);
  
  /** Запись о конфигурации отдельного компонента. */
  class ComponentConfiguration {
    /** Имя класса. */
    private final String className;
    /** Конфигурация. */
    private final Map configuration;
    /** Инициализация новой записи.
     * @param _className имя класса
     * @param _configuration конфигурация
     */
    public ComponentConfiguration(String _className, Map _configuration) {
      className = _className;
      configuration = _configuration;
    }
    /** Вернуть сохранённое имя класса. */
    public String getClassName() {
      return className;
    }
    
    /** Вернуть сохранённую конфигурацию. */
    public Map getConfiguration() {
      return configuration;
    }
  }
}
