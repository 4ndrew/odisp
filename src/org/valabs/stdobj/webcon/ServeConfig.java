package com.novel.stdobj.webcon;

import java.util.*;
import com.novel.stdobj.webcon.servlet.*;
import com.novel.stdobj.webcon.servlet.http.*;

class ServeConfig implements ServletConfig {
  private ServletContext context;
  public ServeConfig( ServletContext context ) {
    this.context = context;
  }

  // Methods from ServletConfig.

  /// Returns the context for the servlet.
  public ServletContext getServletContext() {
    return context;
  }

  /// Gets an initialization parameter of the servlet.
  // @param name the parameter name
  public String getInitParameter( String name ) {
    // This server doesn't support servlet init params.
    return null;
  }

  /// Gets the names of the initialization parameters of the servlet.
  // @param name the parameter name
  public Enumeration getInitParameterNames() {
    // This server doesn't support servlet init params.
    return new Vector().elements();
  }

}
