package view;

import data_access.multiplechoice.QuestionDAO;
import interface_adapter.logged_in.LoggedInState;
import interface_adapter.logged_in.LoggedInViewModel;
import interface_adapter.logout.LogoutController;
import interface_adapter.ViewManagerModel;
import interface_adapter.connections.ConnectionsController;
import interface_adapter.crossword.CrosswordController;
import interface_adapter.multiplechoice.QuizController;
import interface_adapter.multiplechoice.QuizPresenter;
import interface_adapter.multiplechoice.QuizViewModel;
import interface_adapter.multiplechoice.ResultsViewModel;
import use_case.multiplechoice.QuestionDataAccessInterface;
import use_case.multiplechoice.quiz.QuizInteractor;
import use_case.multiplechoice.submit.SubmitAnswerInteractor;

import javax.swing.*;
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
    private final ViewManagerModel viewManagerModel;

    private LogoutController logoutController;
    private ConnectionsController connectionsController;
    private CrosswordController crosswordController;

    private final JLabel username;

    // UPDATED: Changed default time to "00:00"
    private String bestCrosswordTime = "00:00";
    private String highestScore = "0";

    private final JLabel bestTimeLabel;
    private final JLabel highScoreLabel;

    private final JButton multipleChoiceBtn;
    private final JButton crosswordBtn;
    private final JButton connectionsBtn;

    private final JButton logOut;
    private final JButton changePassword;

    public LoggedInView(LoggedInViewModel loggedInViewModel, ViewManagerModel viewManagerModel) {

        this.loggedInViewModel = loggedInViewModel;
        this.viewManagerModel = viewManagerModel;
        this.loggedInViewModel.addPropertyChangeListener(this);

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        // --- 1. Title Section (Hello, User) ---
        username = new JLabel("Hello, ");
        username.setFont(new Font("Arial", Font.BOLD, 15));
        username.setAlignmentX(Component.CENTER_ALIGNMENT);

        // --- 2. Stats Section ---
        bestTimeLabel = new JLabel("Best Crossword Time: " + bestCrosswordTime);
        bestTimeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        highScoreLabel = new JLabel("Highest Multiple Choice Score: " + highestScore);
        highScoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // --- 3. Minigames Menu Section ---
        JPanel menuButtons = new JPanel();
        menuButtons.setLayout(new BoxLayout(menuButtons, BoxLayout.Y_AXIS));
        menuButtons.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Buttons
        multipleChoiceBtn = new JButton("Multiple Choice");
        multipleChoiceBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        crosswordBtn = new JButton("Crossword");
        crosswordBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        connectionsBtn = new JButton("Connections");
        connectionsBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Add components to menu panel
        menuButtons.add(multipleChoiceBtn);
        menuButtons.add(crosswordBtn);
        menuButtons.add(connectionsBtn);

        // --- 4. Bottom Buttons ---
        JPanel bottomButtons = new JPanel();
        logOut = new JButton("Log Out");
        changePassword = new JButton("Change Password");
        bottomButtons.add(logOut);
        bottomButtons.add(changePassword);

        // Actions
        logOut.addActionListener(this);
        changePassword.addActionListener(e -> {
            viewManagerModel.setState("change password");
            viewManagerModel.firePropertyChange();
        });

        // --- Assemble the Main View with Spacing ---
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

        // Crossword button action: switch to crossword card managed by ViewManager
        crosswordBtn.addActionListener(e -> {
            // Ensure controller exists (wired in AppBuilder); even if null, we can still show the view
            viewManagerModel.setState("crossword");
            viewManagerModel.firePropertyChange();
        });

        // Multiple Choice button action: launch the Multiple Choice quiz
        multipleChoiceBtn.addActionListener(e -> launchMultipleChoiceQuiz());

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        // Add some space at the very top
        this.add(username);

        // Space between "Hello" and Stats
        this.add(Box.createVerticalStrut(15));
        this.add(bestTimeLabel);
        this.add(highScoreLabel);

        // Space between Stats and Minigames
        this.add(Box.createVerticalStrut(15));
        this.add(menuButtons);

        // Push bottom buttons to the bottom
        this.add(bottomButtons);
    }

    /**
     * React to a button click.
     */
    @Override
    public void actionPerformed(ActionEvent evt) {
        if (logoutController != null) {
            logoutController.execute();
        }
        System.out.println("Click " + evt.getActionCommand());
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("state")) {
            LoggedInState state = (LoggedInState) evt.getNewValue();
            // UPDATED: Concatenate "Hello, " with the username
            username.setText("Hello, " + state.getUsername());
        }
    }

    public String getViewName() {
        return viewName;
    }

    public void setLogoutController(LogoutController logoutController) {
        this.logoutController = logoutController;
    }

    public void setConnectionsController(ConnectionsController connectionsController) {
        this.connectionsController = connectionsController;
    }

    public void setCrosswordController(CrosswordController crosswordController) {
        this.crosswordController = crosswordController;
    }

    /**
     * Launches the Multiple Choice quiz.
     */
    private void launchMultipleChoiceQuiz() {
        QuestionDataAccessInterface repository = new QuestionDAO();
        repository.loadData();

        QuizViewModel quizViewModel = new QuizViewModel();
        ResultsViewModel resultsViewModel = new ResultsViewModel();
        QuizPresenter presenter = new QuizPresenter(quizViewModel, resultsViewModel);

        QuizInteractor quizInteractor = new QuizInteractor(repository, presenter);
        QuizController quizController = new QuizController(quizInteractor);

        CategorySelectionView selectionView = new CategorySelectionView(quizViewModel);
        selectionView.setQuizController(quizController);
        QuizView quizView = new QuizView(quizController, quizViewModel);
        ResultsView resultsView = new ResultsView(resultsViewModel);

        // Callback to update high score when quiz finishes
        resultsView.setOnFinishCallback(score -> {
            int currentHighScore = 0;
            try {
                currentHighScore = Integer.parseInt(highestScore);
            } catch (NumberFormatException ex) {
                currentHighScore = 0;
            }
            if (score > currentHighScore) {
                highestScore = String.valueOf(score);
                highScoreLabel.setText("Highest Multiple Choice Score: " + highestScore + "%");
            }
        });

        quizViewModel.addPropertyChangeListener(evt -> {
            if ("imagePath".equals(evt.getPropertyName())) {
                if (!quizController.hasSubmitAnswerInteractor()
                        && quizInteractor.getCurrentSession() != null) {
                    SubmitAnswerInteractor submitAnswerInteractor =
                            new SubmitAnswerInteractor(
                                    quizInteractor.getCurrentSession(),
                                    presenter,
                                    presenter);
                    quizController.setSubmitAnswerInteractor(submitAnswerInteractor);
                }
                if (selectionView.isDisplayable()) {
                    selectionView.dispose();
                    quizView.setVisible(true);
                }
            }
        });

        resultsViewModel.addPropertyChangeListener(evt -> {
            String name = evt.getPropertyName();
            if ("accuracy".equals(name) || "totalTimeMs".equals(name)) {
                SwingUtilities.invokeLater(() -> {
                    quizView.dispose();
                    resultsView.setVisible(true);
                });
            }
        });

        selectionView.setVisible(true);
    }
}
