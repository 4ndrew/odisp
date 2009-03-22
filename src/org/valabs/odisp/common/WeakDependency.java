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
package org.valabs.odisp.common;

/** Слабая зависимость от заданного компонента.
 * @author <a href="mailto:valeks@valabs.spb.ru">Алексеев Валентин А.</a>
 * @version $Id$
 */
public final class WeakDependency {
  private WeakDependency() {}
  public static final String create(String objectName) {
    return "~" + objectName;
  }
  
  public static final boolean isWeakDependency(String objectName) {
    return objectName.startsWith("~");
  }
  
  public static final String getWeakDependency(String objectName) {
    assert isWeakDependency(objectName) : objectName + " is not a weak dependency";
    return objectName.substring(1);
  }
}
