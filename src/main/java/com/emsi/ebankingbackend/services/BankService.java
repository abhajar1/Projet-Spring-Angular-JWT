package com.emsi.ebankingbackend.services;
import com.emsi.ebankingbackend.entities.BankAccount;
import com.emsi.ebankingbackend.entities.CurrentAccount;
import com.emsi.ebankingbackend.entities.SavingAccount;
import com.emsi.ebankingbackend.repositories.BankAccountRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
@Service
@Transactional

public class BankService {
    @Autowired
    private BankAccountRepository bankAccountRepository;
    public void consulter(){
        BankAccount bankAccount=
                bankAccountRepository.findById("1").orElse(null);
        System.out.println("banckAccount: " + bankAccount);
        if(bankAccount !=null) {
            System.out.println("*************************************");
            System.out.println(bankAccount.getId());
            System.out.println(bankAccount.getBalance());
            System.out.println(bankAccount.getStatus());
            System.out.println(bankAccount.getCreatedAt());
            System.out.println(bankAccount.getCustomer().getName());
            System.out.println(bankAccount.getClass().getSimpleName());
            if (bankAccount instanceof CurrentAccount) {
                System.out.println("Over Draft => " + ((CurrentAccount) bankAccount).getOverDraft());
            } else if (bankAccount instanceof SavingAccount) {
                System.out.println("Rate => " + ((SavingAccount) bankAccount).getInterestRate());
            }
            bankAccount.getAccountOperations().forEach(op -> {
                System.out.println("=====================");
                System.out.println(op.getType());
                System.out.println(op.getOperationDate());
                System.out.println(op.getAmount());
                System.out.println("=====================");
            });
        }
    }
}
