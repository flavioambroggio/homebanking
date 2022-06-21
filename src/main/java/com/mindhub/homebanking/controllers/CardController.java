package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dtos.CardApplicationDTO;
import com.mindhub.homebanking.models.*;
import com.mindhub.homebanking.services.AccountService;
import com.mindhub.homebanking.services.CardService;
import com.mindhub.homebanking.services.ClientService;
import com.mindhub.homebanking.services.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

import static com.mindhub.homebanking.utils.CardUtils.completarCifras;
import static com.mindhub.homebanking.utils.CardUtils.getRandomNumber;


@RestController
@RequestMapping("/api")
public class CardController {
    @Autowired
    private CardService cardService;
    @Autowired
    private ClientService clientService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private TransactionService transactionService;


    @PostMapping (path = "/clients/current/cards")
    public ResponseEntity<Object> newCard (@RequestParam CardType type, @RequestParam CardColor color , Authentication authentication) {

        Client client = clientService.getClientCurrent(authentication);
        Set<Card> cards = client.getCards().stream().filter(card -> card.getType().equals(type)).collect(Collectors.toSet());

        if(cards.size() >=  3) {
            return new ResponseEntity<>("No puedes tener mas de 3 tarjetas del mismo tipo", HttpStatus.FORBIDDEN);
        }

        Card card = new Card(client.getFirstName()+" "+client.getLastName(), type, color,
                completarCifras(getRandomNumber(1, 10000)) + "-" + completarCifras(getRandomNumber(1, 10000)) + "-" + completarCifras(getRandomNumber(1, 10000)) + "-" + completarCifras(getRandomNumber(1, 10000)),
                getRandomNumber(100, 1000), LocalDateTime.now(), LocalDateTime.now().plusYears(5), true, false, client);
        cardService.saveCard(card);

        return new ResponseEntity<>("Tarjeta creada" , HttpStatus.CREATED);

    }

    /*
    @DeleteMapping("/clients/current/cards/{id}")
    public ResponseEntity<Object> deleteCard (@PathVariable Long id) {

        Card card = cardService.getCardById(id);
        cardService.deleteCard(card);

        return new ResponseEntity<>("Delete Card" , HttpStatus.ACCEPTED);

    }
    */

    @PatchMapping("/clients/current/cards/{id}")
    public ResponseEntity<Object> hideCard (@PathVariable Long id) {

        Card card = cardService.getCardById(id);

        card.setVisibility(false);
        cardService.saveCard(card);

        return new ResponseEntity<>("Hidden Card" , HttpStatus.ACCEPTED);
    }

    @PatchMapping("/clients/current/cards/expired/{id}")
    public ResponseEntity<Object> expiredCard (@PathVariable Long id) {

        Card card = cardService.getCardById(id);

        card.setExpired(true);
        cardService.saveCard(card);

        return new ResponseEntity<>("Expired Card" , HttpStatus.ACCEPTED);
    }

    @CrossOrigin
    @Transactional
    @PostMapping (path = "/clients/cards/payment")
    public ResponseEntity<Object> paymentCard (@RequestBody CardApplicationDTO cardApplicationDTO) {

        LocalDateTime today = LocalDateTime.now();

        if(cardApplicationDTO.getNumber().isEmpty() || cardApplicationDTO.getAmount() <= 0 ||
                cardApplicationDTO.getDescription().isEmpty() || cardApplicationDTO.getCvv() <= 0 ||
        cardApplicationDTO.getEmail().isEmpty()) {
            return new ResponseEntity<>("Missing data", HttpStatus.FORBIDDEN);
        }
        if(cardApplicationDTO.getNumber().length() < 19) {
            return new ResponseEntity<>("Incomplete card number", HttpStatus.FORBIDDEN);
        }

        Card card = cardService.getCardByNumber(cardApplicationDTO.getNumber());

        if(card == null) {
            return new ResponseEntity<>("Nonexistent card", HttpStatus.FORBIDDEN);
        }

        Client client = card.getClient();

        if(!client.getEmail().equals(cardApplicationDTO.getEmail())) {
            return new ResponseEntity<>("The card does not belong to you", HttpStatus.FORBIDDEN);
        }

        Account account = client.getAccount().stream().filter(account1 -> account1.getBalance() >= cardApplicationDTO.getAmount()).findFirst().orElse(null);

        if(account == null) {
            return new ResponseEntity<>("You do not have sufficient funds in any account", HttpStatus.FORBIDDEN);
        }
        if(!cardApplicationDTO.getNumber().equals(card.getNumber())) {
            return new ResponseEntity<>("Wrong card number", HttpStatus.FORBIDDEN);
        }
        if (card.getThruDate().isBefore(today)) {
            return new ResponseEntity<>("Expired card", HttpStatus.FORBIDDEN);
        }
        if(cardApplicationDTO.getCvv() != card.getCvv()) {
            return new ResponseEntity<>("Wrong card security code", HttpStatus.FORBIDDEN);
        }
        if (account.getBalance() < cardApplicationDTO.getAmount()) {
            return new ResponseEntity<>("Amount not available", HttpStatus.FORBIDDEN);
        }

        account.setBalance(account.getBalance() - cardApplicationDTO.getAmount());
        accountService.saveAccount(account);

        Transaction transaction = new Transaction(TransactionType.DEBITO, cardApplicationDTO.getAmount(), cardApplicationDTO.getDescription(), LocalDateTime.now(), account.getBalance(), true, account);
        transactionService.saveTransaction(transaction);

        return new ResponseEntity<>("Payment completed" , HttpStatus.CREATED);
    }




}
