package com.example.api.Responses;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ListTransactionsResponse {
    
    private List<TransactionResponse> transactions;

}
