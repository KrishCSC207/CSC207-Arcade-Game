package view;

import interfaceadapter.ViewManagerModel;
import interfaceadapter.change_password.ChangePasswordController;
import interfaceadapter.logged_in.LoggedInState;
import interfaceadapter.logged_in.LoggedInViewModel;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class ChangePasswordView extends JPanel implements PropertyChangeListener {

    private final String viewName = "change password";
    private final LoggedInViewModel loggedInViewModel;
    private final ViewManagerModel viewManagerModel;

    private ChangePasswordController changePasswordController;

    private final JLabel username;
    private final JTextField passwordField = new JTextField(15);
    private final JButton submitBtn = new JButton("Change Password");
    private final JButton backBtn = new JButton("Back");

    public ChangePasswordView(LoggedInViewModel loggedInViewModel,
                              ViewManagerModel viewManagerModel) {

        this.loggedInViewModel = loggedInViewModel;
        this.viewManagerModel = viewManagerModel;

        this.loggedInViewModel.addPropertyChangeListener(this);

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JLabel usernameInfo = new JLabel("Currently logged in as: ");
        usernameInfo.setAlignmentX(Component.CENTER_ALIGNMENT);

        username = new JLabel();
        username.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Password input
        JPanel inputPanel = new JPanel();
        inputPanel.add(new JLabel("New Password:"));
        inputPanel.add(passwordField);
        // Prevent input panel from stretching vertically
        inputPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        // Change Password button
        submitBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        submitBtn.addActionListener(e -> {
            String username = this.loggedInViewModel.getState().getUsername();
            String password = passwordField.getText();

            changePasswordController.execute(username, password);
        });

        // Back button
        backBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        backBtn.addActionListener(e -> {
            viewManagerModel.setState("logged in");
            viewManagerModel.firePropertyChange();
        });
        JPanel bottomButtons = new JPanel();

        bottomButtons.add(backBtn);
        bottomButtons.add(submitBtn);

        this.add(usernameInfo);
        this.add(username);

        // Space between username and the input field
        this.add(Box.createVerticalStrut(20));

        this.add(inputPanel);

        // Space between input field and buttons
        this.add(Box.createVerticalStrut(30));

        this.add(bottomButtons);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("state")) {
            final LoggedInState state = (LoggedInState) evt.getNewValue();
            username.setText(state.getUsername());
        }
        else if (evt.getPropertyName().equals("password")) {
            final LoggedInState state = (LoggedInState) evt.getNewValue();
            if (state.getPasswordError() == null) {
                JOptionPane.showMessageDialog(this, "password updated for " + state.getUsername());
                passwordField.setText("");
            }
            else {
                JOptionPane.showMessageDialog(this, state.getPasswordError());
            }
        }

    }
    public String getViewName() {
        return viewName;
    }

    public void setChangePasswordController(ChangePasswordController controller) {
        this.changePasswordController = controller;
    }
}