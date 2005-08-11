package org.valabs.stdobj.corba;

import java.util.HashMap;
import java.util.Map;

import org.omg.CORBA.ORB;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.NotFound;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;
import org.omg.PortableServer.Servant;
import org.omg.PortableServer.POAManagerPackage.AdapterInactive;
import org.omg.PortableServer.POAPackage.ServantNotActive;
import org.omg.PortableServer.POAPackage.WrongPolicy;
import org.valabs.odisp.common.Message;
import org.valabs.odisp.common.MessageHandler;
import org.valabs.odisp.common.StandartODObject;
import org.valabs.stdmsg.ODObjectLoadedMessage;
import org.valabs.stdmsg.corba.CorbaAddServiceMessage;

/** Основной объект CORBA интерфейса, который отвечает за
 * инициализацию сервисов и запуск всех необходимых 
 * серверных процессов.
 * @author <a href="mailto:valeks@valabs.spb.ru">Алексеев Валентин А.</a>
 * @version $Id: CorbaManager.java,v 1.1 2005/08/11 21:45:11 valeks Exp $
 */
public class CorbaManager extends StandartODObject implements MessageHandler {

    public static final String NAME = "corbamanager";
    public static final String FULLNAME = "ODISP-CORBA Interface";
    public static final String VERSION = "0.1.0";
    public static final String COPYRIGHT = "(C) 2005 Valentin A. Alekseev";
    
    private Map services = new HashMap();
    private ORB anOrb;
    private NamingContextExt ncRef;
    private POA rootpoa;
    
    protected void registerHandlers() {
        addHandler(CorbaAddServiceMessage.NAME, this);
        addHandler(ODObjectLoadedMessage.NAME, this);
    }

    public void messageReceived(Message msg) {
        if (ODObjectLoadedMessage.equals(msg)) {
            String orbArgs[] = { "-ORBInitialPort", "50000", };
            // create and initialize the ORB
            anOrb = ORB.init(orbArgs, null);

            try {
            // get reference to rootpoa & activate the POAManager
            rootpoa = POAHelper.narrow(anOrb
                    .resolve_initial_references("RootPOA"));
            rootpoa.the_POAManager().activate();
            // get the root naming context
            org.omg.CORBA.Object objRef = anOrb
                    .resolve_initial_references("NameService");
            // Use NamingContextExt which is part of the Interoperable
            // Naming Service (INS) specification.
            ncRef = NamingContextExtHelper.narrow(objRef);
            } catch (InvalidName e) {
                dispatcher.getExceptionHandler().signalException(e);
            } catch (AdapterInactive e) {
                dispatcher.getExceptionHandler().signalException(e);
            }
        } else if (CorbaAddServiceMessage.equals(msg)) {
            String serviceName = CorbaAddServiceMessage.getServiceName(msg);
            Servant serviceImpl = CorbaAddServiceMessage.getServiceImpl(msg);
            try {
                org.omg.CORBA.Object ref = rootpoa.servant_to_reference(serviceImpl);
                NameComponent path[] = ncRef.to_name(serviceName);
                ncRef.rebind(path, serviceImpl._this_object(anOrb));
                logger.config("Added CORBA service " + serviceName);
            } catch (ServantNotActive e) {
                dispatcher.getExceptionHandler().signalException(e);
            } catch (WrongPolicy e) {
                dispatcher.getExceptionHandler().signalException(e);
            } catch (org.omg.CosNaming.NamingContextPackage.InvalidName e) {
                dispatcher.getExceptionHandler().signalException(e);
            } catch (NotFound e) {
                dispatcher.getExceptionHandler().signalException(e);
            } catch (CannotProceed e) {
                dispatcher.getExceptionHandler().signalException(e);
            }
        }
    }
    
    public CorbaManager() {
        super(NAME, FULLNAME, VERSION, COPYRIGHT);
    }
    
    public String[] getDepends() {
        String[] result = { "dispatcher", };
        return result;
    }

    public String[] getProviding() {
        String[] providing = { NAME, };
        return providing;
    }

}
