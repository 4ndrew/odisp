package org.valabs.odisp.standart;

import java.util.Iterator;
import java.util.List;

import org.doomdark.uuid.UUID;
import org.valabs.odisp.common.ConfigurationManager;
import org.valabs.odisp.common.Dispatcher;
import org.valabs.odisp.common.ExceptionHandler;
import org.valabs.odisp.common.Message;
import org.valabs.odisp.common.ObjectManager;
import org.valabs.odisp.common.ResourceManager;
import org.valabs.odisp.common.SecurityManager;


/** Обёртка диспетчера для защиты от подмены сообщений.
 * @author <a href="mailto:valeks@valabs.spb.ru">Алексеев Валентин А.</a>
 * @version $Id: SecureDispatcher.java,v 1.1 2005/02/12 17:25:37 valeks Exp $
 */
final class SecureDispatcher implements Dispatcher {
  private Dispatcher realDispatcher;
  private String objectName;
  
  SecureDispatcher(Dispatcher _realDispatcher, String _objectName) {
    realDispatcher = _realDispatcher;
    objectName = _objectName;
  }
  
  public void send(Message message) {
    if (message == null) {
      return;
    }
    assert message.getOrigin().equals(objectName) : "Message origin faked.";
    realDispatcher.send(message);
  }

  public void send(Message[] messageList) {
    if (messageList == null) {
      return;
    }
    for (int i = 0; i < messageList.length; i++) {
      send(messageList[i]);
    }
  }

  public void send(List messageList) {
    if (messageList == null) {
      return;
    }
    Iterator it = messageList.iterator();
    while (it.hasNext()) {
      Message element = (Message) it.next();
      send(element);
    }
  }

  public Message getNewMessage(String action, String destination, String origin, UUID inReplyTo) {
    assert origin.equals(objectName) : "Message origin faked.";
    return realDispatcher.getNewMessage(action, destination, origin, inReplyTo);
  }

  public Message getNewMessage() {
    return realDispatcher.getNewMessage();
  }

  public ObjectManager getObjectManager() {
    return realDispatcher.getObjectManager();
  }

  public ResourceManager getResourceManager() {
    return realDispatcher.getResourceManager();
  }

  public SecurityManager getSecurityManager() {
    return realDispatcher.getSecurityManager();
  }

  public ConfigurationManager getConfigurationManager() {
    return realDispatcher.getConfigurationManager();
  }

  public ExceptionHandler getExceptionHandler() {
    return realDispatcher.getExceptionHandler();
  }

  public void addSecurityManager(SecurityManager additionalSecurityManager) {
    realDispatcher.addSecurityManager(additionalSecurityManager);
  }

  public void addExceptionHandler(ExceptionHandler ex) {
    realDispatcher.addExceptionHandler(ex);
  }

  public void addConfigurationManager(ConfigurationManager cman) {
    realDispatcher.addConfigurationManager(cman);
  }

}
