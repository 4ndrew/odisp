package org.valabs.stdobj.nullauth;

import java.util.Map;

import org.valabs.odisp.common.Message;
import org.valabs.odisp.common.SecurityManager;
import org.valabs.odisp.common.StandartODObject;
import org.valabs.stdmsg.ODObjectLoadedMessage;

/** �������� ������������ ��-���������.
 * ��������� ���� ������������ ���������� ��� ������ ��������� ��������� ������ �
 * ����� classes.cfg. �������� ���� ��� �������� ��������� -- default to deny ���
 * default to accept.
 * @author (C) 2004 <a href="mailto:valeks@novel-il.ru">�������� �. ��������</a>
 * @version $Id: NullAuth.java,v 1.3 2004/08/23 07:42:38 valeks Exp $
 */
public class NullAuth extends StandartODObject implements SecurityManager {
	public static final String NAME = "nullauth";
	
	public NullAuth(final Integer id) {
		super(NAME + id.toString());
	}
  /* (non-Javadoc)
   * @see org.valabs.odisp.common.SecurityManager#checkAccess(java.lang.String, java.lang.String, java.util.Map)
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
   * @see org.valabs.odisp.common.ODObject#getDepends()
   */
  public String[] getDepends() {
    String[] depends = {
    	"dispatcher"
    };
    return depends;
  }

  /* (non-Javadoc)
   * @see org.valabs.odisp.common.ODObject#getProviding()
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
