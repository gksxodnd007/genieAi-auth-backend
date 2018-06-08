package com.finder.genie_ai.util;

import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.Random;

public class TokenGenerator {

    public static String generateSessionToken(String userId) {
        String seedValue = userId + System.currentTimeMillis();
        byte[] targetBytes = new byte[0];
        try {
            targetBytes = seedValue.getBytes("utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Base64.Encoder encoder = Base64.getEncoder();
        return encoder.encodeToString(targetBytes);
    }

    public static String generateSaltValue() {
        String seedValue = String.valueOf(new Random().nextLong() + System.currentTimeMillis());
        System.out.println(seedValue);
        byte[] targetBytes = new byte[0];
        try {
            targetBytes = seedValue.getBytes("utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Base64.Encoder encoder = Base64.getEncoder();
        return encoder.encodeToString(targetBytes);
    }

}
