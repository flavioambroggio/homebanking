package com.mindhub.homebanking.services;

import com.lowagie.text.DocumentException;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;

public interface PDFGeneratorService {

    void export(HttpServletResponse response, long id, LocalDate desde, LocalDate hasta) throws IOException, DocumentException;
}
