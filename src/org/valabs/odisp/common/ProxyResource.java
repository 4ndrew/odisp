package com.novel.odisp.common;

import java.lang.reflect.*;
import java.util.logging.*;

/** ������-������ ��� ������� � ������������ ������� ��������.
* @author �������� �. ��������
* @author (C) 2003, ��� "�����-��"
* @version $Id: ProxyResource.java,v 1.2 2003/10/22 21:21:33 valeks Exp $
*/
public class ProxyResource implements Resource {
        public Object resource;
        public String className;
        private boolean isAlive;
	private static Logger logger = Logger.getLogger("proxyresource");
	/** ������������ ���������� ������ ������������ �������������� �������� */
	public int getMaxReferenceCount(){return 0;};
	/** ���������� ��� ������������� �������� ������� */
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
                        logger.warning("failed: "+e);
                } catch(NoSuchMethodException e){
                        logger.warning("failed: "+e);
                } catch(ClassNotFoundException e){
                	logger.warning("failed: "+e);
                } catch(InstantiationException e){
                        logger.warning("failed: "+e);
                } catch(IllegalAccessException e){
                        logger.warning("failed: "+e);
                } catch(IllegalArgumentException e){
                        logger.warning("failed: "+e);
                }
                return resource;
        }
}
