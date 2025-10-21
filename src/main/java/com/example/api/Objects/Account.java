package com.example.api.Objects;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Account {

    @NotBlank(message = "Account number cannot be blank")
    @Pattern(regexp = "^01\\d{6}$", message = "Account number must be 01 followed by 6 digits")
    private String accountNumber;

    @NotBlank(message = "Sort code cannot be blank")
    private String sortCode;

    @NotBlank(message = "Account name cannot be blank")
    private String name;

    @NotBlank(message = "Account type cannot be blank")
    private String accountType;

    @NotNull(message = "Balance cannot be null")
    private Double balance;

    @NotBlank(message = "Currency cannot be blank")
    private String currency;

    @Builder.Default
    private LocalDateTime createdTimestamp = LocalDateTime.now();

    @Builder.Default
    private LocalDateTime updatedTimestamp = LocalDateTime.now();

    @Builder.Default
    private HashMap<String, Transaction> transactions = new HashMap<>();

    public void addTransaction(@NotNull Transaction transaction) {
        transactions.put(transaction.getId(), transaction);
    }

}
