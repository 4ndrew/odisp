/* ODISP -- Message Oriented Middleware
 * Copyright (C) 2003-2005 Valentin A. Alekseev
 * Copyright (C) 2003-2005 Andrew A. Porohin 
 * 
 * ODISP is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, version 2.1 of the License.
 * 
 * ODISP is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with ODISP.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.valabs.odisp.standart;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;

/**
 * Реализация менеджера конфигурации.
 * 
 * @author (C) 2004 <a href="valeks@valabs.spb.ru">Валентин А. Алексеев </a>
 * @version $Id: ConfigurationManager.java,v 1.12 2007/01/16 10:27:17 dron Exp $
 */
class ConfigurationManager implements org.valabs.odisp.common.ConfigurationManager {

  List objects = new ArrayList();

  List resources = new ArrayList();

  MultiMap params = new MultiMap();

  Logger log = Logger.getLogger(ConfigurationManager.class.getName());
  
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
        for (int i = 0; i < cfgFiles.length; i++) {
          loadConfigFile(element + File.separator + cfgFiles[i]);
        }
      }
    }
//    loadConfiguration();
  }

  private void loadConfigFile(String element) {
    try {
//      compConf.addConfiguration();
      XMLConfiguration conf = new XMLConfiguration(element);
//      try {
//        // попытка загрузить override
//        compConf.addConfiguration(new XMLConfiguration(element + ".local"));
//      } catch (ConfigurationException ex) {
//
//      }
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
    int foundParams = 0;
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
      foundObjects++;
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
      foundResources++;
    }
    
    List rootConfig = compConf.getList("param[@name]");
    int rootConfigCount = rootConfig.size();
    for (int i = 0; i < rootConfigCount; i++) {
      String name = compConf.getString("param(" + i + ")[@name]");
      String value = compConf.getString("param(" + i + ")[@value]", "SET");
      params.put("root", name, value);
      foundParams++;
    }
    
    log.fine("Found " + foundResources + " resources and " + foundObjects + " objects to load (" + foundParams + " params).");
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