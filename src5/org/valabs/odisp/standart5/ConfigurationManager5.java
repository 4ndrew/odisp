package org.valabs.odisp.standart5;

import java.io.File;
import java.io.FilenameFilter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;

/**
 * Реализация менеджера конфигурации.
 * 
 * @author (C) 2005 <a href="valeks@valabs.spb.ru">Валентин А. Алексеев </a>
 * @version $Id: ConfigurationManager5.java,v 1.4 2006/03/29 11:33:24 valeks Exp $
 */
class ConfigurationManager5 implements org.valabs.odisp.common.ConfigurationManager {

  final List<ComponentConfiguration> objects = new LinkedList<ComponentConfiguration>();

  final List<ComponentConfiguration> resources = new LinkedList<ComponentConfiguration>();

  final MultiMap params = new MultiMap();

  static final Logger log = Logger.getLogger(ConfigurationManager5.class.getName());
  
  public ConfigurationManager5() {
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
      File f = new File(element);
      if (!f.exists()) {
        continue;
      }
      if (f.isFile()) {
        // если это файл -- загрузить как есть
        loadConfigFile(element);
      } else if (f.isDirectory()) {
        // если это каталог -- найти все xml файлы в нём и загрузить
        String cfgFiles[] = f.list(new FilenameFilter() {
          public boolean accept(File arg0, String arg1) {
            return arg1.endsWith(".xml");
          }
        });
        for (String aFile : cfgFiles) {
          loadConfigFile(element + File.separator + aFile);
        }
      }
    }
  }

  private void loadConfigFile(String element) {
    try {
      XMLConfiguration conf = new XMLConfiguration(element);
      log.info("Loaded " + element);
      loadConfiguration(conf);
    } catch (ConfigurationException ex) {
      ex.printStackTrace();
    }
  }

  private void loadConfiguration(XMLConfiguration compConf) {
    List objsList = compConf.getList("object[@name]");
    int foundObjects = 0;
    int foundResources = 0;
    int objectsCount = objsList.size();
    for (int i = 0; i < objectsCount; i++) {
      String className = compConf.getString("object(" + i + ")[@name]");
      List params = compConf.getList("object(" + i +").param[@name]");
      int paramCount = params.size();
      Map<String, String> config = new HashMap<String, String>();
      for (int j = 0; j < paramCount; j++) {
        String key = compConf.getString("object(" + i + ").param(" + j + ")[@name]");
        String value = compConf.getString("object(" + i + ").param(" + j + ")[@value]", "SET");
        config.put(key, value);
      }
      objects.add(new ComponentConfiguration(className, config));
      foundObjects++;
    }

    List resourcesList = compConf.getList("resource[@name]");
    
    int resourcesCount = resourcesList.size();
    for (int i = 0; i < resourcesCount; i++) {
      String className = compConf.getString("resource(" + i + ")[@name]");
      List params = compConf.getList("resource(" + i +").param[@name]");
      int paramCount = params.size();
      Map<String, String> config = new HashMap<String, String>();
      for (int j = 0; j < paramCount; j++) {
        String key = compConf.getString("resource(" + i + ").param(" + j + ")[@name]");
        String value = compConf.getString("resource(" + i + ").param(" + j + ")[@value]", "SET");
        config.put(key, value);
      }
      resources.add(new ComponentConfiguration(className, config));
      foundResources++;
    }
    
    List rootConfig = compConf.getList("params.param");
    int rootConfigCount = rootConfig.size();
    for (int i = 0; i < rootConfigCount; i++) {
      String name = compConf.getString("params.param(" + i + ")[@name]");
      String value = compConf.getString("params.param(" + i + ")[@value]", "SET");
      params.put("root", name, value);
    }
    
    log.fine("Found " + foundResources + " resources and " + foundObjects + " objects to load.");
  }

  class MultiMap {

    private final Map<String, Map<String, String>> domains = new HashMap<String, Map<String, String>>();

    public void put(final String domainName, final String param, final String value) {
      getDomain(domainName).put(param, value);
    }

    public String get(final String domainName, final String paramName) {
      return getDomain(domainName).get(paramName);
    }

    public void putAll(final String domainName, final Map<String, String> domainParams) {
      getDomain(domainName).putAll(domainParams);
    }

    public void putAllPrefixed(final String domainName, final String prefix, final Map<String, String> domainParams) {
      //Не стоит забывать что параметров может и не быть ;)
      if (domainParams == null) { return; }

      final Map<String, String> domain = getDomain(domainName);
      for (String key: domainParams.keySet()) {
        domain.put(prefix + key, domainParams.get(key));
      }
    }

    public Map<String, String> getDomain(final String domain) {
      Map<String, String> result;
      if (domains.containsKey(domain)) {
        result = domains.get(domain);
      } else {
        result = new HashMap<String, String>();
        domains.put(domain, result);
      }
      return result;
    }
  }
}