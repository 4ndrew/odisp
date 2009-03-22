package org.valabs.stdobj.bugtrack;

import java.util.Map;

/** Интерфейс объектов реализующих запись/отправку сообщения об ошибке.
 * @author valeks
 * @version $Id: AbstractBugWriter.java,v 1.1 2005/02/27 12:37:30 valeks Exp $
 */
abstract class AbstractBugWriter {
  protected Map config;
  public AbstractBugWriter(Map configuration) {
    config = configuration;
  }
  
  public abstract void writeBugReport(final String id, final String pc, final String ai, final SystemSnapshot ss);
}
