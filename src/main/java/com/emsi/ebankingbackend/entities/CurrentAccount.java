package com.emsi.ebankingbackend.entities;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@DiscriminatorValue("CA")
@NoArgsConstructor
@AllArgsConstructor
public class CurrentAccount extends BankAccount{
    private double overDraft;
}
