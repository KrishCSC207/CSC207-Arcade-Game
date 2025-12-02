package usecase.signup;

import dataaccess.InMemoryUserDataAccessObject;
import entity.UserFactory;
import org.junit.jupiter.api.Test;
import usecase.passwordvalidator.PasswordValidatorServiceDataAccessInterface;

import static org.junit.jupiter.api.Assertions.*;

class SignupInteractorTest {

    @Test
    void successTest() {
        // 1. Arrange
        SignupInputData inputData = new SignupInputData("Paul", "password123", "password123");
        InMemoryUserDataAccessObject userRepository = new InMemoryUserDataAccessObject();
        UserFactory factory = new UserFactory();

        // FAIL-SAFE VALIDATOR: Creates a fake validator that always returns false (Not Compromised)
        // We need this because the Interactor constructor now requires it.
        PasswordValidatorServiceDataAccessInterface mockValidator = new PasswordValidatorServiceDataAccessInterface() {
            @Override
            public boolean isPasswordCompromised(String password) {
                return false; // Treat all passwords as safe for this test
            }
        };

        // 2. Act
        SignupOutputBoundary successPresenter = new SignupOutputBoundary() {
            @Override
            public void prepareSuccessView(SignupOutputData user) {
                assertEquals("Paul", user.getUsername());
                assertTrue(userRepository.existsByName("Paul"));
            }

            @Override
            public void prepareFailView(String error) {
                fail("Use case failure is unexpected.");
            }

            @Override
            public void switchToLoginView() {
                // This is expected for some implementations
            }
        };

        // 3. Execute: Pass the mockValidator as the 4th argument
        SignupInputBoundary interactor = new SignupInteractor(userRepository, successPresenter, factory, mockValidator);
        interactor.execute(inputData);
    }

    @Test
    void failurePasswordCompromisedTest() {
        // 1. Arrange
        SignupInputData inputData = new SignupInputData("Paul", "badPassword", "badPassword");
        InMemoryUserDataAccessObject userRepository = new InMemoryUserDataAccessObject();
        UserFactory factory = new UserFactory();

        // COMPROMISED VALIDATOR: Creates a fake validator that always returns TRUE (Compromised)
        PasswordValidatorServiceDataAccessInterface mockValidator = new PasswordValidatorServiceDataAccessInterface() {
            @Override
            public boolean isPasswordCompromised(String password) {
                return true; // Force the error to happen
            }
        };

        // 2. Act
        SignupOutputBoundary failurePresenter = new SignupOutputBoundary() {
            @Override
            public void prepareSuccessView(SignupOutputData user) {
                fail("Use case success is unexpected. Should fail on password check.");
            }

            @Override
            public void prepareFailView(String error) {
                // Verify the exact error message from your Interactor
                assertEquals("Password is too common/compromised. Please choose a stronger one.", error);
            }

            @Override
            public void switchToLoginView() {
                // Not expected here
            }
        };

        // 3. Execute
        SignupInputBoundary interactor = new SignupInteractor(userRepository, failurePresenter, factory, mockValidator);
        interactor.execute(inputData);
    }
}