package com.mindhub.homebanking.services;

import com.mindhub.homebanking.dtos.LoanDTO;
import com.mindhub.homebanking.models.Loan;

import java.util.List;

public interface LoanService {

    List<LoanDTO> getLoansDTO();

    Loan getLoanById(long id);

    void saveLoan(Loan loan);

}
