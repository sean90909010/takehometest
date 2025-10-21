package com.example.api.requests;

import com.example.api.objects.Account.AccountTypes;

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
