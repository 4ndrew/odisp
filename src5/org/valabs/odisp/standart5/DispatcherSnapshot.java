package org.valabs.odisp.standart5;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;


/**
 * Класс читающий и сохраняющий состояние диспетчера перед перезагрузкой.
 * 
 * @author <a href="mailto:valeks@valabs.spb.ru">Алексеев Валентин А.</a>
 * @version $Id: DispatcherSnapshot.java,v 1.2 2005/12/24 16:46:37 valeks Exp $
 */
class DispatcherSnapshot {
  private static final String SNAP_NAME = "restart.snap";
  private Map<String, Map> objectSnapshots = new HashMap<String, Map>();
  private final static Logger log = Logger.getLogger(DispatcherSnapshot.class.getName());

  @SuppressWarnings("unchecked")
  public DispatcherSnapshot() {
    try {
      final ObjectInputStream snapFile = new ObjectInputStream(new FileInputStream(SNAP_NAME));
      objectSnapshots = (Map<String, Map>) snapFile.readObject();
      new File(SNAP_NAME).delete();
    } catch (FileNotFoundException e) {
      log.info("No snapshot found. Starting from scratch.");
    } catch (IOException e) {
      log.warning("System restart snapshot exists but it could not be loaded. Starting from scratch.");
    } catch (ClassNotFoundException e) {
      log.warning("System restart snapshot exists but it could not be loaded. Starting from scratch.");
    }
    Runtime.getRuntime().addShutdownHook(new Thread() {
      public final void run() {
        if (objectSnapshots.size() > 0) {
          try {
            final ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(SNAP_NAME));
            out.writeObject(objectSnapshots);
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
  }

  public final void addObjectSnapshot(final String objectName, final Map options) {
    if (options != null) {
      objectSnapshots.put(objectName, options);
      log.info("Saving object " + objectName + " into snapshot database ["+ options.size() +" states].");
    }
  }
    
  public Map getObjectSnapshot(final String objectName) {
    return objectSnapshots.get(objectName);
  }

  /**
   * @return true - снимок существует, false - иначе.
   */
  public boolean hasSnapshot() {
    return objectSnapshots.size() > 0;
  }
}
