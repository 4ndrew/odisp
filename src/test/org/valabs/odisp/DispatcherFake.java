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
package test.org.valabs.odisp;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.doomdark.uuid.UUID;
import org.valabs.odisp.common.ConfigurationManager;
import org.valabs.odisp.common.Dispatcher;
import org.valabs.odisp.common.ExceptionHandler;
import org.valabs.odisp.common.Message;
import org.valabs.odisp.common.ObjectManager;
import org.valabs.odisp.common.Resource;
import org.valabs.odisp.common.ResourceManager;
import org.valabs.odisp.common.SecurityManager;
import org.valabs.stdmsg.StandartMessage;


/** ���������-�������� ��� ������������ ODISP ��������.
 * @author (�) 2004 <a href="mailto:valeks@novel-il.ru">�������� �������� �.</a>
 * @version $Id: DispatcherFake.java,v 1.7 2005/07/22 13:06:57 dron Exp $
 */
public class DispatcherFake extends TestCase implements Dispatcher, ObjectManager, ResourceManager, SecurityManager, ConfigurationManager, ExceptionHandler {

  /* (non-Javadoc)
   * @see org.valabs.odisp.common.Dispatcher#send(org.valabs.odisp.common.Message)
   */
  public void send(Message message) {
    /* �� ������������ */
  }

  /* (non-Javadoc)
   * @see org.valabs.odisp.common.Dispatcher#send(org.valabs.odisp.common.Message[])
   */
  public void send(Message[] messageList) {
    for (int i = 0; i < messageList.length; i++) {
      send(messageList[i]);
    }
  }

  /* (non-Javadoc)
   * @see org.valabs.odisp.common.Dispatcher#send(java.util.List)
   */
  public void send(List messageList) {
    Iterator it = messageList.iterator();
    while (it.hasNext()) {
      Message msg = (Message) it.next();
      send(msg);
    }
  }

  /* (non-Javadoc)
   * @see org.valabs.odisp.common.Dispatcher#getNewMessage(java.lang.String, java.lang.String, java.lang.String, org.doomdark.uuid.UUID)
   */
  public Message getNewMessage(String action, String destination, String origin, UUID inReplyTo) {
    return new StandartMessage(action, destination, origin, inReplyTo);
  }

  /* (non-Javadoc)
   * @see org.valabs.odisp.common.Dispatcher#getNewMessage()
   */
  public Message getNewMessage() {
    return new StandartMessage();
  }

  /* (non-Javadoc)
   * @see org.valabs.odisp.common.Dispatcher#getObjectManager()
   */
  public ObjectManager getObjectManager() {
    return this;
  }

  /* (non-Javadoc)
   * @see org.valabs.odisp.common.Dispatcher#getResourceManager()
   */
  public ResourceManager getResourceManager() {
    return this;
  }

  /* (non-Javadoc)
   * @see org.valabs.odisp.common.Dispatcher#getSecurityManager()
   */
  public SecurityManager getSecurityManager() {
    return this;
  }

  /* (non-Javadoc)
   * @see org.valabs.odisp.common.Dispatcher#getConfigurationManager()
   */
  public ConfigurationManager getConfigurationManager() {
    return this;
  }

  /* (non-Javadoc)
   * @see org.valabs.odisp.common.Dispatcher#getExceptionHandler()
   */
  public ExceptionHandler getExceptionHandler() {
    return this;
  }

  /* (non-Javadoc)
   * @see org.valabs.odisp.common.Dispatcher#addSecurityManager(org.valabs.odisp.common.SecurityManager)
   */
  public void addSecurityManager(SecurityManager additionalSecurityManager) {
    /* �� ������������ */
  }

  /* (non-Javadoc)
   * @see org.valabs.odisp.common.Dispatcher#addExceptionHandler(org.valabs.odisp.common.ExceptionHandler)
   */
  public void addExceptionHandler(ExceptionHandler ex) {
    /* �� ������������ */
  }

  /* (non-Javadoc)
   * @see org.valabs.odisp.common.Dispatcher#addConfigurationManager(org.valabs.odisp.common.ConfigurationManager)
   */
  public void addConfigurationManager(ConfigurationManager cman) {
    /* �� ������������ */
  }

  public void addProvider(String service, String objectName) {
    /* �� ������������ */
  }
  
  public Map getObjects() {
    return null;
  }
  
  public List getProviding() {
    return null;
  }
  
  public void loadObject(String name, Map params) {
    /* �� ������������ */
  }
  
  public void loadPending() {
    /* �� ������������ */
  }
  
  public void removeProvider(String service, String objectName) {
    /* �� ������������ */
  }
  
  public void unloadObject(String objectName, int code) {
    /* �� ������������ */
  }
  
  public Map getResources() {
    return null;
  }
  
  public void loadResource(String className, int mult, Map params) {
    /* �� ������������ */
  }
  
  public void releaseResource(String className, Resource resource) {
    /* �� ������������ */
  }
  
  public Resource resourceAcquire(String className) {
    return null;
  }
  
  public Resource resourceTryAcquire(String className) {
    return null;
  }
  
  public List statRequest() {
    return null;
  }
  
  public boolean authenticateUser(String name, String password) {
    return false;
  }
  
  public boolean checkAccess(String userName, String capabilityName, Map options) {
    return false;
  }
  
  public List getObjectList() {
    return null;
  }
  
  public String getParameter(String domain, String paramName) {
    return null;
  }
  
  public List getResourceList() {
    return null;
  }
  
  public void setCommandLineArguments(List args) {
    /* �� ������������ */
  }
  
  public boolean supportComponentListing() {
    return false;
  }
  
  public boolean supportParameterFetching() {
    return false;
  }
  
  public void signalException(Exception e) {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    e.printStackTrace(new PrintStream(out));
    assertTrue("Exception " + e.toString() + " signaled. Stack trace:\n" + out.toString(), false);
  }

/**
 * @see org.valabs.odisp.common.SecurityManager#audit(java.lang.String, java.lang.String, boolean, java.lang.String)
 */
public void audit(String userName, String actionId, boolean successStatus, String ai) {
	// TODO Auto-generated method stub
	
}
}
