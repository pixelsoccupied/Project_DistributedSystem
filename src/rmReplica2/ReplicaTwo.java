package rmReplica2;

import YasmineClient.CustomerClient;


import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.FileHandler;

import static log.Log.generateLogFileServer;


//YASMINE
public class ReplicaTwo {

	static private HashMap<Character, ArrayList<CustomerClient>> myMap = new HashMap<>();
	static int IDCount = 1000;;
    private HashMap<String, Integer> allBranchCount = new HashMap<>();

    private HashMap<String, Integer>serverPort = new HashMap<>();
    //Logger logger = Logger.getLogger(ReplicaTwo.class.getName());
    FileHandler handler;

    //lock object
    Object lock = new Object();

    //setter for ORB

    public ReplicaTwo() {
         /*
        qc->5876
        BC->6876
        MB->7876
        NB->8876

        */
        serverPort.put("QC" , 5876 );
        serverPort.put("BC" , 6876 );
        serverPort.put("MB" , 7876 );
        serverPort.put("NB" , 8876 );

        synchronized (myMap) {

        }


    }
    /*When a manager invokes this method through a program called Clients.ManagerClient, the
    server associated with the indicated branch attempts to create a customer record
    with the information passed, assigns a unique customerID and inserts the customer
    record at the appropriate location in the hash map maintained at the indicated
    branch. The server returns information to the manager whether the operation was
    successful or not and both the server and the client store this information in their
    logs.CHECK THIS LATER.*/
    public synchronized boolean createAccountRecord(String firstName, String lastName, String address, String phone, String branch) {
    	 System.out.println("Replica Two...");
    	 String className = new Exception().getStackTrace()[1].getClassName();

         System.out.println(className);

         String clientID = branch + "C" + IDCount;
         IDCount++;
         CustomerClient client = new CustomerClient(firstName, lastName, phone, address);
         client.setBranch(branch);
         client.setAccountNumber(clientID);

         Character key = lastName.toUpperCase().charAt(0);
         int count = 0;
         if (myMap.containsKey(key)) {

             ArrayList<CustomerClient> temp = myMap.get(key);
             temp.add(client);
             myMap.put(key, temp);
             count++;
             System.out.println("*** User Account Created! ***");
             generateInfo(client);
             System.out.println();

         } else {

             ArrayList<CustomerClient> temp = new ArrayList<CustomerClient>();
             temp.add(client);
             myMap.put(key, temp);
             count++;
             System.out.println("*** Added Client To Database! ***");
             generateInfo(client);
             System.out.println();

         }

         if (count > 0) {
//             try{
//                 log("Added New Client With ID: " + clientID);}
//             catch(Exception e){
//             }
             return true;
         }

         return false;
    }

    public int listSize(){

        int total = 0;

        Set entrySet = myMap.entrySet();
        Iterator it = entrySet.iterator();

        while (it.hasNext()) {
            Map.Entry me = (Map.Entry) it.next();
            ArrayList<CustomerClient> temp = myMap.get(me.getKey());
            total += temp.size();

        }

        return total;

    }
    

    public synchronized boolean editRecord(String customerID, String fieldName, String newValue) {
    	System.out.println("Replica Two...");
    	int result = 0;
        ArrayList<CustomerClient> temp;

        if (fieldName.equalsIgnoreCase("branch")) {
            if (newValue.equalsIgnoreCase("bc") || newValue.equalsIgnoreCase("mb") || newValue.equalsIgnoreCase("nb") ||
                    newValue.equalsIgnoreCase("qc")) {

            } else {
                System.out.println("Error: no such branch");
                return false;
            }
        } else if (fieldName.equalsIgnoreCase("phone") || fieldName.equalsIgnoreCase("address")) {

        } else {
            System.out.println("Error: You cannot alter this field");
            return false;
        }

        CustomerClient client = findClient(customerID);

        if(client == null){
            System.out.println("Error: client with CustomerID: " + customerID + " does not exist!");
            return false;
        }
        switch (fieldName.toLowerCase()) {
            case "branch":
                client.setBranch(newValue.toUpperCase());
                break;
            case "phone":
                client.setPhone(newValue);
                break;
            case "address":
                client.setAddress(newValue);
                break;
            default:
                break;
        }

        System.out.println("****** User account successfully alterd! ******");
        generateInfo(client);
//        try{
//            log("Altered Client Account With ID: " + customerID);}
//        catch(Exception e){
//        }
        return true;
       
    }

    public CustomerClient findClient(String customerID) {

        ArrayList<CustomerClient> temp;
        CustomerClient tempClient = null;
        for (Character key : myMap.keySet()) {

            temp = myMap.get(key);

            for (CustomerClient client : temp) {
                if (client.getAccountNumber().equalsIgnoreCase(customerID)) {
                    return client;
                }
            }
        }
        return tempClient;
    }
    
   

    public synchronized String getAccountCount() {
    	
    	System.out.println("Replica Two...");
        int bcCount = 0;
        int qcCount = 0;
        int nbCount = 0;
        int mbCount = 0;

        //iterate through all the


        // Getting a Set of Key-value pairs
        Set entrySet = myMap.entrySet();


        // Iterate through HashMap entries(Key-Value pairs)
        for (Object anEntrySet : entrySet) {
            Map.Entry me = (Map.Entry) anEntrySet;

            //System.out.println("Key is: " + me.getKey());
            //iterate with the arraylist
            ArrayList<CustomerClient> temp = myMap.get(me.getKey());
            for (CustomerClient cc : temp
                    ) {
                if (cc.getBranch().equalsIgnoreCase("BC")) {
                    bcCount += 1;
                }
                if (cc.getBranch().equalsIgnoreCase("QC")) {
                    qcCount += 1;
                }
                if (cc.getBranch().equalsIgnoreCase("NB")) {
                    nbCount += 1;
                }
                if (cc.getBranch().equalsIgnoreCase("MB")) {
                    mbCount += 1;
                }

            }

        }




        String result = "BC:" + bcCount + "|QC:" + qcCount + "|NB:" + nbCount + "|MB:" + mbCount;

        return result;
    }


    public synchronized boolean transferFund(String managerID, double amount, String sourceCustomerID, String destinationCustomerID) {
    	System.out.println("Replica Two...");
    	//check if there's enough balance

        String sourceSever = sourceCustomerID.substring(0,2);
        sourceSever = sourceSever + "Server";
        Boolean hasEnoughBalance = verifyBalance(sourceCustomerID, amount);

       
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

    private Boolean udpToTransferFunds(String destinationCustomerID, double amount) {

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



            //System.out.println("Added count from " + branchName + " server");


        } catch (NumberFormatException | IOException ignored) { }




        return false;
    }

    private Boolean verifyBalance(String sourceCustomerID, double amount) {
        CustomerClient customerClient = findClient(sourceCustomerID);
        return customerClient != null && amount < getBalance(customerClient.getAccountNumber());
    }

    public synchronized boolean deposit(String customerID, double amt) {
    	CustomerClient client = findClient(customerID);

        if(client == null){
            System.out.println("Error: client with CustomerID: " + customerID + " does not exist!");
            return false;
        }
        double newBalance;
        synchronized(this) {

            newBalance = client.getBalance() + amt;
            client.setBalance(newBalance);
        }
            System.out.println("Client with customer ID: " + customerID + " has succefully deposited $" + amt );
          System.out.println("New Balance: " + client.getBalance());


//       try{
//            //log("Deposited $" + amt + " Into Clients Account with ID: " + customerID);}
//        catch(Exception e){
//        }
        return true;
    }

    public synchronized boolean withdraw(String customerID, double amt) {
    	System.out.println("Replica Two...");
    	CustomerClient client = findClient(customerID);

        if(client == null){
            System.out.println("Error: client with CustomerID: " + customerID + " does not exist!");
            return false;
        }

        double newBalance = client.getBalance() - amt;

        if(newBalance < 0){
            System.out.println("Error: Account has insufficient funds!");
            return false;
        }
        synchronized(this) {
            client.setBalance(newBalance);
        }
            System.out.println("Client with customer ID: " + customerID + " has succefully withdrawn $" + amt );
            System.out.println("New Balance: " + client.getBalance());

//        try{
//            log("Withdrew $" + amt + " From Clients Account with ID: " + customerID);}
//        catch(Exception e){
//        }
        return true;
    }

    public synchronized double getBalance(String customerID) {
    	System.out.println("Replica Two...");
    	CustomerClient temp = findClient(customerID);

        if(temp != null){
//            try{
//                //log("Retrieved Balance for Clients Account with ID: " + customerID);}
//            catch(Exception e){
//            }
            return temp.getBalance();
        }
        return 0;
    }
    
 

    public void generateInfo(CustomerClient client){

        if(client == null){
            System.out.println("Error: Client does not exist!");
        }
        String firstName = client.getFirstName();
        String lastName = client.getLastName();
        String phone = client.getPhone();
        String accNum = client.getAccountNumber();
        String branch = client.getBranch();
        String address = client.getAddress();
        Double balance = client.getBalance();

        System.out.println("-----------------------------------------------");
        System.out.println("First Name: " + firstName + "\nLast Name: " + lastName + "\nAddress: " + address +
                "\nAccount Number: " + accNum + "\nBranch: " + branch + "\nBalance: $" + balance);
        System.out.println("-----------------------------------------------");
    }

    //add a main to invoke the server
    public static void main(String [] args ){


        ReplicaTwo replicaTwo = new ReplicaTwo();
        replicaTwo.runUDPServer();

    }

    private void runUDPServer() {


        DatagramSocket serverSocket = null;
        try {
            serverSocket = new DatagramSocket(7655);
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
