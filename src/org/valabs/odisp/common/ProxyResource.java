package com.novel.odisp.common;

import java.lang.reflect.*;

/** ������-������ ��� ������� � ������������ ������� ��������.
* @author �������� �. ��������
* @author (C) 2003, ��� "�����-��"
* @version $Id: ProxyResource.java,v 1.1 2003/10/21 12:35:33 valeks Exp $
*/
public class ProxyResource implements Resource {
        public Object resource;
        public String className;
        private boolean isAlive;
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
