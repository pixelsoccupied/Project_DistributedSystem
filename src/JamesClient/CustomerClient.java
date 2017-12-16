package JamesClient;

import java.util.HashMap;

public class CustomerClient {
    public static HashMap<String, String[]> portArg = new HashMap<>();


    private String firstName;
    private String lastName;
    private String accountNumber;
    private String address;
    private String phone;
    private double balance;
    private String branch;

    public CustomerClient(String firstName, String lastName, String accountNumber, String address, String phone) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.accountNumber = accountNumber;
        this.address = address;
        this.phone = phone;
    }

    public CustomerClient(String firstName, String lastName, String accountNumber, String address, String phone, String branch) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.accountNumber = accountNumber;
        this.address = address;
        this.phone = phone;
        this.branch = branch;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    @Override
    public String toString() {
        return "Customer -----------------" +
                "\nFirst Name - " + firstName +
                "\nLast Name - " + lastName +
                "\nAccount Number - " + accountNumber +
                "\nAddress - " + address +
                "\nPhone #" + phone +
                "\nBalance $" + balance +
                "\nBranch -" + branch +
                "\n-------------";
    }

}
