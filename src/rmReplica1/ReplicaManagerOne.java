package rmReplica1;

import java.io.IOException;
import java.net.*;
import java.util.HashMap;

public class ReplicaManagerOne{

	String nameOfReplica = "RM1";
	private ManagerHelperOne managerHelperOne = new ManagerHelperOne();

	//helper class

	HashMap<String, Integer> replicaPortNumbers = new HashMap<>();

	protected byte[] buf = new byte[1024];
	//protected MulticastSocket socket = null; needed to find bad replica method
	private String currentReplica;

	static int faultCount = 0;

	public  ReplicaManagerOne() {


		replicaPortNumbers.put("ReplicaOne", 7654);
		replicaPortNumbers.put("ReplicaTwo", 7655);
		replicaPortNumbers.put("ReplicaThree", 7656);
		replicaPortNumbers.put("ReplicaFour", 7657);


		currentReplica = "ReplicaOne";

	}



	
	//------------------------------------------------------------------------------s------------------------------------------------ MAIN
	
	public static void main(String args[]) throws Exception {
		ReplicaManagerOne remoteManagerOne = new ReplicaManagerOne();

	  //	String reply = remoteManagerOne.managerHelperOne.forwardToReplica("deposit QCC1000 500",7654);

		//System.out.println(reply);

		Thread t1 = new Thread(() -> {
			try {
				remoteManagerOne.takeRequestFromSequencer();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		});
		
		Thread t2 = new Thread(() -> {
			try {
				remoteManagerOne.getFromFE();
			} catch (Exception e) {
				e.printStackTrace();
			}
		});



		
		t1.start();
		t2.start();
		t2.join();
		t1.join();


	}

	
	//------------------------------------------------------------------------------------------------------------------------------ takeRequestFromSequencer()
	private void takeRequestFromSequencer() throws Exception {

		int mcPort = 5858;
		String mcIPStr = "230.1.1.1";
		MulticastSocket mcSocket;
		InetAddress mcIPAddress;
		mcIPAddress = InetAddress.getByName(mcIPStr);
		mcSocket = new MulticastSocket(mcPort);
		System.out.println("Multicast Receiver running at:" + mcSocket.getLocalSocketAddress());
		mcSocket.joinGroup(mcIPAddress);

		DatagramPacket packet = new DatagramPacket(new byte[1024], 1024);
		
		while (true) {
			System.out.println("Waiting for a  multicast message...");
			mcSocket.receive(packet);




			String received = new String(packet.getData(), packet.getOffset(), packet.getLength());
			System.out.println("Multicast Received:" + received);


			//Get the request from the Sequencer and split it in an array
			String [] request = received.split("-");

			//Get the request ID and the method name
			String requestID = request[0];
			String method = request[1];


			String sendToReplica = "";
			for (int i =1; i<request.length; i++){
				sendToReplica += request[i] + " ";
			}
			if (received.equalsIgnoreCase("RM1"))
			{
				currentReplica = "ReplicaTwo";
				faultCount++;
			}


			System.out.println("Manager name -> " + nameOfReplica);

			//if (replicaPortNumbers.get(currentReplica) == 7655) {

				System.out.println("Using the port#" + replicaPortNumbers.get(currentReplica));

			//}
			//switch port number here incase there is a fail
			String reply;
			if (faultCount >0){

				System.out.println("Inside the if statement");
				 reply = managerHelperOne.forwardToReplica(sendToReplica,7655);


			}else {

				System.out.println("Inside the else statement");
				 reply = managerHelperOne.forwardToReplica(sendToReplica, replicaPortNumbers.get(currentReplica));

			}
			sendReplyToFE(requestID, reply);


			if (nameOfReplica.equals(received)) {
				break;
			}
			
		}
		
	}

	//------------------------------------------------------------------------------------------------------------------------------ getFromFE()

	public void getFromFE()  {

		int mcPort = 5859;
		String mcIPStr = "230.1.1.1";
		MulticastSocket mcSocket = null;
		InetAddress mcIPAddress = null;
		try {
			mcIPAddress = InetAddress.getByName(mcIPStr);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		try {
			mcSocket = new MulticastSocket(mcPort);
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			mcSocket.joinGroup(mcIPAddress);
		} catch (IOException e) {
			e.printStackTrace();
		}

		DatagramPacket packet = new DatagramPacket(new byte[1024], 1024);

		while (true) {

			try {
				mcSocket.receive(packet);
			} catch (IOException e) {
				e.printStackTrace();
			}
			String msg = new String(packet.getData(), packet.getOffset(), packet.getLength());
			System.out.println("Multicast from FE : " + msg);

			//change current replica to replica two
			if (nameOfReplica.equalsIgnoreCase(msg)) {
				System.out.println("Replica switched from one to two");
				this.currentReplica = "ReplicaTwo";
				System.out.println(currentReplica);
				faultCount++;
				this.currentReplica = "Incremented fault count";

				break;
			}


		}
		//mcSocket.leaveGroup(mcIPAddress);
		//mcSocket.close();
	}




	//------------------------------------------------------------------------------------------------------------------------------ sendReplyToFE()
	
	public void sendReplyToFE(String requestID, String replyFromReplicaString) throws IOException {

		DatagramSocket clientSocket = new DatagramSocket();
		InetAddress IPAddress = InetAddress.getByName("localhost");

		byte[] sendData  = new byte[1024];;

		String sentence;
		long startTime = System.currentTimeMillis();
		sentence = requestID + " " + "RM1"+" "+Long.toString(startTime) + " " + replyFromReplicaString;
		
		sendData = sentence.getBytes();
		DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 9878);
		clientSocket.send(sendPacket);
		clientSocket.close();

	}
	
	//------------------------------------------------------------------------------------------------------------------------------ getFromFE()


	public void uu() throws Exception{

		System.out.println("Running RM sererrr");
		DatagramSocket serverSocket = new DatagramSocket(8321);
		byte[] receiveData = new byte[1024];
		byte[] sendData = new byte[1024];
		while(true)
		{
			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
			serverSocket.receive(receivePacket);
			String sentence = new String( receivePacket.getData());
			System.out.println("RECEIVED: " + sentence);
			currentReplica = "ReplicaTwo";
			InetAddress IPAddress = receivePacket.getAddress();
			int port = receivePacket.getPort();
			String capitalizedSentence = sentence.toUpperCase();
			sendData = capitalizedSentence.getBytes();
			DatagramPacket sendPacket =
					new DatagramPacket(sendData, sendData.length, IPAddress, port);
			serverSocket.send(sendPacket);
		}

	}


}
