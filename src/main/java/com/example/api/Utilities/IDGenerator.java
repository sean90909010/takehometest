package com.example.api.utilities;

import java.security.SecureRandom;

public class IDGenerator {

    private static final String USER_PREFIX = "usr-";
    private static final String TRANSACTION_PREFIX = "tan-";
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int ID_LENGTH = 6; // change as desired
    private static final SecureRandom RANDOM = new SecureRandom();

    public static String generateUserId() {
        StringBuilder sb = new StringBuilder(USER_PREFIX);
        for (int i = 0; i < ID_LENGTH; i++) {
            int index = RANDOM.nextInt(CHARACTERS.length());
            sb.append(CHARACTERS.charAt(index));
        }
        return sb.toString();
    }

    public static String generateTransactionId() {
        StringBuilder sb = new StringBuilder(TRANSACTION_PREFIX);
        for (int i = 0; i < ID_LENGTH; i++) {
            int index = RANDOM.nextInt(CHARACTERS.length());
            sb.append(CHARACTERS.charAt(index));
        }
        return sb.toString();
    }
}
