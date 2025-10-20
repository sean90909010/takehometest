package com.example.api;

public class Transaction {
    
    private double amount;
    private String currency;
    private String type;

    public Transaction(double amount, String currency, String type) {
        setAmount(amount);
        setCurrency(currency);
        setType(type);
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
