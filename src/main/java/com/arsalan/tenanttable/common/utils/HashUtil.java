package com.arsalan.tenanttable.common.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

public final class HashUtil {
    private HashUtil() {
        throw new AssertionError("Utility class should not be instantiated.");
    }

    /**
     * <h1>Algorithm:</h1>
     * <ol>
     *     <li> Receive the input string.</li>
     *     <li> Convert the input string into UTF-8 encoded bytes.</li>
     *     <li> Compute the SHA-256 hash of the input bytes.</li>
     *     <li> Obtain the resulting 32-byte (256-bit) hash.</li>
     *     <li> Convert the hash bytes into a hexadecimal string.</li>
     *     <li> Return the 64-character hexadecimal hash.</li>
     * </ol>
     * <h1>Verification:</h1>
     * <ol>
     *     <li> Receive the original input (invitation token).</li>
     *     <li> Compute its SHA-256 hash using this method.</li>
     *     <li> Compare the computed hash with the stored hash.</li>
     *     <li> If both hashes match, the input is valid; otherwise, it is invalid.</li>
     * </ol>
     */
    public static String sha256(String value) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            byte[] inputBytes = value.getBytes(StandardCharsets.UTF_8);
            byte[] hashBytes = messageDigest.digest(inputBytes);
            return HexFormat.of().formatHex(hashBytes);

        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 algorithm not available", e);
        }
    }
}
