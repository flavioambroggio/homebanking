package com.mindhub.homebanking.controllers;


import com.lowagie.text.*;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.models.Transaction;
import com.mindhub.homebanking.services.AccountService;
import com.mindhub.homebanking.services.ClientService;
import com.mindhub.homebanking.services.TransactionService;
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


import java.awt.*;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
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

    @PostMapping("/pdf/{id}")
    public ResponseEntity<Object> createPdf(@PathVariable Long id, @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate desde,
                                            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate hasta) throws FileNotFoundException, DocumentException {

        Account account = accountService.getAccountById(id);
        if(account ==  null) {
            return new ResponseEntity<>("Nonexistent account", HttpStatus.FORBIDDEN);
        }

        Client client = account.getClient();
        if(!client.getAccount().contains(account)) {
            return new ResponseEntity<>("The account does not belong to you", HttpStatus.FORBIDDEN);
        }

        Set<Transaction> transactions = account.getTransactions();
        Set<Transaction> transactionsSet = transactions.stream().filter(transaction -> transaction.getDate().toLocalDate().isBefore(hasta.plusDays(1))).collect(Collectors.toSet());
        Set<Transaction> transactionsSet2 = transactionsSet.stream().filter(transaction -> transaction.getDate().toLocalDate().isAfter(desde.plusDays(-1))).collect(Collectors.toSet());

        if(transactionsSet2.size() == 0) {
            return new ResponseEntity<>("No transactions found", HttpStatus.FORBIDDEN);
        }

        Document document = new Document();
        String ruta = System.getProperty("user.home");
        PdfWriter.getInstance(document, new FileOutputStream(ruta + "/Desktop/Transactions-" + account.getNumber() +".pdf"));

        document.open();

        Paragraph tituloP = new Paragraph("PIGGY BANK");
        tituloP.setSpacingAfter(10);
        document.add(tituloP);

        Paragraph titulo = new Paragraph("Transactions"+ " " + account.getNumber() + " " + client.getFirstName() + " " + client.getLastName());
        titulo.setSpacingAfter(10);
        titulo.setAlignment(Element.ALIGN_CENTER);
        document.add(titulo);

        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        PdfPCell h1 = new PdfPCell(new Phrase("Tipo"));
        h1.setBackgroundColor(new Color(194, 117, 255));
        table.addCell(h1);
        PdfPCell h2 = new PdfPCell(new Phrase("Monto"));
        h2.setBackgroundColor(new Color(194, 117, 255));
        table.addCell(h2);
        PdfPCell h3 = new PdfPCell(new Phrase("Descripcion"));
        h3.setBackgroundColor(new Color(194, 117, 255));
        table.addCell(h3);
        PdfPCell h4 = new PdfPCell(new Phrase("Fecha"));
        h4.setBackgroundColor(new Color(194, 117, 255));
        table.addCell(h4);
        PdfPCell h5 = new PdfPCell(new Phrase("Fecha"));
        h5.setBackgroundColor(new Color(194, 117, 255));
        table.addCell(h5);

        transactionsSet2.forEach(transaction -> {
            PdfPCell c1 = new PdfPCell(new Phrase(transaction.getType() + ""));
            PdfPCell c2 = new PdfPCell(new Phrase(transaction.getAmount() + ""));
            PdfPCell c3 = new PdfPCell(new Phrase(transaction.getDescription()));
            PdfPCell c4 = new PdfPCell(new Phrase(transaction.getDate().toLocalDate() + ""));
            PdfPCell c5 = new PdfPCell(new Phrase(transaction.getBalance() + ""));
            table.addCell(c1);
            table.addCell(c2);
            table.addCell(c3);
            table.addCell(c4);
            table.addCell(c5);
        });

        document.add(table);

        document.close();

        return new ResponseEntity<>("PDF created" , HttpStatus.CREATED);
    }

}
