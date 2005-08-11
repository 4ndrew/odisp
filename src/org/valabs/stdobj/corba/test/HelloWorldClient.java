package org.valabs.stdobj.corba.test;

import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;

/** Тестовый клиент для тестового-же объекта моста ODISP-CORBA.
 * @author <a href="mailto:valeks@valabs.spb.ru">Алексеев Валентин А.</a>
 * @version $Id: HelloWorldClient.java,v 1.1 2005/08/11 21:45:11 valeks Exp $
 */
public class HelloWorldClient {

    public static void main(String[] args) {
        try {
            String orbArgs[] = { "-ORBInitialPort", "50000", };
            // create and initialize the ORB
            ORB orb = ORB.init(orbArgs, null);

            // get the root naming context
            org.omg.CORBA.Object objRef = orb
                    .resolve_initial_references("NameService");
            // Use NamingContextExt instead of NamingContext. This is
            // part of the Interoperable naming Service.
            NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);

            // resolve the Object Reference in Naming
            String name = "HelloWorld";
            HelloWorld helloImpl = HelloWorldHelper.narrow(ncRef
                    .resolve_str(name));

            System.out.println("Obtained a handle on server object: "
                    + helloImpl);
            System.out.println(helloImpl.makeHello("World"));
            helloImpl.sayHello("Console");
            //            helloImpl.shutdown();

        } catch (Exception e) {
            System.out.println("ERROR : " + e);
            e.printStackTrace(System.out);
        }
    }
}