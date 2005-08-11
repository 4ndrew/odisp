package org.valabs.stdobj.corba.test;

/** Реализация тестового CORBA-сервиса.
 * @author <a href="mailto:valeks@valabs.spb.ru">Алексеев Валентин А.</a>
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
