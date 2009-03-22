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
package org.valabs.stdobj.bugtrack;

import java.util.Map;

/** ��������� �������� ����������� ������/�������� ��������� �� ������.
 * @author valeks
 * @version $Id: AbstractBugWriter.java,v 1.1 2005/02/27 12:37:30 valeks Exp $
 */
abstract class AbstractBugWriter {
  protected Map config;
  public AbstractBugWriter(Map configuration) {
    config = configuration;
  }
  
  public abstract void writeBugReport(final String id, final String pc, final String ai, final SystemSnapshot ss);
}
