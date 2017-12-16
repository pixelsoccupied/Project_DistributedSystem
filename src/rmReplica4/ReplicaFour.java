package rmReplica4;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import InnaClient.Configuration;
import InnaClient.Configuration.BRANCH_ID;
import InnaClient.CustomerClient;


//INNA
public class ReplicaFour {

	static private HashMap<Character, ArrayList<CustomerClient>> customer_records = new HashMap<>();

	private static final Object lockID = new Object();
    private static final Object lockCount = new Object();
    
    private static final Object lockDeposit = new Object();
    private static final Object lockWithdraw= new Object();
    private static final Object lockBalance = new Object();
    private BRANCH_ID branchID;
    private int accountCount;
    
    
    static int custID;
    private static final Logger LOGGER = Logger.getLogger(ReplicaFour.class.getName());

    Logger logger = Logger.getLogger(branchID + "Server");
	FileHandler fileHandler;

    private HashMap<String, Integer>ports = new HashMap<>();
    Object lock = new Object();

    public ReplicaFour() {
    	custID = 1000;    	
        this.branchID = branchID;
        ports.put("QC" , 5876 );
        ports.put("BC" , 6876 );
        ports.put("MB" , 7876 );
        ports.put("NB" , 8876 );

    }

    //------------------------------------------------------------------------------------------------------------------------------------------------------------
    //-----------------------------------------------------------------------------------------------------------------------------------------GETTERS AND SETTERS
  	public BRANCH_ID getBranchID() {
  		return branchID;
  	}


  	public void setBranchID(BRANCH_ID branchID) {
  		this.branchID = branchID;
  	}


  	//------------------------------------------------------------------------------------------------------------------------------------------------------------
  	//-------------------------------------------------------------------------------------------------------------------------------------- CREATE ACCOUNT RECORD
  	
    public boolean createAccountRecord(String firstName, String lastName, String address, String phone, String branch) {
    	System.out.println("Replica Four...");
    	String message = "";
        String customerID;
	       
		synchronized (lockID) {
	        	customerID = branch + "C" + custID;
	        	custID++;
	    }
       
        
        CustomerClient customerClient = new CustomerClient(firstName, lastName, customerID, address, phone, branch);
        Character key = lastName.toUpperCase().charAt(0);
        
        if (customer_records.containsKey(key)) {
            ArrayList<CustomerClient> list = customer_records.get(key);
            list.add(customerClient);
            customer_records.put(key, list);
    
        } else {
            
            ArrayList<CustomerClient> list = new ArrayList<CustomerClient>();
            list.add(customerClient);
            customer_records.put(key, list);
        }
      
        ArrayList<CustomerClient> list = customer_records.get(key);
        for (CustomerClient customer : list) {
            System.out.println(customer.toString());
        }
        System.out.println("Created a new account record for customer " + firstName + " " + lastName + " with ID " + customerID);
      return true;
   }

    //------------------------------------------------------------------------------------------------------------------------------------------------------------
  	//------------------------------------------------------------------------------------------------------------------------------------------------ EDIT RECORD

    public boolean editRecord(String customerID, String fieldName, String newValue) {
    	System.out.println("Replica Four...");
    	CustomerClient customer = verifyCustomerClient(customerID);
    	if (customer == null) {
    		System.out.println("Customer with ID " + customerID + " does not exist!");
    		return false;
    	}
    	else {
    		switch(fieldName){
			case "address":
				customer.setAddress(newValue);
				System.out.println("The address of customer with ID " + customerID + " is changed."); 
				return true;
			case "phone":
				customer.setPhone(newValue);
				System.out.println("The phone of customer with ID " + customerID + " is changed.");
				return true;
			case "branch":
				customer.setBranch(newValue);
				System.out.println("The branch of customer with ID " + customerID + " is changed.");
				return true;
			default:
				System.out.println("Only fields address, phone and branch are allowed to be changed.");
				return false;
			}
    	}
    }

    
    //------------------------------------------------------------------------------------------------------------------------------------------------------------
  	//--------------------------------------------------------------------------------------------------------------------------------------------- VERIFY CUSOMER
    private CustomerClient verifyCustomerClient(String customerID) {
    	
    	System.out.println("Inside verifyClient........................................: ");
    	
    	 ArrayList<CustomerClient> temp;
         CustomerClient tempClient = null;
         for (Character key : customer_records.keySet()) {

             temp = customer_records.get(key);

             for (CustomerClient client : temp) {
                 if (client.getAccountNumber().equalsIgnoreCase(customerID)) {
                	 System.out.println("Returning client " + client);
                     return client;
                 }
             }
         }
         System.out.println("Inside verify...before returning null................................");
         return tempClient;
      }
        

    //------------------------------------------------------------------------------------------------------------------------------------------------------------
  	//----------------------------------------------------------------------------------------------------------------------------------------- GET ACCOUNT COUNTS
    public synchronized String getAccountCount() {
    	System.out.println("Replica Four...");
    	int bcCount = 0, qcCount = 0, nbCount = 0, mbCount  = 0;
        Set entrySet = customer_records.entrySet();

        for (Object anEntrySet : entrySet) {
            Map.Entry mapEntry = (Map.Entry) anEntrySet;
            ArrayList<CustomerClient> list = customer_records.get(mapEntry.getKey());
            for (CustomerClient customer :  list
                    ) {
                if (customer.getBranch().equalsIgnoreCase("BC")) {
                    bcCount++;
                }
                if (customer.getBranch().equalsIgnoreCase("QC")) {
                    qcCount++;
                }
                if (customer.getBranch().equalsIgnoreCase("NB")) {
                    nbCount++;
                }
                if (customer.getBranch().equalsIgnoreCase("MB")) {
                    mbCount++;
                }
            }
        }
        String result = "BC:" + bcCount + "|QC:" + qcCount + "|NB:" + nbCount + "|MB:" + mbCount;
        return result;
    }



	//------------------------------------------------------------------------------------------------------------------------------------------------------------
	//-------------------------------------------------------------------------------------------------------------------------------------------------TRANSFERFUND

    public boolean transferFund(String managerID, double amount, String sourceCustomerID, String destinationCustomerID) {
    	System.out.println("Replica Four...");
        //performs a verification if there's enough money in the source customer account

        String sourceSever = sourceCustomerID.substring(0,2);
        sourceSever = sourceSever + "Server";
        Boolean availableFunds = verifyFunds(sourceCustomerID, amount);

        //if there's enough money send the money
        if(availableFunds) {
     
            boolean sendMoner = withdraw(sourceCustomerID, amount);
            boolean enoughMoney = deposit(destinationCustomerID, amount);

            if (enoughMoney && sendMoner) {
            	System.out.println("Manager " + managerID + "called transfer money for " + sourceCustomerID + "to " + destinationCustomerID + " success");
                return true;
            }
            else
                return false;
        }

        else {
        		System.out.println("Manager " + managerID + "called transfer money for " + sourceCustomerID + "to " + destinationCustomerID + " Failed...not enough money!");
                return false;

        }
    }

	//------------------------------------------------------------------------------------------------------------------------------------------------------------
	//----------------------------------------------------------------------------------------------------------------------------------- TRANSFER (HELPER METHOD)
    private Boolean transfer(String destinationCustomerID, double amount) {

        String serverName = destinationCustomerID.substring(0, 2).toUpperCase();
       
        try {
        	DatagramSocket clientSocket = new DatagramSocket();
            InetAddress IPAddress = InetAddress.getByName("");
            byte[] sendData = new byte[1024];
            byte[] receiveData = new byte[1024];
            String sentence = "1 " + destinationCustomerID + " " + amount;
            sendData = sentence.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, ports.get(serverName));
            clientSocket.send(sendPacket);
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            clientSocket.receive(receivePacket);
            String receivedValue = new String(receivePacket.getData());
            System.out.println("FROM SERVER:" + receivedValue.trim());

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

    private Boolean verifyFunds(String sourceCustomerID, double amount) {
        CustomerClient customerClient = verifyCustomerClient(sourceCustomerID);
        return customerClient != null && amount < getBalance(customerClient.getAccountNumber());
    }
    

	//------------------------------------------------------------------------------------------------------------------------------------------------------------
	//----------------------------------------------------------------------------------------------------------------------------------------- DEPOSIT
    
    public synchronized boolean deposit(String customerID, double amount) {
    	System.out.println("Inside deposit of Replica 4...");
        double balance = 0;
        CustomerClient customerClient = verifyCustomerClient(customerID);
        System.out.println("Our customer is: ");
        System.out.println(customerClient);
        
        if(customerClient != null){
            balance = customerClient.getBalance();
            
            synchronized (lockDeposit) {
            	balance += amount;
            }
            
            synchronized (lockBalance) {
            	customerClient.setBalance(balance);
            }
            System.out.println("The current balance of customer with ID " + customerID + " is " + balance);
            return true;
        }
        else{
            System.out.println("Customer with ID " + customerID + " does not exist.");
            return false;
        }
    }

    //------------------------------------------------------------------------------------------------------------------------------------------------------------
  	//----------------------------------------------------------------------------------------------------------------------------------------- WITHDRAW
    public synchronized boolean withdraw(String customerID, double amount) {
    	System.out.println("Replica Four...");
        double balance = 0;
        CustomerClient customerClient = verifyCustomerClient(customerID);
        if(customerClient != null){
        	balance = customerClient.getBalance();
        	
        	synchronized (lockWithdraw) {
        		balance -= amount;
        	}
           
        	synchronized (lockBalance) {
            	customerClient.setBalance(balance);
            }
        	
            System.out.println("Money withdrawn. Customer " + customerID + " has $" + balance + " in the account");
            return true;
        }
        else{
        	System.out.println("Customer with ID " + customerID + " does not exist.");
            return false;
        }

    }

    //------------------------------------------------------------------------------------------------------------------------------------------------------------
  	//-------------------------------------------------------------------------------------------------------------------------------------- GET BALANCE
    public synchronized double getBalance(String customerID) {
    	System.out.println("Replica Four...");
        double balance = 0;
        CustomerClient customerClient = verifyCustomerClient(customerID);
        if(customerClient != null){
        	synchronized (lockBalance) {
        		balance = customerClient.getBalance();
        	}
            System.out.println("The current balance of customer with ID " + customerID + " is " + balance);
            return balance;
        }
        else{
            System.out.println("Customer with ID " + customerID + " does not exist.");
            return 0;
        }

    }

    //add a main to invoke the server
    public static void main(String [] args ){


        ReplicaFour replicaFour = new ReplicaFour();
        replicaFour.runUDPServer();

    }

    private void runUDPServer() {


        DatagramSocket serverSocket = null;
        try {
            serverSocket = new DatagramSocket(7657);
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
