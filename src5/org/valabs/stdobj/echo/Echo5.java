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
package org.valabs.stdobj.echo;

import java.util.Collections;

import org.valabs.odisp.common.Message;
import org.valabs.odisp.common.ODISPMessageHandler;
import org.valabs.odisp.common.StandartODObject5;
import org.valabs.stdmsg.ModuleStatusMessage;
import org.valabs.stdmsg.ModuleStatusReplyMessage;
import org.valabs.stdmsg.ODObjectLoadedMessage;

/** Тестовый объект.
 * @author (C) <a href="mailto:valeks@valabs.spb.ru">Алексеев Валентин А.</a>, 2005
 * @version $Id: Echo5.java,v 1.3 2006/01/31 11:00:24 valeks Exp $
 */
public class Echo5 extends StandartODObject5 {

  public Echo5() {
    super("echo5", "Echo object for Java 1.5 version of ODISP", "0.1.0", "(C) Valentin A. Alekseev, 2005", Echo5.class);
    setInstance(this);
  }
  
  @ODISPMessageHandler("echo")
  public void handleEchoMessage(Message m) {
    logger.fine("Received: " + m.toString(true));
  }
  
  @ODISPMessageHandler(ODObjectLoadedMessage.NAME)
  public void handleODObjectLoaded() {
    logger.fine("Object loaded!");
  }
  
  @ODISPMessageHandler(value="echo_mapping", mapping={"text"})
  public void handleEchoWithMapping(final Message msg, String someText) {
    logger.fine(msg.getOrigin() + " said: " + someText);
  }
  
  
  @ODISPMessageHandler(ModuleStatusMessage.NAME)
  public void handleModuleStatus(final Message msg) {
    Message m = dispatcher.getNewMessage();
    ModuleStatusReplyMessage.setup(m, msg.getOrigin(), getObjectName(), msg.getId());
    ModuleStatusReplyMessage.initAll(m, "Echo5 works ok!", Collections.EMPTY_LIST, Collections.EMPTY_LIST, Collections.EMPTY_LIST);
    dispatcher.send(m);
  }
  
  /**
   * @see org.valabs.odisp.common.ODObject#getDepends()
   */
  public String[] getDepends() {
    return new String[] {
        "dispatcher",
    };
  }

  /**
   * @see org.valabs.odisp.common.ODObject#getProviding()
   */
  public String[] getProviding() {
    return new String[] {
      "echo5",
    };
  }
}
