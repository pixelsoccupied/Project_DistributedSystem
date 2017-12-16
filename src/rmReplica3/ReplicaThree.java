package rmReplica3;

import AmiraliClient.CustomerClient;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;


import static log.Log.generateLogFileServer;


//AMIRALI
public class ReplicaThree {


	static private HashMap<Character, ArrayList<CustomerClient>> map = new HashMap<>();
    static int iD = 1000;
    private HashMap<String, Integer> allBranchCount = new HashMap<>();
    private HashMap<String, Integer>serverPort = new HashMap<>();
    private int destinationCustomerID=1000;
	private Object lock = new Object();

	Logger logger = Logger.getLogger("MyLog");
	FileHandler fh;



    //setter for ORB

    public ReplicaThree() {
        
        serverPort.put("QC" , 5876 );
        serverPort.put("BC" , 6876 );
        serverPort.put("MB" , 7876 );
        serverPort.put("NB" , 8876 );

        synchronized (map) {

        }


    }

    public boolean createAccountRecord(String firstName, String lastName, String address, String phoneNumber,
			String branchName) {
    	System.out.println("Replica Three...");
		CustomerClient newClient = new CustomerClient(firstName, lastName, generateID(branchName), address, phoneNumber, branchName, 0.0);

		char key=lastName.charAt(0);

		if (map.containsKey(key)){
			List<CustomerClient> temp=map.get(key);
			temp.add(newClient);
		}else{
			List<CustomerClient> temp = new ArrayList<>();
			temp.add(newClient);
			map.put(key, (ArrayList<CustomerClient>) temp);
		}

		try {
			log("New account cretaed with ID: "+newClient.getAccountNumber());
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;

	}

    

    public synchronized boolean editRecord(String customerID, String fieldName, String newValue) {
    	System.out.println("Replica Three...");
    	boolean done=false;

		CustomerClient customerObj = verifyClientID(customerID);

		if (customerObj!=null){

			switch(fieldName){
			case "branch":
				// optional: we can add check to get the right value name from the user 
				customerObj.setBranchName(newValue);
				try {
					log("Clinet account successfully edited, "+fieldName+" was chaneged to "+newValue );
				} catch (SecurityException | IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			case "phone":
				customerObj.setPhoneNumber(newValue);
				try {
					log("Clinet account successfully edited, "+fieldName+" was chaneged to "+newValue );
				} catch (SecurityException | IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			case "address":
				customerObj.setAddress(newValue);
				try {
					log("Clinet account successfully edited, "+fieldName+" was chaneged to "+newValue );
				} catch (SecurityException | IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			case "firstName":
				customerObj.setFirstName(newValue);
				try {
					log("Clinet account successfully edited, "+fieldName+" was chaneged to "+newValue );
				} catch (SecurityException | IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			case "familyName":
				customerObj.setLastName(newValue);
				try {
					log("Clinet account successfully edited, "+fieldName+" was chaneged to "+newValue );
				} catch (SecurityException | IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			default:
				System.out.println("invalid input");		
			}
			done =true;
		}else{
			System.out.println("Client ID not found.");
			try {
				log("Unsuccessful attempt" );
			} catch (SecurityException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return done;
       
    }

    private CustomerClient locateUser(String customerID) {


        // Getting a Set of Key-value pairs
        Set entrySet = map.entrySet();

        // Obtaining an iterator for the entry set

        // Iterate through HashMap entries(Key-Value pairs)
        for (Object anEntrySet : entrySet) {
            Map.Entry me = (Map.Entry) anEntrySet;

            System.out.println("Key is: " + me.getKey());
            //iterate with the arraylist
            ArrayList<CustomerClient> temp = map.get(me.getKey());
            for (CustomerClient cc : temp
                    ) {
                if (cc.getAccountNumber().equalsIgnoreCase(customerID)) {
                    System.out.println("Found user!");
                    return cc;
                }
            }

        }
        System.out.println("Customer does not exist in the DB");

        return null;
    }


    public synchronized String getAccountCount() {
    	System.out.println("Replica Three...");
        int bcCount = 0;
        int qcCount = 0;
        int nbCount = 0;
        int mbCount = 0;

        //iterate through all the


        // Getting a Set of Key-value pairs
        Set entrySet = map.entrySet();


        // Iterate through HashMap entries(Key-Value pairs)
        for (Object anEntrySet : entrySet) {
            Map.Entry me = (Map.Entry) anEntrySet;

            //System.out.println("Key is: " + me.getKey());
            //iterate with the arraylist
            ArrayList<CustomerClient> temp = map.get(me.getKey());
            for (CustomerClient cc : temp
                    ) {
                if (cc.getBranchName().equalsIgnoreCase("BC")) {
                    bcCount += 1;
                }
                if (cc.getBranchName().equalsIgnoreCase("QC")) {
                    qcCount += 1;
                }
                if (cc.getBranchName().equalsIgnoreCase("NB")) {
                    nbCount += 1;
                }
                if (cc.getBranchName().equalsIgnoreCase("MB")) {
                    mbCount += 1;
                }

            }

        }
        String result = "BC:" + bcCount + "|QC:" + qcCount + "|NB:" + nbCount + "|MB:" + mbCount;
      return result;
    }


    public synchronized boolean transferFund(String managerID, double amount, String sourceCustomerID, String destinationCustomerID) {
    	System.out.println("Replica Three...");
    	//check if there's enough balance

        String sourceSever = sourceCustomerID.substring(0,2);
        sourceSever = sourceSever + "Server";
        Boolean hasEnoughBalance = enoughBalance(sourceCustomerID, amount);

        //if there's enough money send the money
        if(hasEnoughBalance) {
            // transfer the money

            //withdraw the money from the source and deposit to the source
            boolean sendCash = withdraw(sourceCustomerID, amount);
            boolean gotTheCash = deposit(destinationCustomerID, amount);

            if (gotTheCash && sendCash) {


                try {
                    String msg = "Manager " + managerID + "called transfer money for " + sourceCustomerID + "to " +
                            destinationCustomerID + " success";
                    generateLogFileServer(msg, "Server", sourceSever);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return true;


            }
            else
                return false;


        }

        else {

            try {
                String msg = "Manager " + managerID + "called transfer money for " + sourceCustomerID + "to " +
                        destinationCustomerID + " Failed...not enough money!";
                generateLogFileServer(msg, "Server", sourceSever);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;

        }


    }

    private Boolean sendMoneyToDestinationCustomer(String destinationCustomerID, double amount) {

        String serverName = destinationCustomerID.substring(0, 2).toUpperCase();
        System.out.println("----------------------------");

        System.out.println(serverName);
        System.out.println(serverPort.get(serverName));

        try {

            //create socket
            DatagramSocket clientSocket = new DatagramSocket();
            //Get ip address
            InetAddress IPAddress = InetAddress.getByName("localhost");
            byte[] sendData = new byte[1024];
            byte[] receiveData = new byte[1024];
            String sentence = "1 " + destinationCustomerID + " " + amount;
            //convert the request to bytes
            sendData = sentence.getBytes();
            //send the stuff

            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, serverPort.get(serverName));
            clientSocket.send(sendPacket);


            //Receive the stuff
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            clientSocket.receive(receivePacket);
            //Store it in a variable
            String receivedValue = new String(receivePacket.getData());
            //Print out the data
            System.out.println("FROM SERVER:" + receivedValue.trim());
            //Parse the data

            receivedValue=receivedValue.trim();
            System.out.println(receivedValue);

            if(receivedValue.equalsIgnoreCase("Success")){
                clientSocket.close();
                return true;
            }
            else {
                clientSocket.close();
                return false;
            }
    } catch (NumberFormatException | IOException ignored) { }
        return false;
    }

    
    
    private Boolean enoughBalance(String sourceCustomerID, double amount) {
        CustomerClient customerClient = locateUser(sourceCustomerID);
        return customerClient != null && amount < getBalance(customerClient.getAccountNumber());
    }

    
    
    public synchronized boolean deposit(String customerID, double amt) {
    	System.out.println("Replica Three...");
    	double total=0.0;
		CustomerClient customerObj = verifyClientID(customerID);
		if(customerObj != null){
			total = customerObj.getBalance();
			total += amt;
			customerObj.setBalance(total);
			System.out.println("Money deposited. Customer " + customerID + " has $" + total + " in the account");
			try {
				log("Successful deposit for "+customerID+" in the amount of "+amt);
			} catch (SecurityException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return true;
		}
		else{
			System.out.println("Customer " + customerID + " doesn't exist in the database!");
			try {
				log ("Deposit failed for "+amt);
			} catch (SecurityException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return false;
		}
    }

    
    public synchronized boolean withdraw(String customerID, double amt) {
    	System.out.println("Replica Three...");
    	double total=0.0;
		CustomerClient customerObj = verifyClientID(customerID);
		if(customerObj != null){
			total = customerObj.getBalance();
			total -= amt;
			customerObj.setBalance(total);
			System.out.println("Money withdrawn. Customer " + customerID + " has $" + total + " in the account");
			try {
				log ("Successful withraw for ID: "+customerID+ " in the amount of "+amt);
			} catch (SecurityException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return true;
		}
		else{
			System.out.println("Customer " + customerID + " doesn't exist in the database!");
			try {
				log("Withraw failed.");
			} catch (SecurityException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return false;
		}
    }

    
    public synchronized double getBalance(String customerID) {
    	System.out.println("Replica Three...");
    	double total=0.0;
		CustomerClient customerObj = verifyClientID(customerID);
		if(customerObj != null){
			total = customerObj.getBalance(); 
			System.out.println("Customer " + customerID + " has $" + total + " in the account");
			try {
				log("Balance requested for "+customerID);
			} catch (SecurityException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return total;
		}
		else{
			System.out.println("Customer " + customerID + " doesn't exist in the database!");
			return total;
		}

    }
    
    public int mapSize() {

		int mapSize=0;

		Set<Character> keys=map.keySet();
		for(Character key:keys){
			List<CustomerClient> temp=map.get(key);
			mapSize+=temp.size();
		}
		return mapSize;
	}

    public synchronized CustomerClient verifyClientID(String customerID){

    	 ArrayList<CustomerClient> temp;
         CustomerClient tempClient = null;
         for (Character key : map.keySet()) {

             temp = map.get(key);

             for (CustomerClient client : temp) {
                 if (client.getAccountNumber().equalsIgnoreCase(customerID)) {
                     return client;
                 }
             }
         }
         return tempClient;
	}
	
    private String generateID(String branchName) {
    	// we need to add one modifier letter to assign costumers or client 
		String costumerID;
		costumerID=branchName+"C"+destinationCustomerID;
		destinationCustomerID++;

		System.out.println("Client ID: "+costumerID);
		return costumerID;


    }
    
    public void log (String msg) throws SecurityException, IOException{
//
//		// change this later
//		fh = new FileHandler("C://Users//Amirali//workspace2//A2SOEN423//src//Log//QCServer.log");
//		logger.addHandler(fh);
//		SimpleFormatter formatter = new SimpleFormatter();
//		fh.setFormatter(formatter);
//		logger.log(Level.INFO, msg);
	}

	//add a main to invoke the server
	public static void main(String [] args ){


		ReplicaThree replicaThree = new ReplicaThree();
		replicaThree.runUDPServer();

	}

	private void runUDPServer() {


		DatagramSocket serverSocket = null;
		try {
			serverSocket = new DatagramSocket(7656);
		} catch (SocketException e) {
			e.printStackTrace();
		}
		byte[] receiveData = new byte[1024];
		byte[] sendData = new byte[1024];
		while(true)
		{
			System.out.println("Waiting for a request from Replica manager ");
			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
			try {
				serverSocket.receive(receivePacket);
			} catch (IOException e) {
				e.printStackTrace();
			}
			String sentence = new String( receivePacket.getData());
			sentence = sentence.trim();
			System.out.println("RECEIVED: " + sentence);

			String []request = sentence.split("\\s");
			String method = request[0];

			String customerID = request[1];
			String resultConv;
			switch(method) {
				case "createAccountRecord":
					String firstName = request[1];
					String lastName = request[2];
					String address = request[3];
					String phone = request[4];
					String branch = request[5];

					Boolean resultBool = createAccountRecord(firstName, lastName, address, phone, branch);
					//convert the result to string
					resultConv = "" + resultBool;
					sendData = resultConv.getBytes();
					break;

				case "editRecord":
					String fieldName = request[2];
					String newValue = request[3];
					boolean replyFromReplicaEditRecord = editRecord(customerID, fieldName, newValue);

					resultConv = "" + replyFromReplicaEditRecord;
					sendData = resultConv.getBytes();

					break;

				case "getAccountCount":
					String reply = getAccountCount();
					resultConv = "" + reply;
					sendData = resultConv.getBytes();
					break;

				case "transferFund":
					String managerID = request[1];
					Double amount = Double.parseDouble(request[2]);
					String sourceCustomerID = request[3];
					String destinationCustomerID = request[4];
					boolean replyFromReplicaTransferFunds = transferFund(managerID, amount, sourceCustomerID, destinationCustomerID);

					resultConv = "" + replyFromReplicaTransferFunds;

					sendData = resultConv.getBytes();
					break;

				case "deposit":
					double amountDeposit = Double.parseDouble(request[2]);
					boolean replyDeposit = deposit(customerID, amountDeposit);
					String convertReply = "" + replyDeposit;
					sendData = convertReply.getBytes();
					break;

				case "withdraw":
					amount = Double.parseDouble(request[2]);
					boolean replyFromReplicaWithdraw = withdraw(customerID, amount);

					resultConv = "" + replyFromReplicaWithdraw;
					sendData = resultConv.getBytes();

					break;

				case "getBalance":

					System.out.println("CustomerID is" + customerID);
					double balance = getBalance(customerID);
					resultConv = ""+balance;
					sendData = resultConv.getBytes();
					break;


			}


			//prepare to send stuff back to manager
			InetAddress IPAddress = receivePacket.getAddress();
			int port = receivePacket.getPort();
			// String capitalizedSentence = sentence.toUpperCase();
			// sendData = capitalizedSentence.getBytes();
			DatagramPacket sendPacket =
					new DatagramPacket(sendData, sendData.length, IPAddress, port);
			try {
				serverSocket.send(sendPacket);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}


}
