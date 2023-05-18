package com.greet.api.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

public class ChatRoomSequenceUtils {
    public static String getSHA256Hash(List<String> emails) {
        String concatenated = emails.get(0) + emails.get(1);

        char[] charArray = concatenated.toCharArray();
        Arrays.sort(charArray);

        String sortedString = new String(charArray);
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(sortedString.getBytes());

            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }

            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        }
    }
}
