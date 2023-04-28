package com.greet.api.util;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class PasswordGeneratorUtils {

    private static final String CHAR_LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String CHAR_UPPER = CHAR_LOWER.toUpperCase();
    private static final String NUMBER = "0123456789";
    private static final String OTHER_CHAR = "!@#$%&*()_+-=[]?";

    private static final String PASSWORD_ALLOW_BASE = CHAR_LOWER + CHAR_UPPER + NUMBER + OTHER_CHAR;

    private static final SecureRandom random = new SecureRandom();


    public static String generatePassword(int length) {
        if (length < 1) throw new IllegalArgumentException();
        String passwordAllow = shuffleString();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int rndCharAt = random.nextInt(passwordAllow.length());
            char rndChar = passwordAllow.charAt(rndCharAt);
            sb.append(rndChar);
        }

        return sb.toString();
    }

    // shuffle the characters of a string with SecureRandom
    private static String shuffleString() {
        List<String> letters = Arrays.asList(PasswordGeneratorUtils.PASSWORD_ALLOW_BASE.split(""));
        Collections.shuffle(letters, random);
        return String.join("", letters);
    }

}

