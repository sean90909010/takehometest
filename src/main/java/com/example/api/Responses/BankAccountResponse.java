package com.example.api.Responses;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BankAccountResponse {
    private String accountNumber;
    private String sortCode;
    private String name;
    private String accountType;
    private Double balance;
    private String currency;
    private LocalDateTime createdTimestamp;
    private LocalDateTime updatedTimestamp;
}
