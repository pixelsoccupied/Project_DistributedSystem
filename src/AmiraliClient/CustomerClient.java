package AmiraliClient;

public class CustomerClient {
	
	private String firstName;
	private String lastName;
	private String accountNumber;
	private String address;
	private String phoneNumber;
	private String branchName;
	private double balance;

	public CustomerClient(String firstName, String lastName, String accountNumber, String address, String phoneNumber,
			String branchName, double balance) {

		this.firstName = firstName;
		this.lastName = lastName;
		this.accountNumber = accountNumber;
		this.address = address;
		this.phoneNumber = phoneNumber;
		this.branchName = branchName;
		this.balance = balance;
	}


	// default constructor
	public CustomerClient() {
	
	}


	public double getBalance() {
		return balance;
	}



	public void setBalance(double balance) {
		this.balance = balance;
	}



	public String getBranchName() {
		return branchName;
	}



	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}


	public String getFirstName() {
		return firstName;
	}



	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}



	public String getLastName() {
		return lastName;
	}



	public void setLastName(String lastName) {
		this.lastName = lastName;
	}



	public String getAccountNumber() {
		return accountNumber;
	}



	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}



	public String getAddress() {
		return address;
	}



	public void setAddress(String address) {
		this.address = address;
	}



	public String getPhoneNumber() {
		return phoneNumber;
	}



	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	
}
