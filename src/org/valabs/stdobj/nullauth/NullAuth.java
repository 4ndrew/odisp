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
package org.valabs.stdobj.nullauth;

import java.util.Map;

import org.valabs.odisp.common.Message;
import org.valabs.odisp.common.SecurityManager;
import org.valabs.odisp.common.StandartODObject;
import org.valabs.stdmsg.ODObjectLoadedMessage;

/** Менеджер безопасности по-умолчанию.
 * Настройка прав производится статически при помощи установки параметра модуля в
 * файле classes.cfg. Возможно лишь два варианта поведения -- default to deny или
 * default to accept.
 * @author (C) 2004 <a href="mailto:valeks@novel-il.ru">Валентин А. Алексеев</a>
 * @version $Id: NullAuth.java,v 1.11 2005/11/20 12:33:04 valeks Exp $
 */
public class NullAuth extends StandartODObject implements SecurityManager {

  public static final String NAME = "nullauth";
  public static final String FULLNAME = "Default security manager";
  public static final String VERSION = "0.1.0";
  public static final String COPYRIGHT = "(C) 2004 Valentin A. Alekseev";
  
  public NullAuth() {
    super(NAME, FULLNAME, VERSION, COPYRIGHT);
  }

  /* (non-Javadoc)
   * @see org.valabs.odisp.common.SecurityManager#checkAccess(java.lang.String, java.lang.String, java.util.Map)
   */
  public boolean checkAccess(String userName, String capabilityName, Map options) {
    if (getParameter("default", "deny") != null && getParameter("default", "deny").equals("deny")) { return false; }
    return true;
  }

  /* (non-Javadoc)
   * @see org.valabs.odisp.common.ODObject#getDepends()
   */
  public String[] getDepends() {
    String[] depends = { "dispatcher" };
    return depends;
  }

  /* (non-Javadoc)
   * @see org.valabs.odisp.common.ODObject#getProviding()
   */
  public String[] getProviding() {
    String[] providing = { NAME, "authmanager" };
    return providing;
  }

  public void handleMessage(Message msg) {
    if (ODObjectLoadedMessage.equals(msg)) {
      dispatcher.addSecurityManager(this);
    }
  }

  /* (non-Javadoc)
   * @see org.valabs.odisp.common.SecurityManager#authenticateUser(java.lang.String, java.lang.String)
   */
  public boolean authenticateUser(String name, String password) {
    if (getParameter("default", "deny") != null && getParameter("default", "deny").equals("deny")) { return false; }
    return true;
  }

  /**
   * @see org.valabs.odisp.common.SecurityManager#audit(java.lang.String, java.lang.String, boolean, java.lang.String)
   */
  public void audit(String userName, String actionId, boolean successStatus, String ai) {
    /* не реализовано. */
  }
}