package use_case.change_password;

import entity.User;
import entity.UserFactory;
import use_case.password_validator.PasswordValidatorServiceDataAccessInterface; // NEW IMPORT

/**
 * The Change Password Interactor.
 */
public class ChangePasswordInteractor implements ChangePasswordInputBoundary {
    private final ChangePasswordUserDataAccessInterface userDataAccessObject;
    private final ChangePasswordOutputBoundary userPresenter;
    private final UserFactory userFactory;
    private final PasswordValidatorServiceDataAccessInterface passwordValidator; // NEW FIELD

    public ChangePasswordInteractor(ChangePasswordUserDataAccessInterface changePasswordDataAccessInterface,
                                    ChangePasswordOutputBoundary changePasswordOutputBoundary,
                                    UserFactory userFactory,
                                    PasswordValidatorServiceDataAccessInterface passwordValidator) { // NEW ARGUMENT
        this.userDataAccessObject = changePasswordDataAccessInterface;
        this.userPresenter = changePasswordOutputBoundary;
        this.userFactory = userFactory;
        this.passwordValidator = passwordValidator; // ASSIGNMENT
    }

    @Override
    public void execute(ChangePasswordInputData changePasswordInputData) {
        if ("".equals(changePasswordInputData.getPassword())) {
            userPresenter.prepareFailView("New password cannot be empty");
        }
        // NEW CHECK HERE
        else if (passwordValidator.isPasswordCompromised(changePasswordInputData.getPassword())) {
            userPresenter.prepareFailView("This password has been exposed in a data breach. Please choose a safer one.");
        }
        else {
            final User user = userFactory.create(changePasswordInputData.getUsername(),
                    changePasswordInputData.getPassword());

            userDataAccessObject.changePassword(user);

            final ChangePasswordOutputData changePasswordOutputData = new ChangePasswordOutputData(user.getName());
            userPresenter.prepareSuccessView(changePasswordOutputData);
        }
    }
}