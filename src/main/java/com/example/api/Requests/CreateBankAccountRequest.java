package com.example.api.requests;

import com.example.api.objects.Account.AccountTypes;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CreateBankAccountRequest {

    @NotBlank(message = "Name cannot be blank")
    private String name;

    @NotBlank(message = "Account type cannot be blank")
    private AccountTypes accountType;
}
