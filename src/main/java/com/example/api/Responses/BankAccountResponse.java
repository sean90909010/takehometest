package com.example.api.Responses;

import java.time.LocalDateTime;

import com.example.api.Objects.Account.AccountTypes;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BankAccountResponse {
    private String accountNumber;
    private String sortCode;
    private String name;
    private AccountTypes accountType;
    private Double balance;
    private String currency;
    private LocalDateTime createdTimestamp;
    private LocalDateTime updatedTimestamp;
}
