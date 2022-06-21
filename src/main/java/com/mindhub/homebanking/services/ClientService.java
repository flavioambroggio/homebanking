package com.mindhub.homebanking.services;

import com.mindhub.homebanking.dtos.ClientDTO;
import com.mindhub.homebanking.models.Client;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface ClientService {

    List<ClientDTO> getClientsDTO(Authentication authentication);

    Client getClientCurrent(Authentication authentication);

    Client getClientByEmail(String email);

    Client getClientById(Long id);

    ClientDTO getCLientDTO(long id);

    void saveClient(Client client);

}
