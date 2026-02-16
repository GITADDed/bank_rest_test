package com.example.bankcards.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public final class PanHashUtil {
    private PanHashUtil() {}

    public static String sha256Hex(String salt, String pan) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest((salt + ":" + pan).getBytes(StandardCharsets.UTF_8));
            return toHex(digest);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private static String toHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) sb.append(String.format("%02x", b));
        return sb.toString();
    }
}

