package app;

import data_access.QuestionDAO;
import interface_adapter.multiple_choice.QuizController;
import interface_adapter.multiple_choice.QuizPresenter;
import interface_adapter.multiple_choice.QuizViewModel;
import interface_adapter.multiple_choice.ResultsViewModel;
import use_case.QuestionDAI;
import use_case.quiz.QuizInteractor;
import use_case.submit.SubmitAnswerInteractor;
import view.CategorySelectionView;
import view.QuizView;
import view.ResultsView;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        QuestionDAI repository = new QuestionDAO();
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