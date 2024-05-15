package com.emsi.ebankingbackend.repositories;

import com.emsi.ebankingbackend.entities.AccountOperation;
import com.emsi.ebankingbackend.entities.BankAccount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccountOperationRepository extends JpaRepository<AccountOperation,Long> {

    public List<AccountOperation> findByBankAccountId(String accountId);
    public Page<AccountOperation> findByBankAccountId(String accountId, Pageable pageable);

}
