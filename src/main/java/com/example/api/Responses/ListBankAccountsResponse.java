package com.example.api.Responses;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ListBankAccountsResponse {
    private List<BankAccountResponse> accounts;
}
