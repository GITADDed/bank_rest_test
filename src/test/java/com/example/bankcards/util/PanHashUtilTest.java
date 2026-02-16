package com.example.bankcards.util;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class PanHashUtilTest {

    @Test
    void sha256Hex_returnsExpectedHash() throws Exception {
        String salt = "salt";
        String pan = "1234567890123456";

        String expected = sha256Hex(salt + ":" + pan);

        String actual = PanHashUtil.sha256Hex(salt, pan);

        assertEquals(expected, actual);
        assertEquals(64, actual.length());
    }

    @Test
    void sha256Hex_changesWhenSaltChanges() {
        String pan = "1234567890123456";

        String hash1 = PanHashUtil.sha256Hex("salt1", pan);
        String hash2 = PanHashUtil.sha256Hex("salt2", pan);

        assertNotEquals(hash1, hash2);
    }

    private static String sha256Hex(String input) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder(digest.length * 2);
        for (byte b : digest) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}

