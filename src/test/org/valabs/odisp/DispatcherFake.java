package test.org.valabs.odisp;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

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


/** Диспетчер-заглушка для тестирования ODISP объектов.
 * @author (С) 2004 <a href="mailto:valeks@novel-il.ru">Алексеев Валентин А.</a>
 * @version $Id: DispatcherFake.java,v 1.1 2004/12/02 22:53:31 valeks Exp $
 */
public class DispatcherFake implements Dispatcher, ObjectManager, ResourceManager, SecurityManager, ConfigurationManager, ExceptionHandler {

  /* (non-Javadoc)
   * @see org.valabs.odisp.common.Dispatcher#send(org.valabs.odisp.common.Message)
   */
  public void send(Message message) {
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
    return null;
  }

  /* (non-Javadoc)
   * @see org.valabs.odisp.common.Dispatcher#getResourceManager()
   */
  public ResourceManager getResourceManager() {
    return null;
  }

  /* (non-Javadoc)
   * @see org.valabs.odisp.common.Dispatcher#getSecurityManager()
   */
  public SecurityManager getSecurityManager() {
    return null;
  }

  /* (non-Javadoc)
   * @see org.valabs.odisp.common.Dispatcher#getConfigurationManager()
   */
  public ConfigurationManager getConfigurationManager() {
    return null;
  }

  /* (non-Javadoc)
   * @see org.valabs.odisp.common.Dispatcher#getExceptionHandler()
   */
  public ExceptionHandler getExceptionHandler() {
    return null;
  }

  /* (non-Javadoc)
   * @see org.valabs.odisp.common.Dispatcher#addSecurityManager(org.valabs.odisp.common.SecurityManager)
   */
  public void addSecurityManager(SecurityManager additionalSecurityManager) {
  }

  /* (non-Javadoc)
   * @see org.valabs.odisp.common.Dispatcher#addExceptionHandler(org.valabs.odisp.common.ExceptionHandler)
   */
  public void addExceptionHandler(ExceptionHandler ex) {
  }

  /* (non-Javadoc)
   * @see org.valabs.odisp.common.Dispatcher#addConfigurationManager(org.valabs.odisp.common.ConfigurationManager)
   */
  public void addConfigurationManager(ConfigurationManager cman) {
  }

  public void addProvider(String service, String objectName) {
  }
  public Map getObjects() {
    return null;
  }
  public List getProviding() {
    return null;
  }
  public void loadObject(String name, Map params) {
  }
  public void loadPending() {
  }
  public void removeProvider(String service, String objectName) {
  }
  public void unloadObject(String objectName, int code) {
  }
  public Map getResources() {
    return null;
  }
  public void loadResource(String className, int mult, Map params) {
  }
  public void releaseResource(String className, Resource resource) {
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
  public void unloadResource(String name, int code) {
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
  public void setCommandLineArguments(String[] args) {
  }
  public boolean supportComponentListing() {
    return false;
  }
  public boolean supportParameterFetching() {
    return false;
  }
  public void signalException(Exception e) {
  }
}
