import interface_adapters.crossword.*;
import use_case.crossword.start.*;
import use_case.crossword.submit.SubmitCrosswordInputBoundary;
import use_case.crossword.submit.SubmitCrosswordInteractor;
import interface_adapters.crossword.data_access.SimpleDaoSelector;
import java.awt.CardLayout;
import view.crossword.DecisionPage;
import view.crossword.easy_wordsearch;
import view.crossword.medium_wordsearch;
import view.crossword.hard_wordsearch;
import view.crossword.ExitPage;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {

        // CA layers

        // ViewModel
        CrosswordViewModel viewModel = new CrosswordViewModel();

        // Presenter
        CrosswordPresenter presenter = new CrosswordPresenter(viewModel);

        // Shared selector implements CrosswordPuzzleDataAccessInterface
        SimpleDaoSelector selector = new SimpleDaoSelector();

        // Use case interactors (both use the selector)
        StartCrosswordInputBoundary startInteractor = new StartCrosswordInteractor(selector, presenter);
        SubmitCrosswordInputBoundary submitInteractor = new SubmitCrosswordInteractor(selector, presenter);

        // Controller that can set difficulty
        CrosswordController controller = new CrosswordController(startInteractor, submitInteractor, selector);

        JFrame frame = new JFrame("CSC207 Crossword");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Root container with CardLayout
        JPanel root = new JPanel(new CardLayout());

        // Build your three crossword panels (they must accept controller + viewModel)
        JPanel easyView = new easy_wordsearch(controller, viewModel);
        JPanel mediumView = new medium_wordsearch(controller, viewModel);
        JPanel hardView = new hard_wordsearch(controller, viewModel);
        
        // Exit page
        ExitPage exitPage = new ExitPage(viewModel);

        // Decision page: on click -> set difficulty + show the chosen card
        DecisionPage decision = new DecisionPage(
                controller, viewModel,
                () -> ((CardLayout) root.getLayout()).show(root, "EASY"),
                () -> ((CardLayout) root.getLayout()).show(root, "MEDIUM"),
                () -> ((CardLayout) root.getLayout()).show(root, "HARD")
        );

        // Register cards
        root.add(decision, "DECISION");
        root.add(easyView, "EASY");
        root.add(mediumView, "MEDIUM");
        root.add(hardView, "HARD");
        root.add(exitPage, "EXIT");

        // Show Decision first
        frame.setContentPane(root);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        ((CardLayout) root.getLayout()).show(root, "DECISION");

    }
}

// Note flow map: VIEW → CONTROLLER → INPUT BOUNDARY → INTERACTOR → OUTPUT BOUNDARY → PRESENTER → VIEWMODEL → VIEW
// Presenter implements the output boundary interface, maybe we can change later
// Need to implement the submit use case to check answers, maybe cna change UI a little bit
// Need to setup data access properly
// maybe we can use the java.beans stuff to send evnets, idk i saw it in the ca lab

