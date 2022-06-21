package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dtos.LoanApplicationDTO;
import com.mindhub.homebanking.dtos.LoanDTO;
import com.mindhub.homebanking.models.*;
import com.mindhub.homebanking.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

import static com.mindhub.homebanking.models.TransactionType.CREDITO;

@RestController
@RequestMapping("/api")
public class LoanController {
    @Autowired
    private ClientService clientService;
    @Autowired
    private LoanService loanService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private ClientLoanService clientLoanService;
    @Autowired
    private TransactionService transactionService;


    @GetMapping("/loans")
    public List<LoanDTO> getLoans() {
        return loanService.getLoansDTO();
    }

    @PostMapping("/createLoan")
    public ResponseEntity<Object> createLoans (@RequestBody Loan loan) {

        Loan newLoan = new Loan(loan.getName(), loan.getMaxAmount(), loan.getPayments(), loan.getInterest());
        loanService.saveLoan(newLoan);

        return new ResponseEntity<>("Loan created" , HttpStatus.CREATED);
    }


    @Transactional
    @PostMapping(path = "/loans")
    public ResponseEntity<Object> newLoans (@RequestBody LoanApplicationDTO loanApplicationDTO, Authentication authentication) {

        Client client = clientService.getClientCurrent(authentication);
        Loan loan = loanService.getLoanById(loanApplicationDTO.getId());
        Account accountDestination = accountService.getAccountByNumber(loanApplicationDTO.getAccountDestination());

        if (loanApplicationDTO.getAccountDestination().isEmpty() || loanApplicationDTO.getAmount() <= 0 || loanApplicationDTO.getPayments() <= 0) {

            return new ResponseEntity<>("Missing data", HttpStatus.FORBIDDEN);
        }
        if (loan == null) {

            return new ResponseEntity<>("Nonexistent loan", HttpStatus.FORBIDDEN);
        }
        if (loanApplicationDTO.getAmount() >= loan.getMaxAmount()) {

            return new ResponseEntity<>("The amount exceeds the maximum available", HttpStatus.FORBIDDEN);
        }
        if (!loan.getPayments().contains(loanApplicationDTO.getPayments())) {

            return new ResponseEntity<>("Number of payments not available", HttpStatus.FORBIDDEN);
        }
        if (accountDestination == null) {

            return new ResponseEntity<>("Destination account does not exist", HttpStatus.FORBIDDEN);
        }
        if (!client.getAccount().contains(accountDestination)) {

            return new ResponseEntity<>("The destination account does not belong to you", HttpStatus.FORBIDDEN);
        }
        if (client.getLoans().contains(loan)) {

            return new ResponseEntity<>("You already have a loan of this type", HttpStatus.FORBIDDEN);
        }

        ClientLoan clientLoan = new ClientLoan(loanApplicationDTO.getAmount() + (loanApplicationDTO.getAmount() * loan.getInterest()/100), loanApplicationDTO.getPayments(), client, loan);
        clientLoanService.saveClientLoan(clientLoan);

        accountDestination.setBalance(accountDestination.getBalance() + loanApplicationDTO.getAmount());
        accountService.saveAccount(accountDestination);

        Transaction transaction = new Transaction(CREDITO, loanApplicationDTO.getAmount(), "loan approved-" + loan.getName(), LocalDateTime.now(), accountDestination.getBalance(), true, accountDestination);
        transactionService.saveTransaction(transaction);

        return new ResponseEntity<>("Loan completed" , HttpStatus.CREATED);

    }




}
