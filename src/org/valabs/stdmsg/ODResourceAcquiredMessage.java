package com.novel.stdmsg;

import com.novel.odisp.*;
import com.novel.odisp.common.*;

/**
 * @author <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @author (C) 2003, îðð "îÏ×ÅÌ-éì"
 * @version $Id: ODResourceAcquiredMessage.java,v 1.1 2003/12/01 22:19:29 valeks Exp $
 */

public class ODResourceAcquiredMessage extends StandartMessage {
  public ODResourceAcquiredMessage(String destination, int replyId) {
    super("od_resource_acquired", destination, "stddispatcher", replyId);
  }

  public String getClassName() {
    return getField(0);
  }

  public ODResourceAcquiredMessage setClassName(String newClassName) {
    fields.set(0, newClassName);
    return this;
  }


  public Resource getResource() {
    return (Resource) getField(1);
  }
  
  public ODResourceAcquiredMessage setResource(Resource newResource) {
    fields.set(1, newResource);
    return this;
  }

}// ODResourceAcquiredMessage
