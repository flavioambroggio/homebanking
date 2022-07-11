package com.mindhub.homebanking.services.implement;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Transaction;
import com.mindhub.homebanking.services.AccountService;
import com.mindhub.homebanking.services.PDFGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PDFGeneratorServiceImplement implements PDFGeneratorService {

    @Autowired
    private AccountService accountService;

    @Override
    public void export(HttpServletResponse response, long id, LocalDate desde, LocalDate hasta) throws IOException, DocumentException {

        Account account = accountService.getAccountById(id);

        Set<Transaction> transactions = account.getTransactions();
        Set<Transaction> transactionsSet = transactions.stream().filter(transaction -> transaction.getDate().toLocalDate().isBefore(hasta.plusDays(1))).collect(Collectors.toSet());
        Set<Transaction> transactionsSet2 = transactionsSet.stream().filter(transaction -> transaction.getDate().toLocalDate().isAfter(desde.plusDays(-1))).collect(Collectors.toSet());

        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, response.getOutputStream());

        document.open();

        Paragraph tituloP = new Paragraph("PIGGY BANK");
        tituloP.setSpacingAfter(10);
        document.add(tituloP);

        Paragraph titulo = new Paragraph("Transactions"+ " " + account.getNumber());
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

    }
}
