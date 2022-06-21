package com.mindhub.homebanking.dtos;

import com.mindhub.homebanking.models.CardType;

import java.time.LocalDateTime;


public class CardApplicationDTO {
    private String number;
    private int cvv;
    private double amount;
    private String email;
    private String description;


    public CardApplicationDTO() {}

    public CardApplicationDTO(String number, int cvv, double amount, String email, String description) {
        this.number = number;
        this.cvv = cvv;
        this.amount = amount;
        this.email = email;
        this.description = description;
    }

    public String getNumber() {
        return number;
    }
    public int getCvv() {
        return cvv;
    }

    public double getAmount() {
        return amount;
    }

    public String getEmail() {
        return email;
    }

    public String getDescription() {
        return description;
    }
}






