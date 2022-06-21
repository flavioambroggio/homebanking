package com.mindhub.homebanking;

import com.mindhub.homebanking.models.*;
import com.mindhub.homebanking.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;

import static com.mindhub.homebanking.models.AccountType.AHORRO;
import static com.mindhub.homebanking.models.AccountType.CORRIENTE;
import static com.mindhub.homebanking.models.CardColor.*;
import static com.mindhub.homebanking.models.CardType.CREDIT;
import static com.mindhub.homebanking.models.CardType.DEBIT;
import static com.mindhub.homebanking.models.TransactionType.CREDITO;
import static com.mindhub.homebanking.models.TransactionType.DEBITO;

@SpringBootApplication
public class HomebankingApplication {

	public static void main(String[] args) {
		SpringApplication.run(HomebankingApplication.class, args);
	}

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Bean
	public CommandLineRunner initData(ClientRepository clientRepository, AccountRepository accountRepository, TransactionRepository transactionRepository, LoanRepository loanRepository, ClientLoanRepository clientLoanRepository, CardRepository cardRepository) {
		return (args) -> {

			Client client1 = new Client("Melba", "Lorenzo", "melbaL@mindhub.com", passwordEncoder.encode("melba123"));
			clientRepository.save(client1);

			Account account1 = new Account("VIN001", LocalDateTime.now(), 5000, AHORRO, true, client1);
			accountRepository.save(account1);

			Account account2 = new Account("VIN002", LocalDateTime.now().plusDays(1), 7500, CORRIENTE, true, client1);
			accountRepository.save(account2);

			Client client2 = new Client("Melba", "Morel", "melba@mindhub.com", passwordEncoder.encode("melba321"));
            clientRepository.save(client2);

			Account account3 = new Account("VIN003", LocalDateTime.now(), 7000, AHORRO, true, client2);
			accountRepository.save(account3);

			Client admin = new Client("admin", "admin", "admin@admin.com", passwordEncoder.encode("admin"));
			clientRepository.save(admin);

			Transaction transaction1 = new Transaction(CREDITO, 10000, "Transferencia", LocalDateTime.now(), 10000, true, account1);
			transactionRepository.save(transaction1);

			Transaction transaction2 = new Transaction(CREDITO, 1000,"Transferencia", LocalDateTime.now(), 7500, true, account2);
			transactionRepository.save(transaction2);

			Transaction transaction3 = new Transaction(DEBITO, 5000, "Alquiler", LocalDateTime.now().plusDays(7), 5000, true, account1);
			transactionRepository.save(transaction3);

			Transaction transaction4 = new Transaction(DEBITO, 1000, "Super", LocalDateTime.now().plusDays(4), 4000, true, account1);
			transactionRepository.save(transaction4);

			Loan loan1 = new Loan("Hipotecario", 500000, List.of(12,24,36,48,60), 15);
			loanRepository.save(loan1);

			Loan loan2 = new Loan("Personal", 100000, List.of(6,12,24), 15);
			loanRepository.save(loan2);

			Loan loan3 = new Loan("Automotriz", 300000, List.of(6,12,24,36), 20);
			loanRepository.save(loan3);

			ClientLoan clientLoan1 = new ClientLoan(400000, 60, client1, loan1);
			clientLoanRepository.save(clientLoan1);

			ClientLoan clientLoan2 = new ClientLoan(50000,12, client1, loan2);
			clientLoanRepository.save(clientLoan2);

			ClientLoan clientLoan3 = new ClientLoan(100000, 24, client2, loan2);
			clientLoanRepository.save(clientLoan3);

			ClientLoan clientLoan4 = new ClientLoan(200000, 36, client2, loan3);
			clientLoanRepository.save(clientLoan4);

			Card card1 = new Card(client1.getFirstName()+" "+client1.getLastName(), DEBIT, GOLD, "3325-6745-7876-4445", 990, LocalDateTime.now(), LocalDateTime.now().plusYears(5), true, false, client1);
			cardRepository.save(card1);

			Card card2 = new Card(client1.getFirstName()+" "+client1.getLastName(), CREDIT, TITANIUM, "2234-6745-5522-7888", 750, LocalDateTime.now(), LocalDateTime.now().plusYears(-5), true, false, client1);
			cardRepository.save(card2);

			Card card3 = new Card(client2.getFirstName()+" "+client2.getLastName(), CREDIT, SILVER, "5544-8877-9954-3321", 123, LocalDateTime.now(), LocalDateTime.now().plusYears(5), true, false, client2);
			cardRepository.save(card3);

		};
	}


}
