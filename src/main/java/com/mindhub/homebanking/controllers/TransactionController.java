package com.mindhub.homebanking.controllers;


import com.lowagie.text.*;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.models.Transaction;
import com.mindhub.homebanking.services.AccountService;
import com.mindhub.homebanking.services.ClientService;
import com.mindhub.homebanking.services.TransactionService;
import com.mindhub.homebanking.services.implement.PDFGeneratorServiceImplement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;


import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

import static com.mindhub.homebanking.models.TransactionType.CREDITO;
import static com.mindhub.homebanking.models.TransactionType.DEBITO;


@RestController
@RequestMapping("/api")
public class TransactionController {
    @Autowired
    private AccountService accountService;
    @Autowired
    private ClientService clientService;
    @Autowired
    private TransactionService transactionService;

    @Transactional
    @PostMapping(path = "/transactions")
    public ResponseEntity<Object> newTransaction (@RequestParam Double amount, @RequestParam String description, @RequestParam String accountOrigin, @RequestParam String accountDestination, Authentication authentication) {

        Client client = clientService.getClientCurrent(authentication);

        Account originAccount = accountService.getAccountByNumber(accountOrigin);
        Account destinationAccount = accountService.getAccountByNumber(accountDestination);

        if (amount.isNaN() || amount <= 0 || description.isEmpty() || accountOrigin.isEmpty() || accountDestination.isEmpty()) {

            return new ResponseEntity<>("Missing data", HttpStatus.FORBIDDEN);

        }
        if (accountOrigin.equals(accountDestination)) {

            return new ResponseEntity<>("Equal accounts", HttpStatus.FORBIDDEN);

        }
        if (accountService.getAccountByNumber(accountOrigin) == null) {

            return new ResponseEntity<>("Nonexistent accountOrigin", HttpStatus.FORBIDDEN);

        }
        if (!client.getAccount().contains(originAccount)) {

            return new ResponseEntity<>("You are not the account owner", HttpStatus.FORBIDDEN);

        }
        if (accountService.getAccountByNumber(accountDestination) == null) {

            return new ResponseEntity<>("Nonexistent account", HttpStatus.FORBIDDEN);

        }
        if (originAccount.getBalance() < amount) {

            return new ResponseEntity<>("Insufficient fund", HttpStatus.FORBIDDEN);

        }

        originAccount.setBalance(originAccount.getBalance() - amount);
        destinationAccount.setBalance(destinationAccount.getBalance() + amount);

        accountService.saveAccount(originAccount);
        accountService.saveAccount(destinationAccount);

        Transaction transactionDebito = new Transaction(DEBITO, amount, description + " " + accountDestination, LocalDateTime.now(), originAccount.getBalance(), true, originAccount);
        transactionService.saveTransaction(transactionDebito);

        Transaction transactionCredito = new Transaction(CREDITO, amount, description + " " + accountOrigin, LocalDateTime.now(), destinationAccount.getBalance(), true, destinationAccount);
        transactionService.saveTransaction(transactionCredito);

        return new ResponseEntity<>("Transaction completed" , HttpStatus.CREATED);

    }

    private final PDFGeneratorServiceImplement pdfGeneratorService;
    public TransactionController (PDFGeneratorServiceImplement pdfGeneratorService) {
        this.pdfGeneratorService = pdfGeneratorService;
    }

    @PostMapping("/pdf/{id}")
    public ResponseEntity<Object> createPdf(HttpServletResponse response, @PathVariable Long id, @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate desde,
                                            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate hasta, Authentication authentication) throws IOException, DocumentException {

        Client client = clientService.getClientCurrent(authentication);
        Account account = accountService.getAccountById(id);
        if(account == null){
            return new ResponseEntity<>("la cuenta no existe",HttpStatus.FORBIDDEN);
        }

        if(!client.getAccount().contains(account)){
            return new ResponseEntity<>("la cuenta no pertenece al cliente",HttpStatus.FORBIDDEN);
        }

        response.setContentType("application/pdf");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd:hh:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=pdf_" + currentDateTime + ".pdf";
        response.setHeader(headerKey, headerValue);


        this.pdfGeneratorService.export(response,id,desde,hasta);

        return new ResponseEntity<>("PDF created" , HttpStatus.CREATED);
    }

}
