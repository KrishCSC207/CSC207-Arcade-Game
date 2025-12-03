package dataaccess;

import okhttp3.*;
import usecase.passwordvalidator.PasswordValidatorServiceDataAccessInterface;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PwnedPasswordDataAccessObject implements PasswordValidatorServiceDataAccessInterface {

    private final OkHttpClient client;

    public PwnedPasswordDataAccessObject() {
        // Reuse OkHttp since you already have it in your project
        this.client = new OkHttpClient();
    }

    @Override
    public boolean isPasswordCompromised(String password) {
        try {
            // 1. Hash the password using SHA-1
            String sha1Password = hashPassword(password);

            // 2. Split into Prefix (5 chars) and Suffix (The rest)
            // Example: If hash is "ABCDE12345...", Prefix="ABCDE", Suffix="12345..."
            String prefix = sha1Password.substring(0, 5);
            String suffix = sha1Password.substring(5);

            // 3. Call the API with ONLY the prefix (k-Anonymity)
            Request request = new Request.Builder()
                    .url("https://api.pwnedpasswords.com/range/" + prefix)
                    .addHeader("User-Agent", "Java-Password-Checker")
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                }

                // 4. The API returns a list of suffixes that match our prefix.
                // We check if OUR specific suffix is in that list.
                String responseBody = response.body().string();
                return responseBody.contains(suffix);
            }

        } catch (Exception e) {
            // If the API fails (internet down, etc.), we default to false (safe)
            // so we don't block the user from signing up.
            System.err.println("Warning: Could not validate password security: " + e.getMessage());
            return false;
        }
    }

    /**
     * Helper method to hash a string using SHA-1.
     * The API requires the hash to be in Uppercase Hex format.
     */
    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] result = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : result) {
                // Convert byte to Hex and force Uppercase
                sb.append(String.format("%02X", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-1 algorithm not found", e);
        }
    }
}