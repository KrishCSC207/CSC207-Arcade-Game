package com.csc207.arcade.multiplechoice.app;

import com.csc207.arcade.multiplechoice.data_access.QuestionDAO;
import com.csc207.arcade.multiplechoice.interface_adapter.QuizController;
import com.csc207.arcade.multiplechoice.interface_adapter.QuizPresenter;
import com.csc207.arcade.multiplechoice.interface_adapter.QuizViewModel;
import com.csc207.arcade.multiplechoice.interface_adapter.ResultsViewModel;
import com.csc207.arcade.multiplechoice.use_case.QuestionDAI;
import com.csc207.arcade.multiplechoice.use_case.quiz.QuizInteractor;
import com.csc207.arcade.multiplechoice.use_case.submit.SubmitAnswerInteractor;
import com.csc207.arcade.multiplechoice.view.CategorySelectionView;
import com.csc207.arcade.multiplechoice.view.QuizView;
import com.csc207.arcade.multiplechoice.view.ResultsView;

import javax.swing.*;
import java.beans.PropertyChangeListener;

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