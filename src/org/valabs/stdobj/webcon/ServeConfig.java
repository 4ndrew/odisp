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

  /** Returns the context for the servlet. */
  public ServletContext getServletContext() {
    return context;
  }

  /** Gets an initialization parameter of the servlet.
   * @param name the parameter name
   */
  public String getInitParameter(String name) {
    // This server doesn't support servlet init params.
    return null;
  }

  /** Gets the names of the initialization parameters of the servlet.
   */
  public Enumeration getInitParameterNames() {
    // This server doesn't support servlet init params.
    return new Vector().elements();
  }

}
