package com.novel.odisp.common;

import java.util.Map;

/** Интерфейс менеджера ресурсов ODISP.
 * @author (C) 2004 <a href="mailto:valeks@valeks.novel.local">Valentin A. Alekseev</a>
 * @version $Id: ResourceManager.java,v 1.1 2004/02/13 12:11:43 valeks Exp $
 */

public interface ResourceManager {
  public void loadResource(String className, int mult, String param);
  public void unloadResource(String name, int code);
  public Map getResources();
}// ResourceManager
