package com.example.api.Requests;

import com.example.api.Objects.Transaction;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CreateTransactionRequest {

    @NotNull(message = "amount cannot be null")
    @Positive(message = "Amount must be greater than 0")
    @Valid
    private Double amount;

    @NotNull(message = "currency cannot be null")
    @Valid
    private String currency;

    @NotNull(message = "type cannot be null")
    @Valid
    private Transaction.TransactionTypes type;

    private String reference;

}
