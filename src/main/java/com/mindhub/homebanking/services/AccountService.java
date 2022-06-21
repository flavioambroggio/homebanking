package com.mindhub.homebanking.services;

import com.mindhub.homebanking.dtos.AccountDTO;
import com.mindhub.homebanking.models.Account;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface AccountService {
    List<AccountDTO> getAccountsDTO();
    AccountDTO getAccountDTO(long id, Authentication authentication);
    Account getAccountByNumber(String number);

    Account getAccountById(Long id);
    void saveAccount(Account account);

}
