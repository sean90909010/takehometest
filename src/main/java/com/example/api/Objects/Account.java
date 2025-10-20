package com.example.api;

public class Account {

    private String accountNumber;
    private String sortCode;
    private String name;
    private String accountType;
    private double balance;
    private String currency;
    private DateTime createdTimestamp;
    private DateTime updatedTimestamp;

    private List<Transaction> transactions;

    public Account(String accountNumber, String sortCode, String name, String accountType, double balance,
            String currency) {
        setAccountNumber(accountNumber);
        setSortCode(sortCode);
        setName(name);
        setAccountType(accountType);
        setBalance(balance);
        setCurrency(currency);
        this.createdTimestamp = LocalDateTime.now();
        setUpdatedTimestamp(LocalDateTime.now());

        transactions = new ArrayList<>();
    }

    public String getAccountNumber() {
        return accountNumber;
    }
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }
    public String getSortCode() {
        return sortCode;
    }
    public void setSortCode(String sortCode) {
        this.sortCode = sortCode;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getAccountType() {
        return accountType;
    }
    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }
    public double getBalance() {
        return balance;
    }
    public void setBalance(double balance) {
        this.balance = balance;
    }
    public String getCurrency() {
        return currency;
    }
    public void setCurrency(String currency) {
        this.currency = currency;
    }
    public DateTime getCreatedTimestamp() {
        return createdTimestamp;
    }
    public DateTime getUpdatedTimestamp() {
        return updatedTimestamp;
    }
    public void setUpdatedTimestamp(DateTime updatedTimestamp) {
        this.updatedTimestamp = updatedTimestamp;
    }
    public List<Transaction> getTransactions() {
        return transactions;
    }
    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }
    public void addTransaction(Transaction transaction) {
        this.transactions.add(transaction);
    }

}
