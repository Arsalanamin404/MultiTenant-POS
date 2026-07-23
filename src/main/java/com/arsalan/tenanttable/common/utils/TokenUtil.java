package com.arsalan.tenanttable.common.utils;

import java.security.SecureRandom;
import java.util.Base64;

/**
 * Utility class for generating cryptographically secure random tokens.
 *
 * <p>The generated tokens are URL-safe Base64 encoded without padding,
 * making them suitable for use in:
 * <ul>
 *   <li>Email verification links</li>
 *   <li>Password reset links</li>
 *   <li>Staff invitation links</li>
 *   <li>Magic login links</li>
 *   <li>Other security-sensitive operations</li>
 * </ul>
 *
 * <p>Internally, this utility uses {@link java.security.SecureRandom}
 * to generate random bytes and encodes them using a URL-safe
 * {@link java.util.Base64} encoder.
 */

public final class TokenUtil {
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final int DEFAULT_TOKEN_SIZE = 32;

    private TokenUtil() {
        throw new AssertionError("Utility class should not be instantiated.");
    }

    /**
     * Generates a cryptographically secure random token.
     *
     * <p><b>How it works:</b>
     *
     * <pre>
     * 1. Allocate a byte array of the requested size.
     *
     * 2. Fill the array with cryptographically secure random bytes
     *    using SecureRandom. [-46, -82, -10, -38, -78, 47]
     *    Each element represents one random byte (8 random bits).
     *
     * 3. Encode the raw bytes using the URL-safe Base64 encoder.
     *
     *    Base64 does not change the randomness it only converts the
     *    binary data into a compact and human-readable string.
     *
     *    Base64 groups the random bits into 6-bit chunks and maps each
     *    chunk to a character from a 64-character alphabet, producing
     *    a compact, URL-safe string representation.
     *
     * 4. Remove '=' padding characters using withoutPadding()
     *    because they are unnecessary for tokens and produce
     *    cleaner URLs.
     * </pre>
     *
     * <p>The returned token is safe to use in URLs, emails, HTTP
     * parameters, and JSON payloads.
     *
     * @param sizeInBytes number of cryptographically secure random bytes
     * @return URL-safe Base64 encoded token
     */

    public static String generateToken(int sizeInBytes) {
        byte[] bytes = new byte[sizeInBytes];
        SECURE_RANDOM.nextBytes(bytes);

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(bytes);
    }

    public static String generateToken() {
        return generateToken(DEFAULT_TOKEN_SIZE);
    }


}
