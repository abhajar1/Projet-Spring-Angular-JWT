package com.emsi.ebankingbackend.exceptions;

public class BankAccountNotFoundException extends Exception {
    public BankAccountNotFoundException(String msg) {
        super(msg);
    }
}
