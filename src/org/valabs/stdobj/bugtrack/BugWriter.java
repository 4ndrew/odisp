package org.valabs.stdobj.bugtrack;

import java.util.Map;

/** Интерфейс объектов реализующих запись/отправку сообщения об ошибке.
 * @author valeks
 * @version $Id: BugWriter.java,v 1.1 2005/01/11 20:37:59 valeks Exp $
 */
abstract class BugWriter {
  protected Map config;
  public BugWriter(Map configuration) {
    config = configuration;
  }
  
  public abstract void writeBugReport(String id, String pc, String ai, SystemSnapshot ss);
}
