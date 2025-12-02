package use_case.password_validator;

/**
 * Interface for checking if a password is valid/safe.
 */
public interface PasswordValidatorServiceDataAccessInterface {
    /**
     * Checks if the password has been compromised.
     * @param password the password to check
     * @return true if the password is compromised (unsafe), false otherwise.
     */
    boolean isPasswordCompromised(String password);
}