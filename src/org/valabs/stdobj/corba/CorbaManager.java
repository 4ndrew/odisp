/* ODISP -- Message Oriented Middleware
 * Copyright (C) 2003-2005 Valentin A. Alekseev
 * Copyright (C) 2003-2005 Andrew A. Porohin 
 * 
 * ODISP is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, version 2.1 of the License.
 * 
 * ODISP is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with ODISP.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.valabs.stdobj.corba;

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
import org.valabs.odisp.common.Message;
import org.valabs.odisp.common.MessageHandler;
import org.valabs.odisp.common.StandartODObject;
import org.valabs.stdmsg.ODObjectLoadedMessage;
import org.valabs.stdmsg.corba.CorbaAddServiceMessage;

/** �������� ������ CORBA ����������, ������� �������� ��
 * ������������� �������� � ������ ���� ����������� 
 * ��������� ���������.
 * @author <a href="mailto:valeks@valabs.spb.ru">�������� �������� �.</a>
 * @version $Id: CorbaManager.java,v 1.2 2005/08/12 09:55:21 valeks Exp $
 */
public class CorbaManager extends StandartODObject implements MessageHandler {

    public static final String NAME = "corbamanager";
    public static final String FULLNAME = "ODISP-CORBA Interface";
    public static final String VERSION = "0.1.0";
    public static final String COPYRIGHT = "(C) 2005 Valentin A. Alekseev";
    
    private CorbaORB orb;
    
    protected void registerHandlers() {
        addHandler(CorbaAddServiceMessage.NAME, this);
        addHandler(ODObjectLoadedMessage.NAME, this);
    }

    public void messageReceived(Message msg) {
        if (ODObjectLoadedMessage.equals(msg)) {
        	orb = new CorbaORB();
        } else if (CorbaAddServiceMessage.equals(msg)) {
            String serviceName = CorbaAddServiceMessage.getServiceName(msg);
            Servant serviceImpl = CorbaAddServiceMessage.getServiceImpl(msg);
            orb.addService(serviceName, serviceImpl);
        }
    }
    
    public int cleanUp(int type) {
    	if (orb != null) {
    		orb.shutdown();
    	}
    	return type;
    }
    
    class CorbaORB extends Thread {
        private ORB anOrb;
        private NamingContextExt ncRef;
        private POA rootpoa;

    	public CorbaORB() {
    		super("Corba Manager CorbaORB");
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
            this.start();
    	}
    	
    	public void run() {
    		anOrb.run();
    	}
    	
    	public synchronized void shutdown() {
    		logger.fine("Shutting down CorbaORB");
    		anOrb.shutdown(true);
    	}
    	
    	public synchronized void addService(String serviceName, Servant serviceImpl) {
            try {
                NameComponent path[] = ncRef.to_name(serviceName);
                ncRef.rebind(path, serviceImpl._this_object(anOrb));
                logger.config("Added CORBA service " + serviceName);
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
