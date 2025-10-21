package com.example.api;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import com.example.api.handlers.AccountsAPI;
import com.example.api.objects.Account;
import com.example.api.objects.Address;
import com.example.api.objects.Transaction;
import com.example.api.objects.User;
import com.example.api.objects.Transaction.TransactionTypes;
import com.example.api.requests.CreateBankAccountRequest;
import com.example.api.requests.CreateTransactionRequest;
import com.example.api.requests.UpdateBankAccountRequest;
import com.example.api.responses.BankAccountResponse;
import com.example.api.responses.ListBankAccountsResponse;
import com.example.api.responses.ListTransactionsResponse;
import com.example.api.responses.TransactionResponse;

@SuppressWarnings("null")
class AccountAPITests {

    private AccountsAPI accountsApi;
    private User testUser;

    private Address address = mock(Address.class);

    @BeforeEach
    void setUp() {
        accountsApi = new AccountsAPI();
        testUser = User.builder()
            .accounts(new HashMap<>())
            .address(address)
            .build();
    }

    // Create Account Tests
    static Stream<Arguments> createAccountTestCases() {
        return Stream.of(
            // Success case - Current Account
            Arguments.of(
                CreateBankAccountRequest.builder()
                    .name("My Personal Account")
                    .accountType(Account.AccountTypes.PERSONAL)
                    .build(),
                HttpStatus.CREATED,
                true,
                null
            ),
            // Success case - Savings Account
            Arguments.of(
                CreateBankAccountRequest.builder()
                    .name("My Savings Account")
                    .accountType(Account.AccountTypes.PERSONAL)
                    .build(),
                HttpStatus.CREATED,
                true,
                null
            )
        );
    }

    @ParameterizedTest
    @MethodSource("createAccountTestCases")
    void testCreateAccount(CreateBankAccountRequest request, HttpStatus expectedStatus, 
                          boolean shouldSucceed, String expectedErrorMessage) {
        if (shouldSucceed) {
            ResponseEntity<BankAccountResponse> response = accountsApi.createAccount(testUser, request);
            assertEquals(expectedStatus, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(request.getName(), response.getBody().getName());
            assertEquals(request.getAccountType(), response.getBody().getAccountType());
            assertTrue(response.getBody().getAccountNumber().startsWith("01"));
        } else {
            ResponseStatusException exception = assertThrows(ResponseStatusException.class, 
                () -> accountsApi.createAccount(testUser, request));
            assertEquals(expectedStatus, exception.getStatusCode());
            assertEquals(expectedErrorMessage, exception.getReason());
        }
    }

    // Get Account Tests
    @Test
    void testGetAccountSuccess() {
        // Setup test account
        Account testAccount = Account.builder()
            .accountNumber("0100001")
            .sortCode("01-01-01")
            .name("Test Account")
            .accountType(Account.AccountTypes.PERSONAL)
            .balance(100.0)
            .currency("GBP")
            .build();
        testUser.getAccounts().put(testAccount.getAccountNumber(), testAccount);

        ResponseEntity<BankAccountResponse> response = accountsApi.getAccount(testUser, "0100001");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(testAccount.getAccountNumber(), response.getBody().getAccountNumber());
    }

    @Test
    void testGetAccountNotFound() {
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, 
            () -> accountsApi.getAccount(testUser, "nonexistent"));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    // List Accounts Test
    @Test
    void testListAccounts() {
        // Setup multiple test accounts
        Account account1 = Account.builder()
            .accountNumber("0100001")
            .name("Account 1")
            .accountType(Account.AccountTypes.PERSONAL)
            .build();
        Account account2 = Account.builder()
            .accountNumber("0100002")
            .name("Account 2")
            .accountType(Account.AccountTypes.PERSONAL)
            .build();

        testUser.getAccounts().put(account1.getAccountNumber(), account1);
        testUser.getAccounts().put(account2.getAccountNumber(), account2);

        ResponseEntity<ListBankAccountsResponse> response = accountsApi.listAccounts(testUser);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().getAccounts().size());
    }

    // Update Account Tests
    static Stream<Arguments> updateAccountTestCases() {
        return Stream.of(
            // Success case - Update name only
            Arguments.of(
                "0100001",
                new UpdateBankAccountRequest("Updated Name", null   ),
                HttpStatus.OK,
                true,
                null
            ),
            // Success case - Update type only
            Arguments.of(
                "0100001",
                new UpdateBankAccountRequest(null, Account.AccountTypes.PERSONAL),
                HttpStatus.OK,
                true,
                null
            ),
            // Failure case - Account not found
            Arguments.of(
                "nonexistent",
                new UpdateBankAccountRequest("New Name", Account.AccountTypes.PERSONAL),
                HttpStatus.NOT_FOUND,
                false,
                "Account not found"
            )
        );
    }

    @ParameterizedTest
    @MethodSource("updateAccountTestCases")
    void testUpdateAccount(String accountNumber, UpdateBankAccountRequest request,
                          HttpStatus expectedStatus, boolean shouldSucceed, String expectedErrorMessage) {
        // Setup test account if test should succeed
        if (shouldSucceed) {
            Account testAccount = Account.builder()
                .accountNumber(accountNumber)
                .name("Original Name")
                .accountType(Account.AccountTypes.PERSONAL)
                .build();
            testUser.getAccounts().put(accountNumber, testAccount);
        }

        if (shouldSucceed) {
            ResponseEntity<BankAccountResponse> response = accountsApi.updateAccount(testUser, accountNumber, request);
            assertEquals(expectedStatus, response.getStatusCode());
            assertNotNull(response.getBody());
            if (request.getName() != null) {
                assertEquals(request.getName(), response.getBody().getName());
            }
            if (request.getAccountType() != null) {
                assertEquals(request.getAccountType(), response.getBody().getAccountType());
            }
        } else {
            ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> accountsApi.updateAccount(testUser, accountNumber, request));
            assertEquals(expectedStatus, exception.getStatusCode());
            assertEquals(expectedErrorMessage, exception.getReason());
        }
    }

    // Transaction Tests
    static Stream<Arguments> createTransactionTestCases() {
        return Stream.of(
            // Success case - Deposit
            Arguments.of(
                "0100001",
                new CreateTransactionRequest(100.0, "GBP", TransactionTypes.DEPOSIT),
                HttpStatus.CREATED,
                true,
                null,
                100.0
            ),
            // Success case - Withdrawal with sufficient funds
            Arguments.of(
                "0100001",
                new CreateTransactionRequest(50.0, "GBP", TransactionTypes.WITHDRAWAL),
                HttpStatus.CREATED,
                true,
                null,
                100.0
            ),
            // Failure case - Withdrawal with insufficient funds
            Arguments.of(
                "0100001",
                new CreateTransactionRequest(200.0, "GBP", TransactionTypes.WITHDRAWAL),
                HttpStatus.UNPROCESSABLE_ENTITY,
                false,
                "Insufficient funds to process transaction",
                100.0
            ),
            // Failure case - Account not found
            Arguments.of(
                "nonexistent",
                new CreateTransactionRequest(100.0, "GBP", TransactionTypes.DEPOSIT),
                HttpStatus.NOT_FOUND,
                false,
                "Account not found",
                0.0
            )
        );
    }

    @ParameterizedTest
    @MethodSource("createTransactionTestCases")
    void testCreateTransaction(String accountNumber, CreateTransactionRequest request,
                             HttpStatus expectedStatus, boolean shouldSucceed,
                             String expectedErrorMessage, double initialBalance) {
        // Setup test account if needed
        if (shouldSucceed || expectedStatus == HttpStatus.UNPROCESSABLE_ENTITY) {
            Account testAccount = Account.builder()
                .accountNumber(accountNumber)
                .balance(initialBalance)
                .currency("GBP")
                .build();
            testUser.getAccounts().put(accountNumber, testAccount);
        }

        if (shouldSucceed) {
            ResponseEntity<TransactionResponse> response = accountsApi.createTransaction(testUser, accountNumber, request);
            assertEquals(expectedStatus, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(request.getAmount(), response.getBody().getAmount());
            assertEquals(request.getType(), response.getBody().getType());

            // Verify balance update
            Account updatedAccount = testUser.getAccounts().get(accountNumber);
            if (request.getType() == TransactionTypes.DEPOSIT) {
                assertEquals(initialBalance + request.getAmount(), updatedAccount.getBalance());
            } else {
                assertEquals(initialBalance - request.getAmount(), updatedAccount.getBalance());
            }
        } else {
            ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> accountsApi.createTransaction(testUser, accountNumber, request));
            assertEquals(expectedStatus, exception.getStatusCode());
            assertEquals(expectedErrorMessage, exception.getReason());
        }
    }

    // List Transactions Test
    @Test
    void testListTransactions() {
        // Setup test account with transactions
        String accountNumber = "0100001";
        Account testAccount = Account.builder()
            .accountNumber(accountNumber)
            .balance(100.0)
            .currency("GBP")
            .build();

        Transaction transaction1 = Transaction.builder()
            .amount(50.0)
            .currency("GBP")
            .type(TransactionTypes.DEPOSIT)
            .build();
        Transaction transaction2 = Transaction.builder()
            .amount(30.0)
            .currency("GBP")
            .type(TransactionTypes.WITHDRAWAL)
            .build();

        testAccount.addTransaction(transaction1);
        testAccount.addTransaction(transaction2);
        testUser.getAccounts().put(accountNumber, testAccount);

        ResponseEntity<ListTransactionsResponse> response = accountsApi.listTransactions(testUser, accountNumber);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().getTransactions().size());
    }

    // Delete Account Tests
    static Stream<Arguments> deleteAccountTestCases() {
        return Stream.of(
            // Success case
            Arguments.of(
                "0100001",
                HttpStatus.OK,
                true,
                null
            ),
            // Failure case - Account not found
            Arguments.of(
                "nonexistent",
                HttpStatus.NOT_FOUND,
                false,
                "Account not found"
            )
        );
    }

    @ParameterizedTest
    @MethodSource("deleteAccountTestCases")
    void testDeleteAccount(String accountNumber, HttpStatus expectedStatus,
                         boolean shouldSucceed, String expectedErrorMessage) {
        // Setup test account if test should succeed
        if (shouldSucceed) {
            Account testAccount = Account.builder()
                .accountNumber(accountNumber)
                .build();
            testUser.getAccounts().put(accountNumber, testAccount);
        }

        if (shouldSucceed) {
            ResponseEntity<String> response = accountsApi.deleteAccount(testUser, accountNumber);
            assertEquals(expectedStatus, response.getStatusCode());
            assertFalse(testUser.getAccounts().containsKey(accountNumber));
        } else {
            ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> accountsApi.deleteAccount(testUser, accountNumber));
            assertEquals(expectedStatus, exception.getStatusCode());
            assertEquals(expectedErrorMessage, exception.getReason());
        }
    }

    // Get Transaction Tests
    @Test
    void testGetTransactionSuccess() {
        // Setup test account and transaction
        String accountNumber = "0100001";
        Account testAccount = Account.builder()
            .accountNumber(accountNumber)
            .build();

        Transaction transaction = Transaction.builder()
            .amount(100.0)
            .currency("GBP")
            .type(TransactionTypes.DEPOSIT)
            .build();

        testAccount.addTransaction(transaction);
        testUser.getAccounts().put(accountNumber, testAccount);

        ResponseEntity<TransactionResponse> response = accountsApi.getTransaction(
            testUser, accountNumber, transaction.getId());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(transaction.getAmount(), response.getBody().getAmount());
        assertEquals(transaction.getType(), response.getBody().getType());
    }

    @Test
    void testGetTransactionNotFound() {
        // Setup test account without transaction
        String accountNumber = "0100001";
        Account testAccount = Account.builder()
            .accountNumber(accountNumber)
            .build();
        testUser.getAccounts().put(accountNumber, testAccount);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
            () -> accountsApi.getTransaction(testUser, accountNumber, "nonexistent"));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Transaction not found", exception.getReason());
    }
}
