package view;

import interface_adapter.logged_in.ChangePasswordController;
import interface_adapter.logged_in.LoggedInState;
import interface_adapter.logged_in.LoggedInViewModel;
import interface_adapter.logout.LogoutController;
import interface_adapter.connections.ConnectionsController;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * The View for when the user is logged into the program.
 */
public class LoggedInView extends JPanel implements ActionListener, PropertyChangeListener {

    private final String viewName = "logged in";
    private final LoggedInViewModel loggedInViewModel;
    private ChangePasswordController changePasswordController = null;
    private LogoutController logoutController;
    private ConnectionsController connectionsController;

    private final JLabel username;
    private final JButton logOut;

    private final JButton multipleChoiceBtn;
    private final JButton crosswordBtn;
    private final JButton connectionsBtn;

    private final JButton changePassword;

    public LoggedInView(LoggedInViewModel loggedInViewModel) {
        this.loggedInViewModel = loggedInViewModel;
        this.loggedInViewModel.addPropertyChangeListener(this);

        final JLabel usernameInfo = new JLabel("Currently logged in as: ");
        usernameInfo.setAlignmentX(Component.CENTER_ALIGNMENT);
        username = new JLabel();
        username.setAlignmentX(Component.CENTER_ALIGNMENT);

        final JPanel menuButtons = new JPanel();
        menuButtons.setLayout(new BoxLayout(menuButtons, BoxLayout.Y_AXIS));
        menuButtons.setAlignmentX(Component.CENTER_ALIGNMENT);

        multipleChoiceBtn = new JButton("Multiple Choice");
        menuButtons.add(multipleChoiceBtn);

        crosswordBtn = new JButton("Crossword");
        menuButtons.add(crosswordBtn);

        connectionsBtn = new JButton("Connections");
        menuButtons.add(connectionsBtn);

        // add action listener to each button

        final JPanel buttons = new JPanel();
        logOut = new JButton("Log Out");
        buttons.add(logOut);

        changePassword = new JButton("Change Password");
        buttons.add(changePassword);

        logOut.addActionListener(this);

        // Connections button action: load a game by prompting for a code
        connectionsBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (connectionsController != null) {
                    String code = JOptionPane.showInputDialog(
                            LoggedInView.this,
                            "Enter Connections game code:",
                            "XBZQ"
                    );
                    if (code != null && !code.trim().isEmpty()) {
                        connectionsController.executeLoad(code.trim());
                    }
                } else {
                    JOptionPane.showMessageDialog(LoggedInView.this,
                            "Connections is not available right now.");
                }
            }
        });

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        // Change this so when clicked it goes to new view
        changePassword.addActionListener(
                // This creates an anonymous subclass of ActionListener and instantiates it.
                evt -> {
                    if (evt.getSource().equals(changePassword)) {
                        final LoggedInState currentState = loggedInViewModel.getState();

                        this.changePasswordController.execute(
                                currentState.getUsername(),
                                currentState.getPassword()
                        );
                    }
                }
        );

        this.add(usernameInfo);
        this.add(username);
        this.add(menuButtons);
        this.add(buttons);
    }

    /**
     * React to a button click that results in evt.
     * @param evt the ActionEvent to react to
     */
    public void actionPerformed(ActionEvent evt) {
        this.logoutController.execute();
        System.out.println("Click " + evt.getActionCommand());
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("state")) {
            final LoggedInState state = (LoggedInState) evt.getNewValue();
            username.setText(state.getUsername());
        }
    }

    public String getViewName() {
        return viewName;
    }

    public void setChangePasswordController(ChangePasswordController changePasswordController) {
        this.changePasswordController = changePasswordController;
    }

    public void setLogoutController(LogoutController logoutController) {
        this.logoutController = logoutController;
    }

    public void setConnectionsController(ConnectionsController connectionsController) {
        this.connectionsController = connectionsController;
    }
}
