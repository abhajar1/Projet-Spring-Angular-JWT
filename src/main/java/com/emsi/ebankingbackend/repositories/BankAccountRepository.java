package com.emsi.ebankingbackend.repositories;

import com.emsi.ebankingbackend.entities.BankAccount;
import com.emsi.ebankingbackend.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BankAccountRepository extends JpaRepository<BankAccount,String> {
}
