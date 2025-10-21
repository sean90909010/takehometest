package com.example.api.Handlers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.api.Objects.Account;
import com.example.api.Objects.Transaction;
import com.example.api.Objects.User;
import com.example.api.Objects.Transaction.TransactionTypes;
import com.example.api.Requests.CreateBankAccountRequest;
import com.example.api.Requests.CreateTransactionRequest;
import com.example.api.Requests.UpdateBankAccountRequest;
import com.example.api.Responses.BankAccountResponse;
import com.example.api.Responses.ListBankAccountsResponse;
import com.example.api.Responses.ListTransactionsResponse;
import com.example.api.Responses.TransactionResponse;

import jakarta.validation.Valid;

@RestController
public class AccountsAPI {
    /**
     * REST controller that manages bank accounts and transactions for the
     * authenticated user. Endpoints are modeled after the project's OpenAPI
     * specification and are intentionally lightweight in-memory implementations
     * (for testing/demo purposes).
     *
     * <p>All methods expect an authenticated {@code User} to be provided by
     * Spring Security via the {@code @AuthenticationPrincipal} annotation and
     * operate only on that user's accounts.</p>
     */
    

    @PostMapping(value = "/v1/accounts", produces = "application/json")
    /**
     * Create a new bank account for the authenticated user.
     *
     * @param authUser the authenticated user (injected by Spring Security)
     * @param request  the create account request payload; validated via Jakarta Validation
     * @return ResponseEntity containing the created {@link BankAccountResponse} with HTTP 201
     * @throws ResponseStatusException if any uniqueness/validation check fails (mapped to appropriate HTTP status)
     */
    public ResponseEntity<BankAccountResponse> createAccount(@AuthenticationPrincipal User authUser, @Valid @RequestBody CreateBankAccountRequest request) {
        Account newAccount = Account.builder()
            .accountNumber("01" + String.format("%06d", authUser.getAccounts().size() + 1))
            .sortCode("01-01-01")
            .name(request.getName())
            .accountType(request.getAccountType())
            .balance(0.0)
            .currency("GBP")
            .build();

        authUser.getAccounts().put(newAccount.getAccountNumber(), newAccount);

        BankAccountResponse response = BankAccountResponse.builder()
            .accountNumber(newAccount.getAccountNumber())
            .sortCode(newAccount.getSortCode())
            .name(newAccount.getName())
            .accountType(newAccount.getAccountType())
            .balance(newAccount.getBalance())
            .currency(newAccount.getCurrency())
            .createdTimestamp(newAccount.getCreatedTimestamp())
            .updatedTimestamp(newAccount.getUpdatedTimestamp())
            .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping(value = "/v1/accounts", produces = "application/json")
    /**
     * List all bank accounts belonging to the authenticated user.
     *
     * @param authUser the authenticated user (injected by Spring Security)
     * @return ResponseEntity containing a {@link ListBankAccountsResponse} with HTTP 200
     */
    public ResponseEntity<ListBankAccountsResponse> listAccounts(@AuthenticationPrincipal User authUser) {
        
        List<BankAccountResponse> accountResponses = authUser.getAccounts().values().stream()
            .map(account -> BankAccountResponse.builder()
                .accountNumber(account.getAccountNumber())
                .sortCode(account.getSortCode())
                .name(account.getName())
                .accountType(account.getAccountType())
                .balance(account.getBalance())
                .currency(account.getCurrency())
                .createdTimestamp(account.getCreatedTimestamp())
                .updatedTimestamp(account.getUpdatedTimestamp())
                .build())
            .toList();

        ListBankAccountsResponse response = ListBankAccountsResponse.builder().accounts(accountResponses).build();
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/v1/accounts/{accountNumber}", produces = "application/json")
    /**
     * Fetch a single bank account by its account number for the authenticated user.
     *
     * @param authUser      the authenticated user (injected by Spring Security)
     * @param accountNumber the account number path variable
     * @return ResponseEntity containing the {@link BankAccountResponse} with HTTP 200
     * @throws ResponseStatusException with HTTP 404 if the account does not exist for the user
     */
    public ResponseEntity<BankAccountResponse> getAccount(@AuthenticationPrincipal User authUser, @PathVariable String accountNumber) {
        Account account = authUser.getAccounts().get(accountNumber);
        if (account == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found");
        }

        BankAccountResponse response = BankAccountResponse.builder()
            .accountNumber(account.getAccountNumber())
            .sortCode(account.getSortCode())
            .name(account.getName())
            .accountType(account.getAccountType())
            .balance(account.getBalance())
            .currency(account.getCurrency())
            .createdTimestamp(account.getCreatedTimestamp())
            .updatedTimestamp(account.getUpdatedTimestamp())
            .build();

        return ResponseEntity.ok(response);
    }

    @PatchMapping(value = "/v1/accounts/{accountNumber}", produces = "application/json")
    /**
     * Partially update account metadata (such as name or type) for the
     * authenticated user's account.
     *
     * @param authUser      the authenticated user (injected by Spring Security)
     * @param accountNumber the account number path variable
     * @param request       payload containing updatable fields; validated via Jakarta Validation
     * @return ResponseEntity with the updated {@link BankAccountResponse}
     * @throws ResponseStatusException with HTTP 404 if the account does not exist for the user
     */
    public ResponseEntity<BankAccountResponse> updateAccount(
            @AuthenticationPrincipal User authUser,
            @PathVariable String accountNumber,
            @Valid @RequestBody UpdateBankAccountRequest request) {
        
        Account account = authUser.getAccounts().get(accountNumber);
        if (account == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found");
        }

        if (request.getName() != null) {
            account.setName(request.getName());
        }
        if (request.getAccountType() != null) {
            account.setAccountType(request.getAccountType());
        }

        BankAccountResponse response = BankAccountResponse.builder()
            .accountNumber(account.getAccountNumber())
            .sortCode(account.getSortCode())
            .name(account.getName())
            .accountType(account.getAccountType())
            .balance(account.getBalance())
            .currency(account.getCurrency())
            .createdTimestamp(account.getCreatedTimestamp())
            .updatedTimestamp(account.getUpdatedTimestamp())
            .build();

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/v1/accounts/{accountNumber}")
    /**
     * Delete a bank account for the authenticated user.
     *
     * @param authUser      the authenticated user (injected by Spring Security)
     * @param accountNumber the account number path variable
     * @throws ResponseStatusException with HTTP 404 if the account does not exist for the user
     */
    public void deleteAccount(@AuthenticationPrincipal User authUser, @PathVariable String accountNumber) {
        if(authUser.getAccounts().get(accountNumber) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found");
        }
        authUser.getAccounts().remove(accountNumber);
    }

    @PostMapping(value = "/v1/accounts/{accountNumber}/transactions", produces = "application/json")
    /**
     * Create a transaction against a user's account. This will update the
     * account balance and persist the transaction in-memory.
     *
     * @param authUser      the authenticated user (injected by Spring Security)
     * @param accountNumber the account number path variable
     * @param request       the create transaction payload; validated via Jakarta Validation
     * @return ResponseEntity with the created {@link TransactionResponse} and HTTP 201
     * @throws ResponseStatusException with HTTP 404 if the account does not exist, or 422 if insufficient funds
     */
    public ResponseEntity<TransactionResponse> createTransaction(
            @AuthenticationPrincipal User authUser, 
            @PathVariable String accountNumber,
            @Valid @RequestBody CreateTransactionRequest request) {
        
        Account account = authUser.getAccounts().get(accountNumber);
        if (account == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found");
        }

        // Check for sufficient funds for withdrawal transactions
        if (TransactionTypes.WITHDRAWAL.equals(request.getType()) && account.getBalance() < request.getAmount()) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Insufficient funds to process transaction");
        }

        Transaction transaction = Transaction.builder()
            .amount(request.getAmount())
            .currency(request.getCurrency())
            .type(request.getType())
            .build();

        account.addTransaction(transaction);

        // Update balance
        if (TransactionTypes.DEPOSIT.equals(request.getType())) {
            account.setBalance(account.getBalance() + request.getAmount());
        } else if (TransactionTypes.WITHDRAWAL.equals(request.getType())) {
            account.setBalance(account.getBalance() - request.getAmount());
        }

        TransactionResponse response = TransactionResponse.builder()
            .id(accountNumber)
            .amount(transaction.getAmount())
            .currency(transaction.getCurrency())
            .type(transaction.getType())
            .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping(value = "/v1/accounts/{accountNumber}/transactions", produces = "application/json")
    /**
     * List all transactions for a given account belonging to the authenticated user.
     *
     * @param authUser      the authenticated user (injected by Spring Security)
     * @param accountNumber the account number path variable
     * @return ResponseEntity with {@link ListTransactionsResponse} and HTTP 200
     * @throws ResponseStatusException with HTTP 404 if the account does not exist
     */
    public ResponseEntity<ListTransactionsResponse> listTransactions(
            @AuthenticationPrincipal User authUser, @PathVariable String accountNumber) {
        Account account = authUser.getAccounts().get(accountNumber);
        if (account == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found");
        }

        List<TransactionResponse> transactionResponses = account.getTransactions().values().stream()
            .map(transaction -> TransactionResponse.builder()
                .id(transaction.getId())
                .amount(transaction.getAmount())
                .currency(transaction.getCurrency())
                .type(transaction.getType())
                .build())
            .toList();
        
        ListTransactionsResponse response = ListTransactionsResponse.builder().transactions(transactionResponses).build();
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/v1/accounts/{accountNumber}/transactions/{transactionId}", produces = "application/json")
    /**
     * Fetch a single transaction by ID for a given account belonging to the
     * authenticated user.
     *
     * @param authUser      the authenticated user (injected by Spring Security)
     * @param accountNumber the account number path variable
     * @param transactionId the transaction id path variable
     * @return ResponseEntity with {@link TransactionResponse} and HTTP 200
     * @throws ResponseStatusException with HTTP 404 if the account or transaction does not exist
     */
    public ResponseEntity<TransactionResponse> getTransaction(
            @AuthenticationPrincipal User authUser, 
            @PathVariable String accountNumber,
            @PathVariable String transactionId) {
        
        Account account = authUser.getAccounts().get(accountNumber);
        if (account == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found");
        }

        Transaction transaction = account.getTransactions().get(transactionId);
        if (transaction == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction not found");
        }

        TransactionResponse response = TransactionResponse.builder()
            .id(transactionId)
            .amount(transaction.getAmount())
            .currency(transaction.getCurrency())
            .type(transaction.getType())
            .build();

        return ResponseEntity.ok(response);
    }
}
