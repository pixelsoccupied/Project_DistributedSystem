package frontEnd;
// tnameserv -ORBInitialPort 1050
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import org.omg.CORBA.ORB;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;

import bankingOperationsApp.BankingOperations;
import bankingOperationsApp.BankingOperationsHelper;

public class FElistener {
	
	FE fe = new FE();
	
	//------------------------------------------------------------------------------------------------------------------------------ CORBA()
    private void CORBA() {

        String [] args = {"-ORBInitialPort", "1050" , "-ORBInitialHost",  "localhost"};

        try{
            // create and initialize the ORB //// get reference to rootpoa &amp; activate the POAManager
            ORB orb = ORB.init(args, null);

            POA rootpoa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
            rootpoa.the_POAManager().activate();

            // create servant and register it with the ORB
            fe.setORB(orb);

            // get object reference from the servant
            org.omg.CORBA.Object ref = rootpoa.servant_to_reference(fe);

            //Get the stub from AdditionHelper
            BankingOperations href = BankingOperationsHelper.narrow(ref);

            //  implementation repository stuff
            // activates the server with the name provided below
            org.omg.CORBA.Object objRef =  orb.resolve_initial_references("NameService");
            NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);

            NameComponent path[] = ncRef.to_name( "FE" );
            ncRef.rebind(path, href);

            System.out.println("FE Server ready and waiting ...");

            // wait for invocations from clients
            while (true) orb.run();
        }

        catch (Exception e) {
            System.err.println("ERROR: " + e);
            e.printStackTrace(System.out);
        }

        System.out.println("HelloServer Exiting ...");
    }

    
	//------------------------------------------------------------------------------------------------------------------------------ MAIN
	public static void main(String[] args) throws IOException {
	
		FElistener felistener = new FElistener();
//		Thread t1 = new Thread(()->{
//			try {
//				felistener.UDP();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		});
		Thread t2 = new Thread(()->{
			felistener.CORBA();
		});

		
		//t1.start();
		t2.start();
		
		
		try {
			//t1.join();
			t2.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}