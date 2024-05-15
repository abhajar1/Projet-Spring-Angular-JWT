package com.emsi.ebankingbackend.services;

import com.emsi.ebankingbackend.dtos.*;
import com.emsi.ebankingbackend.entities.*;
import com.emsi.ebankingbackend.enums.OperationType;
import com.emsi.ebankingbackend.exceptions.BalanceNotSufficentException;
import com.emsi.ebankingbackend.exceptions.BankAccountNotFoundException;
import com.emsi.ebankingbackend.exceptions.CustomerNotFoundException;
import com.emsi.ebankingbackend.mappers.BankAccountMapperImpl;
import com.emsi.ebankingbackend.repositories.AccountOperationRepository;
import com.emsi.ebankingbackend.repositories.BankAccountRepository;
import com.emsi.ebankingbackend.repositories.CustomerRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
@Service
@Transactional
@AllArgsConstructor
@Slf4j // --> pour journalisation {logger}
public class BankAccountServiceImpl implements BankAccountService{


    private CustomerRepository customerRepository;
    private BankAccountRepository bankAccountRepository;
    private AccountOperationRepository accountOperationRepository;
    private BankAccountMapperImpl dtoMapper;

    @Override
    public CustomerDTO saveCustomer(CustomerDTO customerDTO) {
        log.info("SAVING NEW CUSTOMER");
        Customer customer=dtoMapper.fromCustomerDTO(customerDTO);
        Customer savedCustomer = customerRepository.save(customer);
        return dtoMapper.fromCustomer(savedCustomer);
    }
    @Override
    public CurrentBankAccountDTO saveCurrentBankAccount(double initialBalance, double overDraft, Long customerId) throws CustomerNotFoundException {


        Customer customer=customerRepository.findById(customerId).orElse(null);
        if(customer==null)
            throw new CustomerNotFoundException("Customer not found");

          CurrentAccount  currentAccount=new CurrentAccount();

        currentAccount.setId(UUID.randomUUID().toString());
        currentAccount.setCreatedAt(new Date());
        currentAccount.setBalance(initialBalance);
        currentAccount.setCustomer(customer);
        currentAccount.setOverDraft(overDraft);
        CurrentAccount savedBankAccount= bankAccountRepository.save(currentAccount);
        return dtoMapper.fromCurrentBankAccount(savedBankAccount);

    }
    @Override
    public SavingBankAccountDTO saveSavingBankAccount(double initialBalance, double interestRate, Long customerId) throws CustomerNotFoundException {


        Customer customer=customerRepository.findById(customerId).orElse(null);
        if(customer==null)
            throw new CustomerNotFoundException("Customer not found");

        SavingAccount  savingAccount=new SavingAccount();

        savingAccount.setId(UUID.randomUUID().toString());
        savingAccount.setCreatedAt(new Date());
        savingAccount.setBalance(initialBalance);
        savingAccount.setCustomer(customer);
        savingAccount.setInterestRate(interestRate);
        SavingAccount savedBanAccount= bankAccountRepository.save(savingAccount);
        return dtoMapper.fromSavingBankAccount(savedBanAccount);
    }
    @Override
    public List<CustomerDTO> listCustomers() {

        List<Customer> customers=customerRepository.findAll();

       List<CustomerDTO> customerDTOS=customers.stream().map(customer -> dtoMapper
                       .fromCustomer(customer))
               .collect(Collectors.toList());
//        for (Customer customer:customers)
//        {
//            CustomerDTO customerDTO=dtoMapper.fromCustomer(customer);
//            customerDTOS.add(customerDTO);
//        }


        return customerDTOS;
    }
    @Override
    public BankAccountDTO getBankAccount(String accountId) throws BankAccountNotFoundException {
        BankAccount bankAccount=bankAccountRepository.findById(accountId)
                .orElseThrow(()-> new BankAccountNotFoundException("Bank account not found !"));

        if(bankAccount instanceof SavingAccount)
        {
            SavingAccount savingAccount=(SavingAccount) bankAccount;
            return dtoMapper.fromSavingBankAccount(savingAccount);
        }
        else {
            CurrentAccount currentAccount=(CurrentAccount) bankAccount;
            return dtoMapper.fromCurrentBankAccount(currentAccount);
        }

    }
    @Override
    public void debit(String accountId, double amount, String description) throws BankAccountNotFoundException, BalanceNotSufficentException {

        BankAccount bankAccount=bankAccountRepository.findById(accountId)
                .orElseThrow(()-> new BankAccountNotFoundException("Bank account not found !"));

        if (bankAccount.getBalance() < amount)
        {
            throw new BalanceNotSufficentException("Balanace not sufficient");
        }
        AccountOperation accountOperation=new AccountOperation();
        accountOperation.setType(OperationType.DEBIT);
        accountOperation.setDescription(description);
        accountOperation.setAmount(amount);
        accountOperation.setOperationDate(new Date());
        accountOperation.setBankAccount(bankAccount);
        accountOperationRepository.save(accountOperation);
        bankAccount.setBalance(bankAccount.getBalance()-amount);
        bankAccountRepository.save(bankAccount);

    }
    @Override
     public void credit(String accountId, double amount, String description) throws BankAccountNotFoundException {

        BankAccount bankAccount=bankAccountRepository.findById(accountId)
                .orElseThrow(()-> new BankAccountNotFoundException("Bank account not found !"));


        AccountOperation accountOperation=new AccountOperation();
        accountOperation.setType(OperationType.CREDIT);
        accountOperation.setDescription(description);
        accountOperation.setAmount(amount);
        accountOperation.setOperationDate(new Date());
        accountOperation.setBankAccount(bankAccount);
        accountOperationRepository.save(accountOperation);
        bankAccount.setBalance(bankAccount.getBalance()+amount);
        bankAccountRepository.save(bankAccount);
    }

    @Override
    public void transfer(String accountIdSource, String accountIdDestination, double amount) throws BankAccountNotFoundException, BalanceNotSufficentException {

        debit(accountIdSource,amount,"Transfer to "+ accountIdDestination);
        credit(accountIdDestination,amount,"Transfer from "+ accountIdSource);

    }

    @Override
    public List<BankAccountDTO> bankAccountList(){
        List<BankAccount> bankAccounts= bankAccountRepository.findAll();
        List<BankAccountDTO> collect = bankAccounts.stream().map(bankAccount -> {
            if (bankAccount instanceof SavingAccount) {
                SavingAccount savingAccount = (SavingAccount) bankAccount;
                return dtoMapper.fromSavingBankAccount(savingAccount);
            } else {
                CurrentAccount currentAccount = (CurrentAccount) bankAccount;
                return dtoMapper.fromCurrentBankAccount(currentAccount);

            }
        }).collect(Collectors.toList()); return collect;
    }

    @Override
    public  CustomerDTO getCustomer(Long customerId) throws CustomerNotFoundException {
        Customer customer=customerRepository.findById(customerId).orElseThrow(()->new CustomerNotFoundException("Customer Not Found!"));

        return dtoMapper.fromCustomer(customer);
    }

    @Override
    public CustomerDTO updateCustomer(CustomerDTO customerDTO) {
        log.info("UPDATING  CUSTOMER");
        Customer customer=dtoMapper.fromCustomerDTO(customerDTO);
        Customer savedCustomer = customerRepository.save(customer);
        return dtoMapper.fromCustomer(savedCustomer);
    }

    @Override
    public void deleteCustomer(Long customerId)
    {
        customerRepository.deleteById(customerId);

    }
    @Override
    public List<AccountOperationDTO> accountHistory(String accountId)
    {
        List<AccountOperation> accountOperations=accountOperationRepository.findByBankAccountId(accountId);
        return accountOperations.stream().map(op-> dtoMapper.fromAccountOperation(op))
                .collect(Collectors.toList());

    }
    @Override
    public AccountHistoryDTO getAccountHistory(String accountId, int page, int size) throws BankAccountNotFoundException {
        BankAccount bankAccount=bankAccountRepository.findById(accountId).orElse(null);
        if (bankAccount==null) throw new BankAccountNotFoundException("Account Not Found!");
        Page<AccountOperation> accountOperations=accountOperationRepository.findByBankAccountId(accountId, PageRequest.of(page,size));
        AccountHistoryDTO accountHistoryDTO=new AccountHistoryDTO();
        List<AccountOperationDTO> accountOperationsDTOS = accountOperations.getContent().stream().map(op -> dtoMapper.fromAccountOperation(op))
                .collect(Collectors.toList());
        accountHistoryDTO.setAccountOperationDTOS(accountOperationsDTOS);
        accountHistoryDTO.setAccountId(bankAccount.getId());
        accountHistoryDTO.setBalance(bankAccount.getBalance());
        accountHistoryDTO.setCurrentPage(page);
        accountHistoryDTO.setPageSize(size);
        accountHistoryDTO.setTotalPages(accountOperations.getTotalPages());

        return accountHistoryDTO;
    }

}
