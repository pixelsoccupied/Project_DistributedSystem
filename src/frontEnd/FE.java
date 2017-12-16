package frontEnd;

import bankingOperationsApp.BankingOperationsPOA;
import org.omg.CORBA.ORB;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class FE extends BankingOperationsPOA{

	//------------------------------------------------------------------------------------------------------------------------------ INSTANCE VARIABLES

	private boolean  socketIsOpen = false;
	private DatagramSocket serverSocket = null;
	private ORB ORB;
	private static int port = 5860;
	private Sequencer sequencer = new Sequencer();
	private HashMap<String, Boolean> activeRMs = new HashMap<>();             //keep count of the active RM
	private HashMap<String, ArrayList<String>> rmReplies = new HashMap<>();   //keep track of replies

	static int numOfRMs = 0;




	//------------------------------------------------------------------------------------------------------------------------------ CONSTRUCTOR

	public FE() {

		activeRMs.put("RM1" , false);
		activeRMs.put("RM2" , false);
		activeRMs.put("RM3" , false);
		activeRMs.put("RM4" , false);
	}


	//------------------------------------------------------------------------------------------------------------------------------ createAccountRecord()
	@Override
	public boolean createAccountRecord(String firstName, String lastName, String address, String phone, String branch) {

		String idRequest = "";
		String request = "";
		String [] args = {firstName, lastName, address, phone, branch};
		try {
			request = sequencer.sendRequestToRM("createAccountRecord", args);
			String[] requestArgs = request.split("-");
			idRequest = requestArgs[0];
			//add it to the hashmap
			rmReplies.put(idRequest, new ArrayList<String>());
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			//wait for the msg;
			getMsgFromRM();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Boolean isGood = verifyTheReplies(idRequest);
		return isGood;
	}

	//------------------------------------------------------------------------------------------------------------------------------ editRecord()
	@Override
	public boolean editRecord(String customerID, String fieldName, String newValue) {
		String [] args = {customerID, fieldName, newValue};
		String idRequest = "";
		String request = "";
		try {
			request = sequencer.sendRequestToRM("editRecord", args);
			String[] requestArgs = request.split("-");
			idRequest = requestArgs[0];

			//add it to the hashmap
			rmReplies.put(idRequest, new ArrayList<String>());

		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			//wait for the msg;
			getMsgFromRM();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Boolean isGood = verifyTheReplies(idRequest);
		return isGood;
	}

	//------------------------------------------------------------------------------------------------------------------------------ getAccountCount()
	@Override
	public String[] getAccountCount() {
		String [] args = {" "};
		String idRequest = "";
		String request = "";
		try {
			request  = sequencer.sendRequestToRM("getAccountCount", args);
			String[] requestArgs = request.split("-");
			idRequest = requestArgs[0];
			//add it to the hashmap
			rmReplies.put(idRequest, new ArrayList<String>());

		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			//wait for the msg;
			getMsgFromRM();
		} catch (IOException e) {
			e.printStackTrace();
		}

		Boolean isGood = verifyTheRepliesAccountCount(idRequest);

		if (isGood) {

			System.out.println("------------------------------------");
			ArrayList<String> temp = rmReplies.get(idRequest);

			for (int i =0; i <temp.size(); i++) {
				System.out.println(temp.get(i));
			}


			String correctResult = temp.get(0);

			//Convert the correct result to string array

			String[] finalStringArray = correctResult.split("|");

			return finalStringArray;
		}
		else {
			String[] empty = {};
			return empty;
		}

	}

	//------------------------------------------------------------------------------------------------------------------------------ transferFund()
	@Override
	public boolean transferFund(String managerID, double amount, String sourceCustomerID, String destinationCustomerID) {
		//transfer fund without UDP
		String [] args = {managerID, String.valueOf(amount), sourceCustomerID, destinationCustomerID};
		//get the id from sequence
		String idRequest = "";
		String request = "";

		try {
			request  = sequencer.sendRequestToRM("transferFund", args);
			String[] requestArgs = request.split("-");
			idRequest = requestArgs[0];
			//add it to the hashmap
			rmReplies.put(idRequest, new ArrayList<String>());

		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			//wait for the msg;
			getMsgFromRM();
		} catch (IOException e) {
			e.printStackTrace();
		}

		Boolean isGood = verifyTheReplies(idRequest);

		System.out.println(isGood + " value from FE transfer fundddd");



		return isGood;
	}

	//------------------------------------------------------------------------------------------------------------------------------ deposit()
	@Override
	public boolean deposit(String customerID, double amt) {
		String [] args = {customerID, Double.toString(amt)};
		String idRequest = "";
		String request = "";
		try {
			request = sequencer.sendRequestToRM("deposit", args);
			String[] requestArgs = request.split("-");
			idRequest = requestArgs[0];

			rmReplies.put(idRequest, new ArrayList<String>());
		} catch (IOException e) {
			e.printStackTrace();
		}
		//start listening
		try {
			getMsgFromRM();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Boolean isGood = verifyTheReplies(idRequest);

		return isGood;
	}

	//------------------------------------------------------------------------------------------------------------------------------ withdraw()
	@Override
	public boolean withdraw(String customerID, double amt) {
		String [] args = {customerID, Double.toString(amt)};

		//get the id from sequence
		String idRequest = "";
		String request = "";

		try {
			request  = sequencer.sendRequestToRM("withdraw", args);
			//add it to the hashmap
			String[] requestArgs = request.split("-");
			idRequest = requestArgs[0];

			rmReplies.put(idRequest, new ArrayList<String>());

		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			//wait for the msg;
			getMsgFromRM();
		} catch (IOException e) {
			e.printStackTrace();
		}

		Boolean isGood = verifyTheReplies(idRequest);

		if (isGood) {

			return true;

		}
		else
			return false;
	}

	//------------------------------------------------------------------------------------------------------------------------------ getBalance()
	@Override
	public double getBalance(String customerID) {

		String [] args = {customerID};

		//get the id from sequence
		String idRequest = "";
		String request = "";

		try {
			request  = sequencer.sendRequestToRM("getBalance", args);
			//add it to the hashmap
			String[] requestArgs = request.split("-");
			idRequest = requestArgs[0];
			rmReplies.put(idRequest, new ArrayList<String>());

		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			//wait for the msg;
			getMsgFromRM();
		} catch (IOException e) {
			e.printStackTrace();
		}

		Boolean isGood = verifyDoubleReplies(idRequest);
		if (isGood) {
			String answer = rmReplies.get(idRequest).get(0);
			double answerDouble = Double.parseDouble(answer);

			return answerDouble;
			//return true;

		}
		else
			//return false;
			return 0.00;
	}


	//------------------------------------------------------------------------------------------------------------------------------ setORB()

	public void setORB(ORB ORB) {
		this.ORB = ORB;
	}



	//------------------------------------------------------------------------------------------------------------------------------ getMsgFromRM()

	public void getMsgFromRM() throws IOException {
		//ID - RM NAME - TIME - REPLY
		activeRMs.replace("RM1", false);
		activeRMs.replace("RM2", false);
		activeRMs.replace("RM3", false);
		activeRMs.replace("RM4", false);


		System.out.println("------------------------------------------Get msg from RM starts here--------------------------------------------------------");

		for(Map.Entry<String, Boolean> entry: activeRMs.entrySet()) {
			System.out.println("key ->" + entry.getKey() + "  "  + " value ->" + entry.getValue() + "\n");
		}

		int timeoutValue = 1000;

		if (!socketIsOpen) {
			this.serverSocket = new DatagramSocket(9878);
			System.out.println("Listening now");
			this.socketIsOpen = true;
		}
		byte[] receiveData = new byte[1024];
		byte[] sendData = new byte[1024];

		while(true)
		{

			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
			try {
				serverSocket.setSoTimeout(timeoutValue);
				serverSocket.receive(receivePacket);
				numOfRMs++;
				String sentence = new String( receivePacket.getData());
				sentence = sentence.trim();

				System.out.println("\n\n" + sentence + "\n\n");

				long serversTime = System.currentTimeMillis();
				String [] msgFromRM = sentence.split("\\s");

				//changing the state of the RMs
				activeRMs.replace(msgFromRM[1], true);
				System.out.println("Just came in from -- " + msgFromRM[1]);

				//timeout couter implementation
				long timeOnClientSide = Long.parseLong(msgFromRM[2]);
				long timeTakenToReach = serversTime-timeOnClientSide;

				//convert timeTaken to reach to int for timeOUT
				int timeTakenToReachInInt = Integer.parseInt(String.valueOf(timeTakenToReach)) *1000;

				//set this time to default timeout value if the time take is higher than default value
				if (timeoutValue < timeTakenToReachInInt) {
					//System.out.println("blah blah blah blah");
					System.out.println("\nTime value changed " + timeoutValue + "ms" + " to " + timeTakenToReachInInt + "ms\n");
					timeoutValue = timeTakenToReachInInt * 2;
				}

				String idRequest = msgFromRM[0];
				String rmName = msgFromRM[1];
				String rmReply = msgFromRM[3];

				System.out.println("Received message from replica " + rmName);
				System.out.println("The reply is " + rmReply );

				rmReplies.get(idRequest).add(rmReply);


				//System.out.println("RECEIVED: " + msgFromRM[0]);
				//System.out.println("Time take to reach - " + timeTakenToReach);

				if (numOfRMs == 4){
					numOfRMs = 0;
					break;
				}


			} catch (SocketTimeoutException e) {
				System.out.println("Inside the timeout catch");
				handleTimeoutException();
				System.out.println("Handled exception!");
				numOfRMs = 0;
				System.out.println("Number of expected RMs #" + numOfRMs);
				break;


			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		System.out.println("--------------------------------------------------------------------------------------------------");

	}

	//------------------------------------------------------------------------------------------------------------------------------ handleTimeoutException()
	private void handleTimeoutException() {

		//System.out.println("Hello blah blah blah laha lalahha ANDY BERNARD");
		String sentenceToSend = "";

		for(Map.Entry<String, Boolean> entry: activeRMs.entrySet()) {
			System.out.println("key ->" + entry.getKey() + "  "  + " value ->" + entry.getValue() + "\n");
			if (entry.getValue()==false) {
				sentenceToSend += entry.getKey();
			}
		}


		DatagramSocket socket = null;
		try {
			socket = new DatagramSocket();
		} catch (SocketException e) {
			e.printStackTrace();
		}
		InetAddress group = null;
		try {
			group = InetAddress.getByName("230.1.1.1");
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		byte[] buf = sentenceToSend.getBytes();

		DatagramPacket packet = new DatagramPacket(buf, buf.length, group, 5859);
		try {
			socket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
		socket.close();
		numOfRMs =0;
		//return;
	}

	private void switchRMPort() throws Exception{


		BufferedReader inFromUser =
				new BufferedReader(new InputStreamReader(System.in));
		DatagramSocket clientSocket = new DatagramSocket();
		InetAddress IPAddress = InetAddress.getByName("localhost");
		byte[] sendData = new byte[1024];
		byte[] receiveData = new byte[1024];
		String sentence = inFromUser.readLine();
		sendData = sentence.getBytes();
		DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 8321);
		clientSocket.send(sendPacket);
		DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
		clientSocket.receive(receivePacket);
		String modifiedSentence = new String(receivePacket.getData());
		System.out.println("FROM SERVER:" + modifiedSentence);
		clientSocket.close();




	}


	//------------------------------------------------------------------------------------------------------------------------------ verifyTheReplies()
	private Boolean verifyTheReplies(String idRequest) {
		ArrayList<String> replies = rmReplies.get(idRequest);

		boolean allEqual;
		int counter1 = 0;
		int counter2 = 0;

		String trueValue = "";

		if(replies.size() == 4) {

			//Checks if ALL 4 replies are the same
			allEqual = replies.stream().allMatch(replies.get(0)::equals);

			if (allEqual){
				String firstValue = replies.get(0);
				return Boolean.valueOf(firstValue);
			}

			//If not ALL 4 values are the same, checks if 3 of the replies are the same
			if(allEqual == false) {

				String firstValue = replies.get(0);
				String secondValue = replies.get(1);

				for(String r : replies) {
					if(r.equals(firstValue)) {
						counter1++;
					}
				}

				if (counter1 >= 3) {
					trueValue = firstValue;
					return true;
				}

				for(String r : replies) {
					if(r.equals(secondValue)) {
						counter2++;
					}
				}

				if (counter2 >= 3) {
					trueValue = secondValue;
					return true;
				}

				rmReplies.get(idRequest).add(0, trueValue);

				return false;
			}

		}

		else if(replies.size() == 3) {
			allEqual = replies.stream().allMatch(replies.get(0)::equals);

			if (allEqual){
				String firstValue = replies.get(0);
				return Boolean.valueOf(firstValue);
			}

		}

		else if(replies.size() == 2) {
			allEqual = false;

		}
		else {
			allEqual = false;
		}

		if(allEqual == false) {
			System.out.println("Less than 3 Replicas...exiting System");
			//System.exit(0);
		}
		return allEqual;
	}


	//------------------------------------------------------------------------------------------------------------------------------ verifyTheReplies()
	private Boolean verifyDoubleReplies(String idRequest) {
		boolean allEqual = false;

		//ArrayList containing the String replies from RMs
		ArrayList<String> replies = rmReplies.get(idRequest);

		//ArrayList containing the Double replies from RMs
		ArrayList<Double> repliesDoubles = new ArrayList<Double>();

		int counter1 = 0;
		int counter2 = 0;

		Double trueValue = 0.0;

		if(replies.size() == 4) {
			repliesDoubles.add(Double.parseDouble(replies.get(0)));
			repliesDoubles.add(Double.parseDouble(replies.get(1)));
			repliesDoubles.add(Double.parseDouble(replies.get(2)));
			repliesDoubles.add(Double.parseDouble(replies.get(3)));
			//Checks if ALL 4 replies are the same
			allEqual = repliesDoubles.stream().allMatch(repliesDoubles.get(0)::equals);

			//If not ALL 4 values are the same, checks if 3 of the replies are the same
			if(allEqual == false) {
				Double firstValue = repliesDoubles.get(0);
				Double secondValue = repliesDoubles.get(1);

				for(Double r : repliesDoubles) {
					if(r.equals(firstValue)) {
						counter1++;
					}
				}

				if (counter1 >= 3) {
					trueValue = firstValue;
					return true;
				}

				for(Double r : repliesDoubles) {
					if(r.equals(secondValue)) {
						counter2++;
					}
				}

				if (counter2 >= 3) {
					trueValue = secondValue;
					return true;
				}

				String trueValueToString = Double.toString(trueValue);
				rmReplies.get(idRequest).add(0, trueValueToString);

				return false;
			}

		}

		else if(replies.size() == 3) {
			repliesDoubles.add(Double.parseDouble(replies.get(0)));
			repliesDoubles.add(Double.parseDouble(replies.get(1)));
			repliesDoubles.add(Double.parseDouble(replies.get(2)));
			allEqual = repliesDoubles.stream().allMatch(repliesDoubles.get(0)::equals);
		}

		else if(replies.size() == 2) {
			allEqual = false;
		}
		else {
			allEqual = false;
		}

		if(allEqual == false) {
			System.out.println("Less than 3 Replicas...exiting System");
			//System.exit(0);
		}
		return allEqual;
	}


	//------------------------------------------------------------------------------------------------------------------------------ verifyTheReplies()
	private Boolean verifyTheRepliesAccountCount(String idRequest) {
		ArrayList<String> replies = rmReplies.get(idRequest);

		boolean allEqual;
		int counter1 = 0;
		int counter2 = 0;

		String trueValue = "";

		if(replies.size() == 4) {

			//Checks if ALL 4 replies are the same
			allEqual = replies.stream().allMatch(replies.get(0)::equals);

			if (allEqual){
				return true;
			}

			//If not ALL 4 values are the same, checks if 3 of the replies are the same
			if(allEqual == false) {

				String firstValue = replies.get(0);
				String secondValue = replies.get(1);

				for(String r : replies) {
					if(r.equals(firstValue)) {
						counter1++;
					}
				}

				if (counter1 >= 3) {
					trueValue = firstValue;
					return true;
				}

				for(String r : replies) {
					if(r.equals(secondValue)) {
						counter2++;
					}
				}

				if (counter2 >= 3) {
					trueValue = secondValue;
					return true;
				}

				rmReplies.get(idRequest).add(0, trueValue);

				return false;
			}

		}

		else if(replies.size() == 3) {
			allEqual = replies.stream().allMatch(replies.get(0)::equals);

			if (allEqual){
				String firstValue = replies.get(0);
				return true;
			}

		}

		else if(replies.size() == 2) {
			allEqual = false;

		}
		else {
			allEqual = false;
		}

		if(allEqual == false) {
			System.out.println("Less than 3 Replicas...exiting System");
			//System.exit(0);
		}
		return allEqual;
	}



	//------------------------------------------------------------------------------------------------------------------------------ MAIN
	public static void main(String args[]) throws IOException {

		FE fe = new FE();
		System.out.println("FE running ....");


	}
}

