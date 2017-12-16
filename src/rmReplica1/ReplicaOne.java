package rmReplica1;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import JamesClient.CustomerClient;

import static log.Log.generateLogFileServer;


//JAMES
public class ReplicaOne {

	static private HashMap<Character, ArrayList<CustomerClient>> hashMap = new HashMap<>();
    static int iD = 1000;
    private HashMap<String, Integer> allBranchCount = new HashMap<>();

    private HashMap<String, Integer>serverPort = new HashMap<>();

    //lock object
    Object lock = new Object();

    //setter for ORB

    public ReplicaOne() {
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

        synchronized (hashMap) {

        }


    }

    public synchronized boolean createAccountRecord(String firstName, String lastName, String address, String phone, String branch) {
    	System.out.println("Replica One...");

        int noErrorCount = 0;
        System.out.println("Inside createAccountRecord()...");
        //create ID
        String generateID = generateID(branch);
        //create a customer object
        CustomerClient customerClient = new CustomerClient(firstName, lastName, generateID, address, phone, branch);
        //customerClient.setBranch(branch);
        //To uppercase
        Character key = lastName.toUpperCase().charAt(0);
        //If key doesnt exist create one, and start the arraylist
        if (hashMap.containsKey(key)) {
            //get the arrayList if the key is present
            ArrayList<CustomerClient> temp = hashMap.get(key);
            temp.add(customerClient);
            hashMap.put(key, temp);
            noErrorCount++;

        } else {
            //Create a temp ArrayList for the value
            ArrayList<CustomerClient> temp = new ArrayList<CustomerClient>();
            temp.add(customerClient);
            //add to the map
            hashMap.put(key, temp);
            noErrorCount++;

        }
        // customerClient.toString();

        //print content of the arraylist for testing
        ArrayList<CustomerClient> temp = hashMap.get(key);
        for (CustomerClient cc : temp
                ) {
            System.out.println(cc.toString());
        }

        if (noErrorCount > 0) {
            //log stuff
            try {
                String msg = "Account created " + customerClient.getAccountNumber();
                //generateLogFileServer(msg, "Server" , "BCServer");
            } catch (Exception e) {
                e.printStackTrace();
            }

            return true;
        } else
            //log stuff
            try {
                String msg = "Account creation failed";
                //generateLogFileServer(msg, "Server" , "BCServer");
            } catch (Exception e) {
                e.printStackTrace();
            }
        return false;

    }

    private String generateID(String branch) {


        String ID = branch + "C" + iD;
        System.out.println(ID);
        iD++;
        return ID;


    }

    public synchronized boolean editRecord(String customerID, String fieldName, String newValue) {

    	System.out.println("Replica One...");
        //Check if the field
        if (fieldName.equalsIgnoreCase("branch")) {
            if (newValue.equalsIgnoreCase("QC") || newValue.equalsIgnoreCase("BC") ||
                    newValue.equalsIgnoreCase("MB") || newValue.equalsIgnoreCase("NB")) {

                //Lookup customer from the hash map
                CustomerClient customerClient = varifiedEntryLookForUser(customerID);
                if (customerClient != null) {
                    customerClient.setBranch(newValue);
                    //log stuff
                    try {
                        String msg = "Field name change for " + customerID;
                        //generateLogFileServer(msg, "Server" , "BCServer");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return true;
                }
            } else {
                System.out.println("Invalid branch name. Please select from QC, BC, NB or MB");
                return false;
            }
        }
        //check if the user wants to change phone
        else if (fieldName.equalsIgnoreCase("phone")) {
            CustomerClient customerClient = varifiedEntryLookForUser(customerID);
            if (customerClient != null) {
                customerClient.setPhone(newValue);
                System.out.println("Updated phone number! of Client " + customerClient.getAccountNumber() +
                        " to " + customerClient.getPhone());
                //log stuff
                try {
                    String msg = "Field name change for " + customerID;
                    //generateLogFileServer(msg, "Server" , "BCServer");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            }

        }
        //check if the user wants to change address
        else if (fieldName.equalsIgnoreCase("address")) {
            CustomerClient customerClient = varifiedEntryLookForUser(customerID);
            if (customerClient != null) {
                customerClient.setAddress(newValue);
                //log stuff
                try {
                    String msg = "Field name change for " + customerID;
                    //generateLogFileServer(msg, "Server" , "BCServer");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            }
        } else {
            System.out.println("Invalid field name. Please select branch, address or phone!");
            return false;
        }

        System.out.println("User with " + customerID + " doesn't exist in the DB");

        return false;



    }

    private CustomerClient varifiedEntryLookForUser(String customerID) {

        ArrayList<CustomerClient> temp;
        CustomerClient tempClient = null;
        for (Character key : hashMap.keySet()) {

            temp = hashMap.get(key);

            for (CustomerClient client : temp) {
                if (client.getAccountNumber().equalsIgnoreCase(customerID)) {
                    return client;
                }
            }
        }
        return tempClient;
    }


    public synchronized String getAccountCount() {
    	System.out.println("Replica One...");
        int bcCount = 0;
        int qcCount = 0;
        int nbCount = 0;
        int mbCount = 0;

        //iterate through all the


        // Getting a Set of Key-value pairs
        Set entrySet = hashMap.entrySet();


        // Iterate through HashMap entries(Key-Value pairs)
        for (Object anEntrySet : entrySet) {
            Map.Entry me = (Map.Entry) anEntrySet;

            //System.out.println("Key is: " + me.getKey());
            //iterate with the arraylist
            ArrayList<CustomerClient> temp = hashMap.get(me.getKey());
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
    	System.out.println("Replica One...");
    	//check if there's enough balance

        String sourceSever = sourceCustomerID.substring(0,2);
        sourceSever = sourceSever + "Server";
        Boolean hasEnoughBalance = checkIfEnoughBalance(sourceCustomerID, amount);


        System.out.println(hasEnoughBalance + " reply from has enough balance!");

        //if there's enough money send the money
        if(hasEnoughBalance) {
            // transfer the money

            //withdraw the money from the source and deposit to the source
            boolean sendCash = withdraw(sourceCustomerID, amount);
            boolean gotTheCash = deposit(destinationCustomerID, amount);


            System.out.println(sendCash + " reply from withdraw");
            System.out.println(gotTheCash + " reply from deposit");


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

    private Boolean checkIfEnoughBalance(String sourceCustomerID, double amount) {
        CustomerClient customerClient = varifiedEntryLookForUser(sourceCustomerID);
        return customerClient != null && amount < getBalance(customerClient.getAccountNumber());
    }

    public synchronized boolean deposit(String customerID, double amt) {
    	System.out.println("Replica One...");
        double total = 0;
        CustomerClient customerClient = varifiedEntryLookForUser(customerID);
        if(customerClient != null){
            total = customerClient.getBalance();
            total += amt;
            customerClient.setBalance(total);
            System.out.println("Money deposited. Customer " + customerID + " has $" + total + " in the account");

            //log stuff
            try {
                String msg = "Deposit made for " + customerID;
                generateLogFileServer(msg, "Server" , "BCServer");
            } catch (IOException e) {
                e.printStackTrace();
            }

            return true;
        }
        else{
            System.out.println("Customer " + customerID + " doesn't exist in the database!");
            ///log stuff
            try {
                String msg = "Deposit failed " + customerID;
                generateLogFileServer(msg, "Server" , "BCServer");
            } catch (IOException e) {
                e.printStackTrace();
            }


            return false;
        }
    }

    public synchronized boolean withdraw(String customerID, double amt) {
    	System.out.println("Replica One...");
        double total = 0;
        CustomerClient customerClient = varifiedEntryLookForUser(customerID);
        if(customerClient != null){
            total = customerClient.getBalance();
            total -= amt;
            customerClient.setBalance(total);
            System.out.println("Money withdrawn. Customer " + customerID + " has $" + total + " in the account");
            //log stuff
            try {
                String msg = "Withdraw  " + customerID;
                generateLogFileServer(msg, "Server" , "BCServer");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }
        else{
            System.out.println("Customer " + customerID + " doesn't exist in the database!");
            //log stuff
            try {
                String msg = "Customer doesnt exist " + customerID;
                generateLogFileServer(msg, "Server" , "BCServer");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }

    }

    public synchronized double getBalance(String customerID) {
    	System.out.println("Replica One...");
        double total = 0;
        CustomerClient customerClient = varifiedEntryLookForUser(customerID);
        if(customerClient != null){
            total = customerClient.getBalance();
            System.out.println("Customer " + customerID + " has $" + total + " in the account");
            //log stuff
            try {
                String msg = "Get balance for " + customerID;
                generateLogFileServer(msg, "Server" , "BCServer");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return total;
        }
        else{
            System.out.println("Customer " + customerID + " doesn't exist in the database!");
            return 0;
        }

    }




    //add a main to invoke the server
    public static void main(String [] args ){


        ReplicaOne replicaOne = new ReplicaOne();
        replicaOne.runUDPServer();

    }

    private void runUDPServer() {


        DatagramSocket serverSocket = null;
        try {
            serverSocket = new DatagramSocket(7654);
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



