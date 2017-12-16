package frontEnd;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

public class Sequencer {

    private static int ID = 1000 ;
    private DatagramSocket socket;
    private InetAddress group;
    private byte[] buf;
    private Queue<String> requestSent = new LinkedList<>();


    public static void main(String args[]) {


    }

    public void multicast(String multicastMessage) throws IOException {

    	int mcPort = 5858;
	    String mcIPStr = "230.1.1.1";
	    DatagramSocket udpSocket = new DatagramSocket();

	    InetAddress mcIPAddress = InetAddress.getByName(mcIPStr);
	    byte[] msg = multicastMessage.getBytes();
	    DatagramPacket packet = new DatagramPacket(msg, msg.length);
	    packet.setAddress(mcIPAddress);
	    packet.setPort(mcPort);
	    udpSocket.send(packet);

	    System.out.println("Sent a  multicast message.");
	    System.out.println("Exiting application");
	    udpSocket.close();


    }

	//------------------------------------------------------------------------------------------------------------------------------ sendRequestToRM()
    public String sendRequestToRM(String nameOfTheMethod, String [] args) throws IOException {
    	
        String sendThisReq = nameOfTheMethod;
        for (int i =0; i <args.length; i++){
            sendThisReq +="-"+args[i];                        
        }
        
        sendThisReq = generateID(sendThisReq);

        multicast(sendThisReq);
        return sendThisReq;
    }


   //------------------------------------------------------------------------------------------------------------------------------ generateID()
    private String generateID(String req) {
        String iDString = Integer.toString(ID);
        req =  iDString + "-" + req;
        //add the ID to the QUEUE
        requestSent.add(iDString);
        ID++;
        return req;
    }
}
