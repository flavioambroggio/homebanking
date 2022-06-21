package com.mindhub.homebanking.services;

import com.mindhub.homebanking.models.Card;

public interface CardService {
    void saveCard (Card card);
    Card getCardById(Long id);
    Card getCardByNumber(String number);

}
