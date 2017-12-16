package frontEnd;

//NOT USING
import bankingOperationsApp.BankingOperations;
import bankingOperationsApp.BankingOperationsHelper;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class FEServer {

    FE fe = new FE();

	//------------------------------------------------------------------------------------------------------------------------------ getMsgFromRM()
    public void getMsgFromRM() throws IOException {

        int timeoutValue = 1000;

        System.out.println("Listening now");

        DatagramSocket serverSocket = null;
        try {
            serverSocket = new DatagramSocket(9876);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        byte[] receiveData = new byte[1024];
        byte[] sendData = new byte[1024];
        while(true)
        {
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            try {
                serverSocket.receive(receivePacket);
                serverSocket.setSoTimeout(timeoutValue);

                String sentence = new String( receivePacket.getData());
                sentence = sentence.trim();
                long serversTime = System.currentTimeMillis();
                String [] msgFromRM = sentence.split("\\s");
                long timeOnClientSide = Long.parseLong(msgFromRM[1]);
                long timeTakenToReach = serversTime-timeOnClientSide;

                //convert timeTaken to reach to int for timeOUT
                int timeTakenToReachInInt = Integer.parseInt(String.valueOf(timeTakenToReach));

                //set this time to default timeout value if the time take is higher than default value
                if (timeoutValue < timeTakenToReachInInt) {
                    System.out.println("Time value changed" + timeoutValue + "ms" + " to " + (timeTakenToReach *1000*2));
                    timeoutValue = timeTakenToReachInInt * 1000 * 2;
                }

                //change the time value if the current more than default

             //   timeOutValue =


                System.out.println("RECEIVED: " + msgFromRM[0]);
                System.out.println("Time take to reach - " + timeTakenToReach);


            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    }


	//------------------------------------------------------------------------------------------------------------------------------ CORBA
    private void startCorba() {
    	
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
    public static void main(String args[]) {


        FEServer feServer = new FEServer();
        //feServer.startCorba();

        Thread thread  = new Thread(() -> {
            try {
                feServer.getMsgFromRM();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        thread.start();

        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }


}
