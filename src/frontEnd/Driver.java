package frontEnd;

import bankingOperationsApp.BankingOperations;
import bankingOperationsApp.BankingOperationsHelper;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;

import java.text.DecimalFormat;
import java.util.Scanner;

public class Driver {

	private BankingOperations myOperations;
	
	public BankingOperations getMyOperations() {
		return myOperations;
	}
	public void findCORBA() throws Exception {
		
		  String[] arguments = {"-ORBInitialPort", "1050", "-ORBInitialHost", "localhost"};

	        ORB orb = ORB.init(arguments, null);
	        org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
	        NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
	        myOperations = BankingOperationsHelper.narrow(ncRef.resolve_str("FE"));
		
	}
	
    public static void main(String args[]) throws Exception {
    
    	Driver driver = new Driver();
    	driver.findCORBA();
        
        DecimalFormat df = new DecimalFormat("0.00");

        boolean cindy = driver.getMyOperations().createAccountRecord("Cindy", "Blasin", "2070MontangneOuest", "(438)3436578", "QC");
        boolean sophie = driver.getMyOperations().createAccountRecord("Sophie", "Cruse", "2070MontangneOuest", "(514)2236578", "QC");
        boolean sandra = driver.getMyOperations().createAccountRecord("Sandra", "Smith", "2070MontangneOuest", "(438)3436998", "QC");
       
        System.out.println("Create Account Record operation is " + cindy);
       
        boolean cindyDeposit = driver.getMyOperations().deposit("QCC1000", 200);
        System.out.println("Deposit operation is " + cindyDeposit);
        
        double cindyDepositBal = driver.getMyOperations().getBalance("QCC1000");
        System.out.println("QCC1000 new balance operation" + cindyDepositBal);
        
        double sophieBal = driver.getMyOperations().getBalance("QCC1001");
        System.out.println("QCC1001 current balance operation" + sophieBal);
        boolean cindyTransfer = driver.getMyOperations().transferFund("auto", 100, "QCC1000", "QCC1001");
        System.out.println("Transfer operation " + cindyTransfer);
        double cindyBal = driver.getMyOperations().getBalance("QCC1000");
        System.out.println("QCC1000 new balance operation" + cindyBal);
        double sophieNewBal = driver.getMyOperations().getBalance("QCC1001");
        System.out.println("QCC1001 new balance operation" + sophieNewBal);
        driver.getMyOperations().getAccountCount();

        

        
//        System.out.println(cindy + ", " + sophie + ", " + sandra);
//
//        boolean jack = driver.getMyOperations().createAccountRecord("Jack", "Smith", "2070 St. Mathieu", "(438)343-6578", "NB");
//        boolean jill = driver.getMyOperations().createAccountRecord("Jill", "Smith", "2070 St. Mathieu", "(514)223-6578", "NB");
//        boolean jackBal = driver.getMyOperations().deposit("NBC1000", 200);
//
//
//        System.out.println(jack + ", " + jill);
//
//        boolean hani = driver.getMyOperations().createAccountRecord("Hani", "Brango", "125 Sunway Ouest", "(438)343-6578", "MB");
//        boolean hassan = driver.getMyOperations().createAccountRecord("Hassan", "Dangjo", "125 Sunway Ouest", "(514)223-6578", "MB");
//        boolean houria = driver.getMyOperations().createAccountRecord("Houria", "Defly", "123 Sunway Ouest", "(438)343-6578", "MB");
//        boolean hamza = driver.getMyOperations().createAccountRecord("Hamza", "Detres", "124 Sunway Ouest", "(514)223-6578", "MB");
//
//        driver.getMyOperations().deposit("MBC1000", 100.60);
////        driver.getMyOperations().withdraw("MBC1000", 90.6);
////        driver.getMyOperations().editRecord("MBC1002", "Name", "Bill");
////        driver.getMyOperations().editRecord("MBC1002", "phone", "(555)555-5555");
//
//        System.out.println(hani + ", " + hassan + ", " + houria + ", " + hamza);
//
//        boolean sally = driver.getMyOperations().createAccountRecord("Sally", "Chen", "6757 St. Arthur", "(438)754-6998", "BC");
//      
//        boolean jason = driver.getMyOperations().createAccountRecord("Jason", "Chou", "4532 St. Arthur", "(438)978-6998", "BC");
//        boolean james = driver.getMyOperations().createAccountRecord("James", "Chien", "3211 St. Arthur", "(438)867-4564", "BC");
//        boolean john = driver.getMyOperations().createAccountRecord("John", "Chat", "3025 St. Arthur", "(438)345-3455", "BC");
//        boolean lila = driver.getMyOperations().createAccountRecord("Lila", "Yang", "1111 St. Arthur", "(438)657-8768", "BC");
//        boolean yasmine = driver.getMyOperations().createAccountRecord("Yasmine", "Caman", " St. Arthur", "(438)543-2323", "BC");

//		  System.out.println(sally + ", " + jason + ", " + james + ", " + john + ", " + lila + ", " + yasmine);
        //driver.getMyOperations().deposit("BCC1000", 100.60);
       // driver.getMyOperations().getBalance("BCC1000");

//        driver.getMyOperations().getAccountCount();
//        driver.getMyOperations().getAccountCount();
//        driver.getMyOperations().getAccountCount();
//        driver.getMyOperations().getAccountCount();

        while(true) {

        	String user = "";
            String branch = "";
            int option = 0;
            Scanner scan = new Scanner(System.in);
            boolean exit = true;

            System.out.println("-----------------------------------------------");
            System.out.println("|       Welcome To Your Banking Platform       |");
            System.out.println("-----------------------------------------------");

            System.out.print("\nEnter UserID: ");
            user = scan.nextLine();
            branch = user.substring(0, 2);

            while (exit) {
                if (user.charAt(2) == 'm' || user.charAt(2) == 'M') {

                    System.out.println("Select Option:\n 1.Create new account\n 2.Edit account\n 3.Get Account Count\n 4.Deposit \n 5.Withdraw \n 6.Get balance \n 7.Money Transfer \n 8.Exit");
                    option = scan.nextInt();
                    scan.nextLine();
                    switch (option) {
                        case 1:
                            System.out.print("Enter phone number: ");
                            String phoneNumber = scan.nextLine();
                            System.out.print("Enter address: ");
                            String address = scan.nextLine();
                            System.out.print("Enter family name: ");
                            String lastName = scan.nextLine();
                            System.out.print("Enter first name: ");
                            String firstName = scan.nextLine();
                            System.out.println("");
                            if (driver.getMyOperations().createAccountRecord(firstName, lastName, address, phoneNumber, branch) == true) {
                                System.out.println("SUCCESS: Added new client!");
                            } else {
                                System.out.println("ERROR: Count not add new client!");
                            }
                            break;

                        case 2:
                            System.out.print("Enter client ID: ");
                            String customerID = scan.next();
                            System.out.print("Enter field name: ");
                            String fieldName = scan.next();
                            System.out.print("Enter new value: ");
                            String newValue = scan.next();
                            if (driver.getMyOperations().editRecord(customerID, fieldName, newValue) == true) {
                                System.out.println("SUCCESS: Altered account with ID: " + customerID);
                            } else {
                                System.out.println("ERROR: Could not alter account with ID: " + customerID);
                            }
                            break;

                        case 3:

                        	 String[] temp = driver.getMyOperations().getAccountCount();
                             System.out.println("******** Account Count In Each Branch *********");
                             System.out.println("-----------------------------------------------");
                            
                                 for(String s: temp) {
                                	 
                                	 System.out.print(s + " ");	 
                                	 
                                 }
                             
                                 System.out.println();
                             System.out.println("-----------------------------------------------");

                             break;

                        case 4:
                            System.out.print("Enter client ID: ");
                            String id = scan.nextLine();
                            System.out.print("Enter amount to deposit: ");
                            double amt = scan.nextDouble();
                            scan.nextLine();
                            if (driver.getMyOperations().deposit(id, amt) == true) {
                                System.out.println("SUCCESS: Deposited $" + amt + " into account with ID: " + id);
                            } else {
                                System.out.println("ERROR: Could not deposit $" + amt + " into account with ID: " + id);

                            }
                            break;

                        case 5:
                            System.out.print("Enter client ID: ");
                            String clientId = scan.nextLine();
                            System.out.print("Enter amount to withdraw: ");
                            double amount = scan.nextDouble();
                            scan.nextLine();
                            System.out.println("");
                            if (driver.getMyOperations().withdraw(clientId, amount) == true) {
                                System.out.println("SUCCESS: Withdrew $" + amount + " from account with ID:" + clientId);
                            } else {
                                System.out.println("ERROR: Could not withdraw $" + amount + " from account with ID: " + clientId);
                            }
                            break;

                        case 6:
                            System.out.print("Enter client ID: ");
                            String userId = scan.nextLine();

                            double bal = driver.getMyOperations().getBalance(userId);
                            System.out.println("Balance: $" + df.format(bal));
                            break;

                        case 7:
                            System.out.print("Enter account number which you wish to transfer from: ");
                            String source = scan.next();
                            System.out.print("Enter account number which you wish to transfer to: ");
                            String dest = scan.next();
                            System.out.print("Enter amount you wish to transfer: ");
                            Double value = scan.nextDouble();

                            boolean success = driver.getMyOperations().transferFund(user, value, source, dest);

                            if (success) {
                                System.out.println("Transfer to: " + dest + " was successful!");
                            } else {
                                System.out.println("Transfer to: " + dest + " was NOT successful!");
                            }

                            break;

                        case 8:
                            System.out.println("Thanks for your visit...Goodbye!");
                            exit = false;
                            break;

                        default:
                            System.out.println("Invalid Option Choice");
                            break;

                    }


                } else if (user.charAt(2) == 'c' || user.charAt(2) == 'C') {

                    System.out.println("Select Option:\n 1.Deposit \n 2.Withdraw \n 3.Get balance \n 4.Money Transfer \n 5.Exit");
                    option = scan.nextInt();
                    scan.nextLine();

                    switch (option) {
                        case 1:
                            System.out.print("Enter amount to deposit: ");
                            double amt = scan.nextDouble();
                            scan.nextLine();
                            if (driver.getMyOperations().deposit(user, amt) == true) {
                                System.out.println("SUCCESS: Deposited $" + amt + " into account.");

                            } else {
                                System.out.println("ERROR: Could not deposit $" + amt + " into account.");
                            }

                            break;

                        case 2:
                            System.out.print("Enter amount to withdraw: ");
                            double amount = scan.nextDouble();
                            scan.nextLine();
                            if (driver.getMyOperations().withdraw(user, amount) == true) {
                                System.out.println("SUCCESS: Withdrew $" + amount + " from account.");
                            } else {
                                System.out.println("ERROR: Could not withdraw $" + amount + " from account.");
                            }
                            break;

                        case 3:
                            double temp = driver.getMyOperations().getBalance(user);
                            System.out.println("Balance: $" + df.format(temp));
                            break;

                        case 4:
                            System.out.print("Enter account number which you wish to transfer to: ");
                            String dest = scan.next();
                            System.out.print("Enter amount you wish to transfer: ");
                            Double value = scan.nextDouble();

                            boolean success = driver.getMyOperations().transferFund("auto", value, user, dest);

                            if (success) {
                                System.out.println("Transfer to: " + dest + " was successful!");
                            } else {
                                System.out.println("Transfer to: " + dest + " was NOT successful!");
                            }

                            break;

                        case 5:
                            System.out.println("Thanks for your visit...Goodbye!");
                            exit = false;
                            break;
                        default:
                            System.out.println("Invalid Option Choice");
                            break;

                    }

                } else {
                    System.out.println("Error: User does not exist!");
                    exit = false;
                }

            }
        }
    }
}
