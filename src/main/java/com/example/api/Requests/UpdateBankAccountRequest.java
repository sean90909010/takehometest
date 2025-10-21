package com.example.api.Requests;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UpdateBankAccountRequest {
    private String name;
    private String accountType;
}
