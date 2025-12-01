<<<<<<< HEAD
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
=======
package app;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        // Make UI fonts bigger globally
        setGlobalUIFont(16f);
        AppBuilder appBuilder = new AppBuilder();
        JFrame application = appBuilder
                .addLoginView()
                .addSignupView()
                .addLoggedInView()
                .addChangePasswordView()
                .addConnectionsView()
                .addSignupUseCase()
                .addLoginUseCase()
                .addChangePasswordUseCase()
                .addLogoutUseCase()
                .addConnectionsUseCases()
                .build();

        application.pack();
        application.setLocationRelativeTo(null);
        application.setVisible(true);
    }

    private static void setGlobalUIFont(float size) {
        UIDefaults defaults = UIManager.getDefaults();
        for (Object key : defaults.keySet()) {
            Object value = defaults.get(key);
            if (value instanceof Font) {
                Font f = (Font) value;
                defaults.put(key, new FontUIResource(f.deriveFont(size)));
            }
        }
    }
}
>>>>>>> 10b763f8c79724cfad3ae4dd49d1be2430427206
