package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dtos.ClientDTO;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.AccountType;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.services.AccountService;
import com.mindhub.homebanking.services.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

import static com.mindhub.homebanking.utils.AccountUtils.getRandomNumber;

@RestController
@RequestMapping("/api")
public class ClientController {
    @Autowired
    private ClientService clientService;

    @GetMapping("/clients")
    public List<ClientDTO> getClients(Authentication authentication) {
        return clientService.getClientsDTO(authentication);
    }

    @GetMapping("clients/{id}")
    public ClientDTO getClient(@PathVariable Long id){
        return clientService.getCLientDTO(id);
    }

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AccountService accountService;


    @PostMapping(path = "/clients")
    public ResponseEntity<Object> register (@RequestParam String firstName, @RequestParam String lastName, @RequestParam String email, @RequestParam String password) {

        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty()) {

            return new ResponseEntity<>("Missing data", HttpStatus.FORBIDDEN);

        }

        if (clientService.getClientByEmail(email) != null) {

            return new ResponseEntity<>("Name already in use", HttpStatus.FORBIDDEN);

        }

        Client client = new Client(firstName, lastName, email, passwordEncoder.encode(password));
        clientService.saveClient(client);

        Account account = new Account("VIN" + getRandomNumber(1, 99999999), LocalDateTime.now(), 0, AccountType.AHORRO, true, client);
        accountService.saveAccount(account);

        return new ResponseEntity<>(HttpStatus.CREATED);

    }

    @GetMapping("clients/current")
    public ClientDTO getUser(Authentication authentication){
        Client client = clientService.getClientCurrent(authentication);
        return new ClientDTO(client);
    }

    @PostMapping("/createClient")
    public ResponseEntity<Object> createClient (@RequestParam String firstName, @RequestParam String lastName, @RequestParam String email, @RequestParam String password) {

        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty()) {
            return new ResponseEntity<>("Missing data", HttpStatus.FORBIDDEN);
        }

        Client client = new Client(firstName, lastName, email, passwordEncoder.encode(password));
        clientService.saveClient(client);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PatchMapping("/editClient/{id}")
    public ResponseEntity<Object> editClient (@PathVariable Long id, @RequestParam String firstName, @RequestParam String lastName, @RequestParam String email) {

        Client client = clientService.getClientById(id);

        client.setFirstName(firstName);
        client.setLastName(lastName);
        client.setEmail(email);

        clientService.saveClient(client);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }



}
