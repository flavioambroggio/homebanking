package com.mindhub.homebanking.dtos;


public class LoanApplicationDTO {
    private long id;
    private double amount;
    private int payments;
    private String accountDestination;


    public LoanApplicationDTO() {}

    public LoanApplicationDTO (long id, double amount, int payments, String accountDestination) {
        this.id = id;
        this.amount = amount;
        this.payments = payments;
        this.accountDestination = accountDestination;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
    public int getPayments() {
        return payments;
    }

    public void setPayments(int payments) {
        this.payments = payments;
    }
    public String getAccountDestination() {
        return accountDestination;
    }

    public void setAccountDestination(String accountDestination) {
        this.accountDestination = accountDestination;
    }

}
