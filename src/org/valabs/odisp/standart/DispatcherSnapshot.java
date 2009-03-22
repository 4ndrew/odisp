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
package org.valabs.odisp.standart;

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
 * @version $Id: DispatcherSnapshot.java,v 1.6 2005/11/25 00:11:45 valeks Exp $
 */
class DispatcherSnapshot {
  private static final String SNAP_NAME = "restart.snap";
  private Map objectSnapshots = new HashMap();
  private final static Logger log = Logger.getLogger(DispatcherSnapshot.class.getName());
  /**
   * 
   */
  public DispatcherSnapshot() {
    try {
      final ObjectInputStream snapFile = new ObjectInputStream(new FileInputStream(SNAP_NAME));
      objectSnapshots = (Map) snapFile.readObject();
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
    return (Map) objectSnapshots.get(objectName);
  }

  /**
   * @return true - снимок существует, false - иначе.
   */
  public boolean hasSnapshot() {
    return objectSnapshots.size() > 0;
  }
}
