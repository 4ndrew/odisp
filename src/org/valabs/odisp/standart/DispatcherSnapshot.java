package org.valabs.odisp.standart;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;


/** Класс читающий и сохраняющий состояние диспетчера перед перезагрузкой.
 * @author <a href="mailto:valeks@valabs.spb.ru">Алексеев Валентин А.</a>
 * @version $Id: DispatcherSnapshot.java,v 1.1 2005/01/26 13:23:05 valeks Exp $
 */
class DispatcherSnapshot {
  private static final String SNAP_NAME = "restart.snap";
  private Map objectSnapshots = new HashMap();
  private List messageQueue = new ArrayList();
  private Logger log = Logger.getLogger(DispatcherSnapshot.class.getName());
  /**
   * 
   */
  public DispatcherSnapshot() {
    try {
      ObjectInputStream in = new ObjectInputStream(new FileInputStream(SNAP_NAME));
      objectSnapshots = (Map) in.readObject();
      messageQueue = (List) in.readObject();
      new File(SNAP_NAME).delete();
    } catch (FileNotFoundException e) {
      // нет ножек -- нет мультиков
    } catch (IOException e) {
      log.warning("System restart snapshot exists but it could not be loaded. Starting from scratch.");
    } catch (ClassNotFoundException e) {
      log.warning("System restart snapshot exists but it could not be loaded. Starting from scratch.");
    }
    Runtime.getRuntime().addShutdownHook(new Thread() {
      public void run() {
        if (objectSnapshots.size() > 0) {
          try {
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(SNAP_NAME));
            out.writeObject(objectSnapshots);
            out.writeObject(messageQueue);
            out.close();
            log.info("System snapshot written.");
          } catch (IOException e) {
            log.warning("Unable to write system snapshot!");
          }
        }
      }
    });
  }
  
  public void clearSnapshot() {
    objectSnapshots.clear();
    messageQueue.clear();
  }

  public void addObjectSnapshot(String objectName, Map options) {
    if (options != null) {
      objectSnapshots.put(objectName, options);
      System.err.println("Saving object " + objectName + " into snapshot database ["+ options.size() +" states].");
    }
  }
  
  public void setMessageQueue(List queueMessages) {
    messageQueue.addAll(queueMessages);
  }
  
  public List getMessageQueue() {
    return messageQueue;
  }
  
  public Map getObjectSnapshot(String objectName) {
    return (Map) objectSnapshots.get(objectName);
  }

  /**
   * @return
   */
  public boolean hasSnapshot() {
    return objectSnapshots.size() > 0 || messageQueue.size() > 0;
  }
}
