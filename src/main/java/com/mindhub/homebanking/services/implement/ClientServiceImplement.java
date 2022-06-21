package com.mindhub.homebanking.services.implement;

import com.mindhub.homebanking.dtos.ClientDTO;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.repositories.ClientRepository;
import com.mindhub.homebanking.services.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClientServiceImplement implements ClientService {
    @Autowired
    private ClientRepository clientRepository;

    @Override
    public List<ClientDTO> getClientsDTO(Authentication authentication) {

        if(!authentication.getName().equals("admin@admin.com")){
            List<Client> clients = clientRepository.findAll();

            clients.forEach(client -> client.setCards(client.getCards().stream().filter(card -> card.isVisibility()).collect(Collectors.toSet())));
            clients.forEach(client -> client.setAccounts(client.getAccount().stream().filter(account -> account.isVisibility()).collect(Collectors.toSet())));

            return  clients.stream().map(ClientDTO::new).collect(Collectors.toList());
        }

        return clientRepository.findAll().stream().map(ClientDTO::new).collect(Collectors.toList());
    }

    @Override
    public Client getClientCurrent(Authentication authentication) {

        if(!authentication.getName().equals("admin@admin.com")){
            Client client = clientRepository.findByEmail(authentication.getName());

            client.setCards(client.getCards().stream().filter(card -> card.isVisibility()).collect(Collectors.toSet()));

            client.setAccounts(client.getAccount().stream().filter(account -> account.isVisibility()).collect(Collectors.toSet()));

            return  client;

        }
        return clientRepository.findByEmail(authentication.getName());
    }

    @Override
    public Client getClientByEmail(String email) {
        return clientRepository.findByEmail(email);
    }

    @Override
    public Client getClientById(Long id) {
        return clientRepository.findById(id).orElse(null);
    }

    @Override
    public ClientDTO getCLientDTO(long id) {
        return clientRepository.findById(id).map(ClientDTO::new).orElse(null);
    }

    @Override
    public void saveClient(Client client) {
        clientRepository.save(client);
    }
}
