package com.example.api.requests;

import com.example.api.objects.Transaction;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateTransactionRequest {

    public CreateTransactionRequest(Double amount, String currency, Transaction.TransactionTypes type) {
        this.amount = amount;
        this.currency = currency;
        this.type = type;
    }
    
    @NotBlank(message = "amount cannot be null")
    @Positive(message = "Amount must be greater than 0")
    @Valid
    private Double amount;

    @NotNull(message = "currency cannot be null")
    @Valid
    private String currency;

    @NotBlank(message = "type cannot be null")
    @Valid
    private Transaction.TransactionTypes type;

    private String reference;

}
