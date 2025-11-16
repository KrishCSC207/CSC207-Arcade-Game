import interface_adapters.crossword.*;
import interface_adapters.crossword.data_access.LocalCrosswordPuzzleDataAccess;
import use_case.crossword.*;
import use_case.crossword.start.*;
import use_case.crossword.submit.SubmitCrosswordInputBoundary;
import use_case.crossword.submit.SubmitCrosswordInteractor;
import view.crossword.CrosswordView;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {

        // CA layers

        // ViewModel
        CrosswordViewModel viewModel = new CrosswordViewModel();

        // Presenter
        CrosswordPresenter presenter = new CrosswordPresenter(viewModel);

        // Data access (hardocdedd)
        CrosswordPuzzleDataAccessInterface dataAccess = new LocalCrosswordPuzzleDataAccess();

        // Use case interactors
        StartCrosswordInputBoundary startInteractor = new StartCrosswordInteractor(dataAccess, presenter);

        SubmitCrosswordInputBoundary submitInteractor = new SubmitCrosswordInteractor(dataAccess, presenter);

        // Controller
        CrosswordController controller = new CrosswordController(startInteractor, submitInteractor);

        // building ui

        JFrame frame = new JFrame("CSC207 Crossword");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // View (calls controller.startCrossword internally)
        CrosswordView view = new CrosswordView(controller, viewModel);

        frame.add(view);
        frame.pack();
        frame.setVisible(true);
    }
}

// Note flow map: VIEW → CONTROLLER → INPUT BOUNDARY → INTERACTOR → OUTPUT BOUNDARY → PRESENTER → VIEWMODEL → VIEW
// Presenter implements the output boundary interface, maybe we can change later
// Need to implement the submit use case to check answers, maybe cna change UI a little bit
// Need to setup data access properly
// maybe we can use the java.beans stuff to send evnets, idk i saw it in the ca lab

