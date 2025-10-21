package com.example.api.Responses;

import java.time.LocalDateTime;

import com.example.api.Objects.Transaction.TransactionTypes;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class TransactionResponse {


    @NotBlank(message = "ID cannot be blank")
    @Pattern(regexp = "^tan-[A-Za-z0-9]$)", message = "ID must start with 'tan-' followed by alphanumeric characters")
    private String id;
    @Positive(message = "Amount must be greater than 0")
    @NotNull(message = "Amount cannot be null")
    private Double amount;
    @NotBlank(message = "Currency cannot be blank")
    private String currency;
    @NotBlank(message = "Transaction type cannot be blank")
    private TransactionTypes type;
    @Builder.Default
    private LocalDateTime createdTimestamp = LocalDateTime.now();
    private String reference;
    private String userId;
    
}
