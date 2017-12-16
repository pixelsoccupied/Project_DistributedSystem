package rmReplica3;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class ReplicaManagerThree{

	String nameOfReplica = "RM3";
	private ManagerHelperThree managerHelperOne = new ManagerHelperThree();

	//helper class
	//ManagerHelperOne managerHelperOne = new ManagerHelperOne();

	HashMap<String, Integer> replicaPortNumbers = new HashMap<>();

	protected byte[] buf = new byte[1024];
	//protected MulticastSocket socket = null; needed to find bad replica method
	String currentReplica;

	public  ReplicaManagerThree() {


		replicaPortNumbers.put("ReplicaOne", 7654);
		replicaPortNumbers.put("ReplicaTwo", 7655);
		replicaPortNumbers.put("ReplicaThree", 7656);
		replicaPortNumbers.put("ReplicaFour", 7657);


		currentReplica = "ReplicaThree";

	}




	//------------------------------------------------------------------------------------------------------------------------------ MAIN

	public static void main(String args[]) throws Exception {
		ReplicaManagerThree remoteManagerThree = new ReplicaManagerThree();

		//	String reply = remoteManagerThree.managerHelperOne.forwardToReplica("deposit QCC1000 500",7654);

		//System.out.println(reply);

		Thread t1 = new Thread(() -> {
			try {
				remoteManagerThree.takeRequestFromSequencer();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		});

		Thread t2 = new Thread(()-> {
			try {
				remoteManagerThree.getFromFE();
			} catch (IOException e) {
				// TODO Auto-generated catch block
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




			//switch port number here incase there is a fail
			String reply = managerHelperOne.forwardToReplica(sendToReplica,replicaPortNumbers.get(currentReplica));
			sendReplyToFE(requestID, reply);


			if (nameOfReplica.equals(received)) {
				break;
			}

		}

	}



	//------------------------------------------------------------------------------------------------------------------------------ sendReplyToFE()

	public void sendReplyToFE(String requestID, String replyFromReplicaString) throws SocketException, IOException {

		DatagramSocket clientSocket = new DatagramSocket();
		InetAddress IPAddress = InetAddress.getByName("localhost");

		byte[] sendData  = new byte[1024];;

		String sentence;
		long startTime = System.currentTimeMillis();
		sentence = requestID + " " + "RM3"+" "+Long.toString(startTime) + " " + replyFromReplicaString;

		sendData = sentence.getBytes();
		DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 9878);
		clientSocket.send(sendPacket);
		clientSocket.close();

	}

	//------------------------------------------------------------------------------------------------------------------------------ getFromFE()

	public void getFromFE() throws IOException {

		int mcPort = 5859;
		String mcIPStr = "230.1.1.1";
		MulticastSocket mcSocket = null;
		InetAddress mcIPAddress = null;
		mcIPAddress = InetAddress.getByName(mcIPStr);
		mcSocket = new MulticastSocket(mcPort);
		mcSocket.joinGroup(mcIPAddress);

		DatagramPacket packet = new DatagramPacket(new byte[1024], 1024);

		while (true) {

			mcSocket.receive(packet);
			String msg = new String(packet.getData(), packet.getOffset(), packet.getLength());
			System.out.println("[ Multicast from FE ] : " + msg);

			//change current replica to replica two
			if (nameOfReplica.equalsIgnoreCase(msg)) {
				currentReplica = "ReplicaTwo";
			}

		}

	}


}
