package app;

import data_access.FileUserDataAccessObject;
import data_access.PwnedPasswordDataAccessObject; // NEW IMPORT
import entity.UserFactory;
import interface_adapter.ViewManagerModel;
import interface_adapter.change_password.ChangePasswordController;
import interface_adapter.change_password.ChangePasswordPresenter;
import interface_adapter.logged_in.LoggedInViewModel;
import interface_adapter.login.LoginController;
import interface_adapter.login.LoginPresenter;
import interface_adapter.login.LoginViewModel;
import interface_adapter.logout.LogoutController;
import interface_adapter.logout.LogoutPresenter;
import interface_adapter.signup.SignupController;
import interface_adapter.signup.SignupPresenter;
import interface_adapter.signup.SignupViewModel;
import interface_adapter.connections.ConnectionsViewModel;
import interface_adapter.connections.ConnectionsPresenter;
import interface_adapter.connections.ConnectionsController;
import use_case.multiple_choice.QuestionDAI;
import view.ConnectionsGameView;
import interface_adapter.crossword.CrosswordController;
import interface_adapter.crossword.CrosswordPresenter;
import interface_adapter.crossword.CrosswordViewModel;
import interface_adapter.multiple_choice.QuizController;
import interface_adapter.multiple_choice.QuizPresenter;
import interface_adapter.multiple_choice.QuizViewModel;
import interface_adapter.multiple_choice.ResultsViewModel;
import use_case.crossword.start.StartCrosswordInputBoundary;
import use_case.crossword.start.StartCrosswordInteractor;
import use_case.crossword.submit.SubmitCrosswordInputBoundary;
import use_case.crossword.submit.SubmitCrosswordInteractor;
import use_case.multiple_choice.quiz.QuizInteractor;
import use_case.multiple_choice.submit.SubmitAnswerInteractor;
import data_access.QuestionDAO;
import data_access.SimpleDaoSelector;
import view.CrosswordView;
import use_case.game.GameInteractor;
import use_case.game.GameSubmitGuessInteractor;
import use_case.game.GameStateRepository;
import use_case.game.GameOutputBoundary;
import use_case.game.GameSubmitGuessOutputBoundary;
import use_case.game.GameDataAccessInterface;
import data_access.ApiGameDataAccess;
import data_access.InMemoryGameStateRepository;
import use_case.change_password.ChangePasswordInputBoundary;
import use_case.change_password.ChangePasswordInteractor;
import use_case.change_password.ChangePasswordOutputBoundary;
import use_case.login.LoginInputBoundary;
import use_case.login.LoginInteractor;
import use_case.login.LoginOutputBoundary;
import use_case.logout.LogoutInputBoundary;
import use_case.logout.LogoutInteractor;
import use_case.logout.LogoutOutputBoundary;
import use_case.signup.SignupInputBoundary;
import use_case.signup.SignupInteractor;
import use_case.signup.SignupOutputBoundary;
import use_case.password_validator.PasswordValidatorServiceDataAccessInterface; // NEW IMPORT
import view.*;

import javax.swing.*;
import java.awt.*;

public class AppBuilder {
    private final JPanel cardPanel = new JPanel();
    private final CardLayout cardLayout = new CardLayout();
    final UserFactory userFactory = new UserFactory();
    final ViewManagerModel viewManagerModel = new ViewManagerModel();
    ViewManager viewManager = new ViewManager(cardPanel, cardLayout, viewManagerModel);

    // DAO for user data
    final FileUserDataAccessObject userDataAccessObject = new FileUserDataAccessObject("users.csv", userFactory);

    // NEW: DAO for password validation
    final PasswordValidatorServiceDataAccessInterface passwordValidator = new PwnedPasswordDataAccessObject();

    private SignupView signupView;
    private SignupViewModel signupViewModel;
    private LoginViewModel loginViewModel;
    private LoggedInViewModel loggedInViewModel;
    private LoggedInView loggedInView;
    private LoginView loginView;
    private ChangePasswordView changePasswordView;
    // Connections game additions
    private ConnectionsViewModel connectionsViewModel;
    private ConnectionsPresenter connectionsPresenter;
    private ConnectionsController connectionsController;
    private ConnectionsGameView connectionsGameView;
    private GameStateRepository gameStateRepository;
    private GameDataAccessInterface gameDataAccess;

    // Crossword additions
    private CrosswordViewModel crosswordViewModel;
    private CrosswordPresenter crosswordPresenter;
    private CrosswordController crosswordController;
    private JPanel crosswordRoot;

    // Multiple choice additions
    private QuizViewModel quizViewModel;
    private ResultsViewModel resultsViewModel;
    private QuizPresenter quizPresenter;
    private QuizController quizController;
    private QuestionDAI questionDAO;

    public AppBuilder() {
        cardPanel.setLayout(cardLayout);
    }

    public AppBuilder addSignupView() {
        signupViewModel = new SignupViewModel();
        signupView = new SignupView(signupViewModel);
        cardPanel.add(signupView, signupView.getViewName());
        return this;
    }

    public AppBuilder addLoginView() {
        loginViewModel = new LoginViewModel();
        loginView = new LoginView(loginViewModel, viewManagerModel);
        cardPanel.add(loginView, loginView.getViewName());
        return this;
    }

    public AppBuilder addLoggedInView() {
        loggedInViewModel = new LoggedInViewModel();
        loggedInView = new LoggedInView(loggedInViewModel, viewManagerModel);
        cardPanel.add(loggedInView, loggedInView.getViewName());
        return this;
    }

    public AppBuilder addChangePasswordView() {
        changePasswordView = new ChangePasswordView(loggedInViewModel, viewManagerModel);
        cardPanel.add(changePasswordView, changePasswordView.getViewName());
        return this;
    }
    public AppBuilder addConnectionsView() {
        connectionsViewModel = new ConnectionsViewModel();
        connectionsPresenter = new ConnectionsPresenter(connectionsViewModel, viewManagerModel);
        connectionsGameView = new ConnectionsGameView(connectionsViewModel, null); // controller set later
        connectionsGameView.setViewManagerModel(viewManagerModel); // Wire ViewManagerModel for Back button
        cardPanel.add(connectionsGameView, connectionsViewModel.getViewName());
        return this;
    }

    public AppBuilder addMultipleChoiceViews() {
        quizViewModel = new QuizViewModel();
        resultsViewModel = new ResultsViewModel();
        quizPresenter = new QuizPresenter(quizViewModel, resultsViewModel, viewManagerModel);

        questionDAO = new QuestionDAO();
        questionDAO.loadData();

        QuizInteractor quizInteractor = new QuizInteractor(questionDAO, quizPresenter);
        quizController = new QuizController(quizInteractor);

        CategorySelectionView categorySelectionView = new CategorySelectionView(quizViewModel);
        categorySelectionView.setQuizController(quizController);
        cardPanel.add(categorySelectionView, categorySelectionView.getViewName());

        QuizView quizView = new QuizView(quizController, quizViewModel);
        cardPanel.add(quizView, quizView.getViewName());

        ResultsView resultsView = new ResultsView(resultsViewModel, viewManagerModel);
        cardPanel.add(resultsView, resultsView.getViewName());

        return this;
    }

    public AppBuilder addSignupUseCase() {
        final SignupOutputBoundary signupOutputBoundary = new SignupPresenter(viewManagerModel,
                signupViewModel, loginViewModel);

        // UPDATED: Added passwordValidator to constructor
        final SignupInputBoundary userSignupInteractor = new SignupInteractor(
                userDataAccessObject,
                signupOutputBoundary,
                userFactory,
                passwordValidator);

        SignupController controller = new SignupController(userSignupInteractor);
        signupView.setSignupController(controller);
        return this;
    }

    public AppBuilder addLoginUseCase() {
        final LoginOutputBoundary loginOutputBoundary = new LoginPresenter(viewManagerModel,
                loggedInViewModel, loginViewModel);
        final LoginInputBoundary loginInteractor = new LoginInteractor(
                userDataAccessObject, loginOutputBoundary);

        LoginController loginController = new LoginController(loginInteractor);
        loginView.setLoginController(loginController);
        return this;
    }

    public AppBuilder addChangePasswordUseCase() {
        final ChangePasswordOutputBoundary changePasswordOutputBoundary = new ChangePasswordPresenter(viewManagerModel,
                loggedInViewModel);

        // UPDATED: Added passwordValidator to constructor
        final ChangePasswordInputBoundary changePasswordInteractor = new ChangePasswordInteractor(
                userDataAccessObject,
                changePasswordOutputBoundary,
                userFactory,
                passwordValidator);

        ChangePasswordController changePasswordController = new ChangePasswordController(changePasswordInteractor);
        changePasswordView.setChangePasswordController(changePasswordController);
        return this;
    }

    public AppBuilder addLogoutUseCase() {
        final LogoutOutputBoundary logoutOutputBoundary = new LogoutPresenter(viewManagerModel,
                loggedInViewModel, loginViewModel);

        final LogoutInputBoundary logoutInteractor =
                new LogoutInteractor(userDataAccessObject, logoutOutputBoundary);

        final LogoutController logoutController = new LogoutController(logoutInteractor);
        loggedInView.setLogoutController(logoutController);
        return this;
    }

    public AppBuilder addConnectionsUseCase() {
        // Set up repository and DAO
        gameStateRepository = new InMemoryGameStateRepository();
        gameDataAccess = new ApiGameDataAccess();

        // Interactors
        GameOutputBoundary loadPresenter = connectionsPresenter;
        GameSubmitGuessOutputBoundary submitPresenter = connectionsPresenter;
        GameInteractor loadInteractor = new GameInteractor(gameDataAccess, gameStateRepository, loadPresenter);
        GameSubmitGuessInteractor submitInteractor = new GameSubmitGuessInteractor(gameStateRepository, submitPresenter);

        // Controller
        connectionsController = new ConnectionsController(loadInteractor, submitInteractor);

        // Wire controller into views
        if (connectionsGameView != null) {
            connectionsGameView.setController(connectionsController);
        }
        if (loggedInView != null) {
            loggedInView.setConnectionsController(connectionsController);
        }
        return this;
    }

    public AppBuilder addCrosswordUseCase() {
        // ViewModel and Presenter
        crosswordViewModel = new CrosswordViewModel();
        crosswordPresenter = new CrosswordPresenter(crosswordViewModel);

        // DAO selector shared by both use cases
        final SimpleDaoSelector selector = new SimpleDaoSelector();

        // Interactors
        final StartCrosswordInputBoundary startInteractor =
                new StartCrosswordInteractor(selector, crosswordPresenter);
        final SubmitCrosswordInputBoundary submitInteractor =
                new SubmitCrosswordInteractor(selector, crosswordPresenter);

        // Controller
        crosswordController = new CrosswordController(startInteractor, submitInteractor, selector);

        // Build a local CardLayout root for the crossword flow
        crosswordRoot = new JPanel(new CardLayout());

        // ✅ FIXED: pass viewManagerModel as the first argument
        JPanel decision = CrosswordView.createDecisionPanel(
                viewManagerModel,
                crosswordController,
                crosswordViewModel,
                () -> ((CardLayout) crosswordRoot.getLayout()).show(crosswordRoot, "EASY"),
                () -> ((CardLayout) crosswordRoot.getLayout()).show(crosswordRoot, "MEDIUM"),
                () -> ((CardLayout) crosswordRoot.getLayout()).show(crosswordRoot, "HARD")
        );

        JPanel easy   = CrosswordView.createPuzzlePanel(crosswordController, crosswordViewModel, "EASY");
        JPanel medium = CrosswordView.createPuzzlePanel(crosswordController, crosswordViewModel, "MEDIUM");
        JPanel hard   = CrosswordView.createPuzzlePanel(crosswordController, crosswordViewModel, "HARD");
        JPanel exit   = CrosswordView.createExitPanel(crosswordViewModel);

        crosswordRoot.add(decision, "DECISION");
        crosswordRoot.add(easy, "EASY");
        crosswordRoot.add(medium, "MEDIUM");
        crosswordRoot.add(hard, "HARD");
        crosswordRoot.add(exit, "EXIT");
        ((CardLayout) crosswordRoot.getLayout()).show(crosswordRoot, "DECISION");

        // Add crossword as a single card to the app's ViewManager
        cardPanel.add(crosswordRoot, "crossword");

        // Wire entrance from LoggedInView
        if (loggedInView != null) {
            loggedInView.setCrosswordController(crosswordController);
        }

        return this;
    }


    public AppBuilder addMultipleChoiceUseCase() {
        // Wire submit answer interactor when quiz starts
        // Create a new SubmitAnswerInteractor each time a quiz starts (when imagePath changes)
        // to ensure it references the current QuizSession
        quizViewModel.addPropertyChangeListener(evt -> {
            if ("imagePath".equals(evt.getPropertyName())) {
                if (quizController.getQuizInteractor().getCurrentSession() != null) {
                    SubmitAnswerInteractor submitAnswerInteractor =
                            new SubmitAnswerInteractor(
                                    quizController.getQuizInteractor().getCurrentSession(),
                                    quizPresenter,
                                    quizPresenter);
                    quizController.setSubmitAnswerInteractor(submitAnswerInteractor);
                }
            }
        });

        // Wire controller into LoggedInView
        if (loggedInView != null) {
            loggedInView.setMultipleChoiceController(quizController);
            // Wire ResultsViewModel to LoggedInView for high score updates
            loggedInView.setResultsViewModel(resultsViewModel);
        }

        return this;
    }

    public JFrame build() {
        final JFrame application = new JFrame("User Login Example");
        application.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        application.add(cardPanel);

        viewManagerModel.setState(signupView.getViewName());
        viewManagerModel.firePropertyChange();

        return application;
    }
}
