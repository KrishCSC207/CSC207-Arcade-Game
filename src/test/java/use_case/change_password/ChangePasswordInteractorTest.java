package use_case.change_password;

import data_access.InMemoryUserDataAccessObject;
import entity.User;
import entity.UserFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ChangePasswordInteractorTest {

    @Test
    void successTest() {
        // FIX: The constructor expects (password, username)
        ChangePasswordInputData inputData = new ChangePasswordInputData("newPassword123", "Paul");

        InMemoryUserDataAccessObject userRepository = new InMemoryUserDataAccessObject();
        UserFactory factory = new UserFactory();

        User user = factory.create("Paul", "oldPassword");
        userRepository.save(user);

        ChangePasswordOutputBoundary successPresenter = new ChangePasswordOutputBoundary() {
            @Override
            public void prepareSuccessView(ChangePasswordOutputData outputData) {
                assertEquals("Paul", outputData.getUsername());
            }

            @Override
            public void prepareFailView(String error) {
                fail("Use case failure is unexpected.");
            }
        };

        ChangePasswordInputBoundary interactor = new ChangePasswordInteractor(userRepository, successPresenter, factory);
        interactor.execute(inputData);
    }

    @Test
    void failurePasswordEmptyTest() {
        // FIX: The constructor expects (password, username)
        ChangePasswordInputData inputData = new ChangePasswordInputData("", "Paul");

        InMemoryUserDataAccessObject userRepository = new InMemoryUserDataAccessObject();
        UserFactory factory = new UserFactory();

        User user = factory.create("Paul", "password");
        userRepository.save(user);

        ChangePasswordOutputBoundary failurePresenter = new ChangePasswordOutputBoundary() {
            @Override
            public void prepareSuccessView(ChangePasswordOutputData user) {
                fail("Use case success is unexpected.");
            }

            @Override
            public void prepareFailView(String error) {
                assertEquals("New password cannot be empty", error);
            }
        };

        ChangePasswordInputBoundary interactor = new ChangePasswordInteractor(userRepository, failurePresenter, factory);
        interactor.execute(inputData);
    }
}