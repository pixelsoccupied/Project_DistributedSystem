package YasmineClient;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class CustomerClient {

	private String accountNumber;
	private String phone;
	private String address;
	private String firstName;
	private String lastName;
	private double balance;
	private String branch;
	
	public CustomerClient(String firstName, String lastName, String phone, String address) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.phone = phone;
		this.address = address;
		this.balance = 0;
	}

	public String getBranch() {
		return branch;
	}

	public void setBranch(String branch) {
		this.branch = branch;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getLastName() { return lastName; }

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public void setFirstName(String firstName) { this.firstName = firstName; }

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public double getBalance() {
		return balance;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}

	Logger logger = Logger.getLogger("log");
	FileHandler handler;

	public void log(String msg, String clientType, String clientID) throws SecurityException, IOException {

		handler = new FileHandler("C:\\Users\\yasmine\\workspace\\SOEN423-CORBA\\ClientLogs\\"
					+ clientID +".log",true);
		logger.addHandler(handler);
		SimpleFormatter formatter = new SimpleFormatter();
		handler.setFormatter(formatter);
		logger.log(Level.INFO, msg);
		handler.close();
	}

}