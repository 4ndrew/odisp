package com.novel.stdobj.nullauth;

import java.util.Map;

import com.novel.odisp.common.Message;
import com.novel.odisp.common.SecurityManager;
import com.novel.odisp.common.StandartODObject;
import com.novel.stdmsg.ODObjectLoadedMessage;

/**
 * @author valeks
 * @author (C) 2004 îðð "îÏ×ÅÌ-éì"
 * @version $Id: NullAuth.java,v 1.1 2004/07/01 13:45:39 valeks Exp $
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
