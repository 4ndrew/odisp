package com.novel.odisp;

import com.novel.odisp.common.Resource;
import com.novel.odisp.common.ProxyResource;
import com.novel.odisp.common.Dispatcher;
import com.novel.odisp.common.ResourceManager;
import com.novel.odisp.common.ObjectManager;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Logger;

/** �������� ��������� �������� ODISP.
 * @author (C) 2004 <a href="mailto:valeks@valeks.novel.local">Valentin A. Alekseev</a>
 * @version $Id: ResourceManager.java,v 1.1 2004/02/13 12:11:43 valeks Exp $
 */
public class StandartResourceManager implements ResourceManager {
  /** ������ �� ��������� ��������. */
  private Dispatcher dispatcher;
  /** ������. */
  private Logger log = Logger.getLogger("com.novel.odisp.ResourceManager");
  /** ������ ��������. */
  private Map resources = new HashMap();

  /** ������ � ��������.
   * @return ������ ��������
   */
  public Map getResources() {
    return resources;
  }

  /** ������������ �������� ��������� ��������.
   * @param className ��� ������������ ������
   * @param mult ���������� ����������� ��������
   * @param param �������� ��������
   */
  public void loadResource(final String className, final int mult, final String param) {
    String logMessage = "loading resource " + className;
    for (int i = 0; i < mult; i++) {
      try {
	Resource r = (Resource) Class.forName(className).newInstance();
	ResourceEntry re = new ResourceEntry(className);
	re.setResource(r);
	log.fine("r instanceof ProxyResource " + (r instanceof ProxyResource)
		 + " r.className:" + r.getClass().getName());
	if (r instanceof ProxyResource) {
	  ((ProxyResource) r).setResource(param);
	  resources.put(param + ":" + i, re);
	} else {
	  resources.put(className + ":" + i, re);
	}
	logMessage += "+";
      } catch (ClassNotFoundException e) {
	log.warning(" failed: " + e);
      } catch (InstantiationException e) {
	log.warning(" failed: " + e);
      } catch (IllegalAccessException e) {
	log.warning(" failed: " + e);
      }
    }
    logMessage += " ok.";
    log.config(logMessage);
  }

  /** �������� ���������� �������.
   * @param name ��� ���������� �������
   * @param code ��� ������
   */
  public void unloadResource(final String name, final int code) {
    if (resources.containsKey(name)) {
      ResourceEntry res = (ResourceEntry) resources.get(name);
      List dependingObjs = new ArrayList();
      Iterator it = dispatcher.getObjectManager().getObjects().keySet().iterator();
      while (it.hasNext()) {
	String className = (String) it.next();
	String[] depends = ((ObjectEntry) dispatcher.getObjectManager().getObjects().get(className)).getDepends();
	for (int i = 0; i < depends.length; i++) {
	  if (depends[i].equals(name.substring(0, name.length()
					       - name.indexOf(":")))
	      && !dependingObjs.contains(name)) {
	    dependingObjs.add(className);
	  }
	}
      }
      if (code == 0) {
	it = dependingObjs.iterator();
	while (it.hasNext()) {
	  dispatcher.getObjectManager().unloadObject((String) it.next(), code);
	}
      }
      res.getResource().cleanUp(code);
      resources.remove(name);
    }
  }
  /** ����������� ��������� ��������.
   * @param newDispatcher ������ �� ��������� ��������� �������� ��������� ��������
   */
  public StandartResourceManager(Dispatcher newDispatcher) {
    dispatcher = newDispatcher;
  }
  
}// ResourceManager
