package org.valabs.odisp.standart;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;

/**
 * Реализация менеджера конфигурации.
 * 
 * @author (C) 2004 <a href="valeks@valabs.spb.ru">Валентин А. Алексеев </a>
 * @version $Id: ConfigurationManager.java,v 1.9 2005/11/10 08:18:51 valeks Exp $
 */
class ConfigurationManager implements org.valabs.odisp.common.ConfigurationManager {

  List objects = new ArrayList();

  List resources = new ArrayList();

  MultiMap params = new MultiMap();

  Logger log = Logger.getLogger(ConfigurationManager.class.getName());

  CompositeConfiguration compConf = new CompositeConfiguration();
  
  public ConfigurationManager() {
    log.setLevel(Level.ALL);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.valabs.odisp.common.ConfigurationManager#supportComponentListing()
   */
  public boolean supportComponentListing() {
    return true;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.valabs.odisp.common.ConfigurationManager#getResourceList()
   */
  public List getResourceList() {
    return resources;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.valabs.odisp.common.ConfigurationManager#getObjectList()
   */
  public List getObjectList() {
    return objects;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.valabs.odisp.common.ConfigurationManager#supportParameterFetching()
   */
  public boolean supportParameterFetching() {
    return true;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.valabs.odisp.common.ConfigurationManager#getParameter(java.lang.String, java.lang.String)
   */
  public String getParameter(final String domain, final String paramName) {
    return params.get(domain, paramName);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.valabs.odisp.common.ConfigurationManager#setCommandLineArguments(java.lang.String[])
   */
  public void setCommandLineArguments(final List args) {
    assert args.size() > 0 : "Argument list too small";
    Iterator it = args.iterator();
    while (it.hasNext()) {
      String element = (String) it.next();
      try {
        compConf.addConfiguration(new XMLConfiguration(element));
        try {
          // попытка загрузить override
          compConf.addConfiguration(new XMLConfiguration(element + ".local"));
        } catch (ConfigurationException ex) {
          
        }
      } catch (ConfigurationException ex) {
        ex.printStackTrace();
      }
    }
    loadConfiguration();
  }

  private void loadConfiguration() {
    List objsList = compConf.getList("object[@name]");
    
    int objectsCount = objsList.size();
    for (int i = 0; i < objectsCount; i++) {
      String className = compConf.getString("object(" + i + ")[@name]");
      List params = compConf.getList("object(" + i +").param[@name]");
      int paramCount = params.size();
      Map config = new HashMap();
      for (int j = 0; j < paramCount; j++) {
        String key = compConf.getString("object(" + i + ").param(" + j + ")[@name]");
        String value = compConf.getString("object(" + i + ").param(" + j + ")[@value]", "SET");
        config.put(key, value);
      }
      objects.add(new ComponentConfiguration(className, config));
    }

    List resourcesList = compConf.getList("resource[@name]");
    
    int resourcesCount = resourcesList.size();
    for (int i = 0; i < resourcesCount; i++) {
      String className = compConf.getString("resource(" + i + ")[@name]");
      List params = compConf.getList("resource(" + i +").param[@name]");
      int paramCount = params.size();
      Map config = new HashMap();
      for (int j = 0; j < paramCount; j++) {
        String key = compConf.getString("resource(" + i + ").param(" + j + ")[@name]");
        String value = compConf.getString("resource(" + i + ").param(" + j + ")[@value]", "SET");
        config.put(key, value);
      }
      resources.add(new ComponentConfiguration(className, config));
    }
    
    List rootConfig = compConf.getList("params.param");
    int rootConfigCount = rootConfig.size();
    for (int i = 0; i < rootConfigCount; i++) {
      String name = compConf.getString("params.param(" + i + ")[@name]");
      String value = compConf.getString("params.param(" + i + ")[@value]", "SET");
      params.put("root", name, value);
    }
    
    log.fine("Found " + resources.size() + " resources and " + objects.size() + " objects to load.");
  }

  class MultiMap {

    private final Map domains = new HashMap();

    public void put(final String domainName, final String param, final String value) {
      getDomain(domainName).put(param, value);
    }

    public String get(final String domainName, final String paramName) {
      return (String) getDomain(domainName).get(paramName);
    }

    public void putAll(final String domainName, final Map domainParams) {
      getDomain(domainName).putAll(domainParams);
    }

    public void putAllPrefixed(final String domainName, final String prefix, final Map domainParams) {
      //Не стоит забывать что параметров может и не быть ;)
      if (domainParams == null) { return; }

      final Map domain = getDomain(domainName);
      final Iterator keyIt = domainParams.keySet().iterator();
      while (keyIt.hasNext()) {
        final String key = (String) keyIt.next();
        domain.put(prefix + key, domainParams.get(key));
      }
    }

    public Map getDomain(final String domain) {
      Map result;
      if (domains.containsKey(domain)) {
        result = (Map) domains.get(domain);
      } else {
        result = new HashMap();
        domains.put(domain, result);
      }
      return result;
    }
  }
}