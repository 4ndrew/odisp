package org.valabs.stdobj.corba.test;

/** ���������� ��������� CORBA-�������.
 * @author <a href="mailto:valeks@valabs.spb.ru">�������� �������� �.</a>
 * @version $Id: HelloWorldImpl.java,v 1.1 2005/08/11 21:45:11 valeks Exp $
 */
public class HelloWorldImpl extends HelloWorldPOA {

    public void sayHello(String toWhom) {
        System.err.println(makeHello(toWhom));
    }

    public String makeHello(String toWhom) {
        return "Hello, " + toWhom + "!";
    }

}
