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
package org.valabs.stdobj.updater;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.valabs.odisp.common.Message;
import org.valabs.odisp.common.MessageHandler;
import org.valabs.odisp.common.StandartODObject;
import org.valabs.stdmsg.ODObjectLoadedMessage;
import org.valabs.stdmsg.ODShutdownMessage;
import org.valabs.stdmsg.updater.UpdaterFireUpdateErrorMessage;
import org.valabs.stdmsg.updater.UpdaterFireUpdateMessage;

/**
 * Компонент поддержки обновлений.
 * @author <a href="mailto:valeks@valabs.spb.ru">Алексеев Валентин А.</a>
 * @version $Id: Updater.java,v 1.13 2006/02/20 11:52:25 valeks Exp $
 */
public class Updater extends StandartODObject implements MessageHandler {
  private static final String NAME = "updater";

  private static final String FULLNAME = "Update support";

  private static final String VERSION = "0.1.1";

  private static final String COPYRIGHT = "(C) 2005 Valentin A. Alekseev";

  private String updateBaseFolder;

  private String destBaseFolder;

  /**
   * Ищет присутствие хоть какого ни будь файла в заданном каталоге или его подкаталогах.
   * @param folder каталог
   * @return список найденых файлов
   */
  private List checkForUpdate(final File folder) {
    List result;
    if (!folder.isDirectory()) {
      result = null;
    } else {
      result = new LinkedList();
      String[] files = folder.list();
      for (int i = 0; i < files.length; i++) {
        File item = new File(folder.getAbsolutePath() + File.separator + files[i]);
        if (item.isDirectory()) {
          List res = checkForUpdate(item);
          if (res != null) {
            result.addAll(res);
          }
        } else {
          result.add(item);
        }
      }
    }
    return result;
  }
  
  /**
   * Произвести обновление из каталога updatebase в destbase.
   * Каталоги создаются в случае необходимости
   * @param filesToMove список файлов для перемещения
   * @return true если перемещение файлов завершилось успешно
   */
  private boolean doUpdate(final List filesToMove) {
    boolean result = true;
    Iterator it = filesToMove.iterator();
    while (it.hasNext()) {
      File fileToMove = (File) it.next();
      File moveTo = new File(fileToMove.getPath().replace(updateBaseFolder, destBaseFolder));
      File parentF = moveTo.getParentFile();
      if (parentF != null) {
        parentF.mkdirs();
      }
      result &= fileToMove.renameTo(moveTo);
    }
    return result;
  }

  /**
   * Конструктор.
   */
  public Updater() {
    super(NAME, FULLNAME, VERSION, COPYRIGHT);
  }

  public void registerHandlers() {
    addHandler(UpdaterFireUpdateMessage.NAME, this);
    addHandler(ODObjectLoadedMessage.NAME, this);
  }

  /*
   * (non-Javadoc)
   * @see org.valabs.odisp.common.MessageHandler#messageReceived(org.valabs.odisp.common.Message)
   */
  public void messageReceived(Message msg) {
    if (ODObjectLoadedMessage.equals(msg)) {
      updateBaseFolder = getParameter("updatebase", "update");
      destBaseFolder = getParameter("destbase", ".");
    } else if (UpdaterFireUpdateMessage.equals(msg)) {
      String security = (String) msg.getField("security");
      if (!dispatcher.getSecurityManager().checkAccess(security, "restart", null)) {
        dispatcher.getSecurityManager().audit(security, "restart", false, "noaccess");
        // TODO добавить отправку сообщения об ошибке
        return;
      }
      logger.info("Checking for components update...");
      List res = checkForUpdate(new File(updateBaseFolder));
      if (res != null && res.size() > 0) {
        if (doUpdate(res)) {
          logger.info("Updates avaliable. Restarting");
          Message m = dispatcher.getNewMessage();
          ODShutdownMessage.setup(m, getObjectName(), msg.getId());
          ODShutdownMessage.setExitCode(m, new Integer(-1));
          dispatcher.send(m);
        } else {
          Message m = dispatcher.getNewMessage();
          UpdaterFireUpdateErrorMessage.setup(m, msg.getOrigin(), getObjectName(), msg.getId());
          UpdaterFireUpdateErrorMessage.initAll(m, "updater.badupdates");
          dispatcher.send(m);          
        }
      } else {
        Message m = dispatcher.getNewMessage();
        UpdaterFireUpdateErrorMessage.setup(m, msg.getOrigin(), getObjectName(), msg.getId());
        UpdaterFireUpdateErrorMessage.initAll(m, "updater.noupdates");
        dispatcher.send(m);
      }
    }
  }

  /*
   * (non-Javadoc)
   * @see org.valabs.odisp.common.ODObject#getDepends()
   */
  public String[] getDepends() {
    String[] depends = {
      "dispatcher",
    };
    return depends;
  }

  /*
   * (non-Javadoc)
   * @see org.valabs.odisp.common.ODObject#getProviding()
   */
  public String[] getProviding() {
    String[] providing = {
      NAME,
    };
    return providing;
  }

}
