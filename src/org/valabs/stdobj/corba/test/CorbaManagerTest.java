package org.valabs.stdobj.corba.test;

import org.doomdark.uuid.UUID;
import org.valabs.odisp.common.Message;
import org.valabs.odisp.common.MessageHandler;
import org.valabs.odisp.common.StandartODObject;
import org.valabs.stdmsg.ODObjectLoadedMessage;
import org.valabs.stdmsg.corba.CorbaAddServiceMessage;

/** Тестовый объект, который работает с CorbaManager.
 * При запуске регистрируется сервис HelloWorld.
 * @author <a href="mailto:valeks@valabs.spb.ru">Алексеев Валентин А.</a>
 * @version $Id: CorbaManagerTest.java,v 1.1 2005/08/11 21:45:11 valeks Exp $
 */
public class CorbaManagerTest extends StandartODObject {

    public CorbaManagerTest() {
        super("corbamanager-test", "Test CORBA Manager functionality", "0.1.0", "(C) Valentin A. Alekseev");
    }
    
    protected void registerHandlers() {
        addHandler(ODObjectLoadedMessage.NAME, new MessageHandler() {
            public void messageReceived(Message msg) {
                Message m = dispatcher.getNewMessage();
                CorbaAddServiceMessage.setup(m, "corbamanager", getObjectName(), UUID.getNullUUID());
                CorbaAddServiceMessage.initAll(m, "HelloWorld", new HelloWorldImpl());
                dispatcher.send(m);
            }
        });
    }
    
    public String[] getDepends() {
        String[] depends = {"corbamanager", "dispatcher", };
        return depends;
    }

    public String[] getProviding() {
        String[] providing = { "corbamanager-test", };
        return providing;
    }

}
