package org.valabs.odisp.standart;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.valeks.xlang.parser.Parser;
import org.valeks.xlang.parser.Tag;
import org.valeks.xlang.parser.XLangException;

/**
 * ���������� ��������� ������������.
 * 
 * @author (C) 2004 <a href="valeks@valabs.spb.ru">�������� �. �������� </a>
 * @version $Id: ConfigurationManager.java,v 1.6 2005/02/27 12:37:31 valeks Exp $
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
    try {
      final InputStream inp = new FileInputStream((String) args.get(0));
      loadConfiguration(new Parser(inp).getRootTag());
    } catch (FileNotFoundException e) {
      log.severe("configuration file " + args.get(0) + " not found.");
    } catch (XLangException e) {
      log.severe("configuration file " + args.get(0) + " contains unrecoverable errors: " + e);
    }
  }

  private void loadConfiguration(final Tag docTag) {
    final Iterator childIt = docTag.getChild().iterator();
    params.putAll("root", getParamsForTag(docTag));
    while (childIt.hasNext()) {
      final Tag curt = (Tag) childIt.next();
      if (curt.getName().equalsIgnoreCase("object")) {
        final String className = (String) curt.getAttributes().get("name");
        if (className == null) {
          log.warning("object tag has no name attribute. ignoring.");
          continue;
        }
        final Map lparams = getParamsForTag(curt);
        objects.add(new org.valabs.odisp.common.ConfigurationManager.ComponentConfiguration(className,
                lparams));
        params.putAllPrefixed("boot", className, lparams);
      } else if (curt.getName().equalsIgnoreCase("resource")) {
        final String className = (String) curt.getAttributes().get("name");
        if (className == null) {
          log.warning("resource tag has no name attribute. ignoring.");
          continue;
        }
        final Map lparams = getParamsForTag(curt);
        resources.add(new org.valabs.odisp.common.ConfigurationManager.ComponentConfiguration(className,
                lparams));
        params.putAllPrefixed("boot", className, lparams);
      }
    }
    log.fine("Found " + resources.size() + " resources and " + objects.size() + " objects to load.");
  }

  /**
   * �������� ���������� � ���������� ��� ��������� ����.
   * 
   * @param childTag ���
   */
  private Map getParamsForTag(final Tag childTag) {
    Map params = null;
    if (childTag.getChild().size() != 0) {
      params = new HashMap();
      // ������� ������� -- ���������� �������������� �� ������ � ��������� ������
      final Iterator cit = childTag.getChild().iterator();
      while (cit.hasNext()) {
        final Tag ctag = (Tag) cit.next();
        if (ctag.getName().equalsIgnoreCase("param")) {
          final String paramName = (String) ctag.getAttributes().get("name");
          final String paramValue = (String) ctag.getAttributes().get("value");
          if (paramName != null && paramValue != null) {
            params.put(paramName, paramValue);
          }
        }
      }
    }
    return params;
  }

  class MultiMap {

    private final Map domains = new HashMap();

    public void put(final String domainName, final String param, final String value) {
      getDomain(domainName).put(param, value);
    }

    public String get(final String domainName, final String paramName) {
      return (String) getDomain(domainName).get(paramName);
    }

    public void putAll(final String domainName, final Map params) {
      getDomain(domainName).putAll(params);
    }

    public void putAllPrefixed(final String domainName, final String prefix, final Map params) {
      //�� ����� �������� ��� ���������� ����� � �� ���� ;)
      if (params == null) { return; }

      final Map domain = getDomain(domainName);
      final Iterator keyIt = params.keySet().iterator();
      while (keyIt.hasNext()) {
        final String key = (String) keyIt.next();
        domain.put(prefix + key, params.get(key));
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