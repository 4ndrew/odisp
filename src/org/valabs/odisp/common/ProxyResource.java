package com.novel.odisp.common;

import java.lang.reflect.*;

/** Прокси-ресурс для доступа к произвольным внешним объектам.
* @author Валентин А. Алексеев
* @author (C) 2003, НПП "Новел-ИЛ"
* @version $Id: ProxyResource.java,v 1.1 2003/10/21 12:35:33 valeks Exp $
*/
public class ProxyResource implements Resource {
        public Object resource;
        public String className;
        private boolean isAlive;
	/** Максимальное количество ссылок одновременно поддерживаемых объектом */
	public int getMaxReferenceCount(){return 0;};
	/** Вызывается при необходимости очистить ресурсы */
	public int cleanUp(int type){return 0;};
        public void setResource(String className){
                this.className = className;
        }
        public boolean isAlive(){return isAlive;}
        public Object newInstance(Class[] declParams, Object[] params){
                try {
                        resource = (Object)Class.forName(className).getConstructor(declParams).newInstance(params);
                        isAlive = true;
                } catch(InvocationTargetException e){
                        System.err.println(" failed: "+e);
                } catch(NoSuchMethodException e){
                        System.err.println(" failed: "+e);
                } catch(ClassNotFoundException e){
                        System.err.println(" failed: "+e);
                } catch(InstantiationException e){
                        System.err.println(" failed: "+e);
                } catch(IllegalAccessException e){
                        System.err.println(" failed: "+e);
                } catch(IllegalArgumentException e){
                        System.err.println(" failed: "+e);
                }
                return resource;
        }
}
