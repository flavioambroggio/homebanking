package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dtos.AccountDTO;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.AccountType;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.models.Transaction;
import com.mindhub.homebanking.services.AccountService;
import com.mindhub.homebanking.services.ClientService;
import com.mindhub.homebanking.services.TransactionService;
import com.mindhub.homebanking.utils.AccountUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api")
public class AccountController {
    @Autowired
    private AccountService accountService;
    @Autowired
    private ClientService clientService;
    @Autowired
    private TransactionService transactionService;


    @GetMapping("/accounts")
    public List<AccountDTO> getAccounts() {
        return accountService.getAccountsDTO();
    }

    @GetMapping("accounts/{id}")
    public AccountDTO getAccount(@PathVariable Long id, Authentication authentication){
        return accountService.getAccountDTO(id, authentication);
    }


    @PostMapping(path = "/clients/current/accounts")
    public ResponseEntity<Object> newAccount (@RequestParam AccountType type, Authentication authentication) {

        Client client = clientService.getClientCurrent(authentication);

        if(client.getAccount().size() >= 3 ) {
            return new ResponseEntity<>("No puedes tener mas de 3 cuentas", HttpStatus.FORBIDDEN);
        }
        if(type == null) {
            return new ResponseEntity<>("Dede elegir el tipo de cuenta", HttpStatus.FORBIDDEN);
        }

        Account account = new Account("VIN" + AccountUtils.getRandomNumber(10000000, 99999999), LocalDateTime.now(), 0, type, true, client);
        accountService.saveAccount(account);

        return new ResponseEntity<>("Cuenta creada", HttpStatus.CREATED);

    }

    @PatchMapping("/clients/current/accounts/{id}")
    public ResponseEntity<Object> hideAccount (@PathVariable Long id) {

        Account account = accountService.getAccountById(id);

        if(account.getBalance() > 0){
            return new ResponseEntity<>("Balance greater than 0", HttpStatus.FORBIDDEN);
        }

        Set<Transaction> transactions = account.getTransactions();
        transactions.forEach(transaction -> transaction.setVisibility(false));
        transactions.forEach(transaction -> transactionService.saveTransaction(transaction));

        account.setVisibility(false);
        accountService.saveAccount(account);

        return new ResponseEntity<>("Hidden Account", HttpStatus.ACCEPTED);
    }



}
