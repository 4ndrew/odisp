package com.novel.stdobj.nullauth;

import java.util.Map;

import com.novel.odisp.common.Message;
import com.novel.odisp.common.SecurityManager;
import com.novel.odisp.common.StandartODObject;
import com.novel.stdmsg.ODObjectLoadedMessage;

/** Менеджер безопасности по-умолчанию.
 * Настройка прав производится статически при помощи установки параметра модуля в
 * файле classes.cfg. Возможно лишь два варианта поведения -- default to deny или
 * default to accept.
 * @author (C) 2004 <a href="mailto:valeks@novel-il.ru">Валентин А. Алексеев</a>
 * @version $Id: NullAuth.java,v 1.2 2004/08/18 12:48:40 valeks Exp $
 */
public class NullAuth extends StandartODObject implements SecurityManager {
	public static final String NAME = "nullauth";
	
	public NullAuth(final Integer id) {
		super(NAME + id.toString());
	}
  /* (non-Javadoc)
   * @see com.novel.odisp.common.SecurityManager#checkAccess(java.lang.String, java.lang.String, java.util.Map)
   */
  public boolean checkAccess(
    String userName,
    String capabilityName,
    Map options) {
    if (getParameter("default", "deny") != null 
    	&& getParameter("default", "deny").equals("deny")) {
    		return false;
    }
    return true;
  }

  /* (non-Javadoc)
   * @see com.novel.odisp.common.ODObject#getDepends()
   */
  public String[] getDepends() {
    String[] depends = {
    	"dispatcher"
    };
    return depends;
  }

  /* (non-Javadoc)
   * @see com.novel.odisp.common.ODObject#getProviding()
   */
  public String[] getProviding() {
    String[] providing = { NAME, "authmanager"};
    return providing;
  }
  public void handleMessage(Message msg) {
  	if (ODObjectLoadedMessage.equals(msg)) {
  		dispatcher.addSecurityManager(this);
  	}
  }
}
