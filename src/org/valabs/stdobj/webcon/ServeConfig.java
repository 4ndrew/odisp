package org.valabs.stdobj.webcon;

import java.util.Enumeration;
import java.util.Vector;

import org.valabs.stdobj.webcon.servlet.ServletConfig;
import org.valabs.stdobj.webcon.servlet.ServletContext;

class ServeConfig implements ServletConfig {
  private ServletContext context;
  public ServeConfig(ServletContext context) {
    this.context = context;
  }

  

  /**
   * @see org.valabs.stdobj.webcon.servlet.ServletConfig#getServletContext()
   */
  public ServletContext getServletContext() {
    return context;
  }

  /**
   * @see org.valabs.stdobj.webcon.servlet.ServletConfig#getInitParameter(java.lang.String)
   */
  public String getInitParameter(String name) {
    // This server doesn't support servlet init params.
    return null;
  }

  /**
   * @see org.valabs.stdobj.webcon.servlet.ServletConfig#getInitParameterNames()
   */
  public Enumeration getInitParameterNames() {
    // This server doesn't support servlet init params.
    return new Vector().elements();
  }

}
