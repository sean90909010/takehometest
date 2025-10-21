package com.example.api.Objects;

import java.time.LocalDateTime;

import com.example.api.Utilities.IDGenerator;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@AllArgsConstructor
@Getter
@Setter
public class Transaction {

    public enum TransactionTypes {
        WITHDRAWAL,
        DEPOSIT
    }

    @NotBlank(message = "ID cannot be blank")
    @Pattern(regexp = "^tan-[A-Za-z0-9]$)", message = "ID must start with 'tan-' followed by alphanumeric characters")
    @Valid
    @Builder.Default
    private String id = IDGenerator.generateTransactionId();

    @Positive(message = "Amount must be greater than 0")
    @NotNull(message = "Amount cannot be null")
    @Valid
    private Double amount;

    @NotBlank(message = "Currency cannot be blank")
    @Valid
    private String currency;

    @NotBlank(message = "Transaction type cannot be blank")
    @Valid
    private TransactionTypes type;

    @Builder.Default
    private LocalDateTime createdTimestamp = LocalDateTime.now();
}
