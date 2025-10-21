package com.example.api.Requests;

import com.example.api.Objects.Account.AccountTypes;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateBankAccountRequest {
    private String name;
    private AccountTypes accountType;
}
