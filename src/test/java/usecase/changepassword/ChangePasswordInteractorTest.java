package usecase.changepassword;

import dataaccess.InMemoryUserDataAccessObject;
import entity.User;
import entity.UserFactory;
import org.junit.jupiter.api.Test;
import usecase.passwordvalidator.PasswordValidatorServiceDataAccessInterface;

import static org.junit.jupiter.api.Assertions.*;

class ChangePasswordInteractorTest {

    @Test
    void failurePasswordCompromisedTest() {
        // 1. Arrange
        ChangePasswordInputData inputData = new ChangePasswordInputData("password123", "Paul");
        InMemoryUserDataAccessObject userRepository = new InMemoryUserDataAccessObject();
        UserFactory factory = new UserFactory();

        // Setup User
        User user = factory.create("Paul", "oldPassword");
        userRepository.save(user);

        // CREATE A MOCK VALIDATOR
        // This simulates the API saying "YES, this password is bad"
        PasswordValidatorServiceDataAccessInterface mockValidator = new PasswordValidatorServiceDataAccessInterface() {
            @Override
            public boolean isPasswordCompromised(String password) {
                return true; // Always say it's compromised for this test
            }
        };

        // 2. Act: Create failure presenter
        ChangePasswordOutputBoundary failurePresenter = new ChangePasswordOutputBoundary() {
            @Override
            public void prepareSuccessView(ChangePasswordOutputData user) {
                fail("Use case success is unexpected.");
            }

            @Override
            public void prepareFailView(String error) {
                // Verify the correct error message for a compromised password
                assertEquals("This password has been exposed in a data breach. Please choose a safer one.", error);
            }
        };

        // 3. Execute
        ChangePasswordInputBoundary interactor = new ChangePasswordInteractor(
                userRepository,
                failurePresenter,
                factory,
                mockValidator // Pass the mock here
        );

        interactor.execute(inputData);
    }
}