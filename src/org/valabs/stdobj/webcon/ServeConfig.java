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
