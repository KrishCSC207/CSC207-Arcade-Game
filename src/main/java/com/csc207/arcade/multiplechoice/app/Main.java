package com.csc207.arcade.multiplechoice.app;

import com.csc207.arcade.multiplechoice.data_access.JsonQuestionRepository;
import com.csc207.arcade.multiplechoice.interface_adapter.QuizController;
import com.csc207.arcade.multiplechoice.interface_adapter.QuizPresenter;
import com.csc207.arcade.multiplechoice.interface_adapter.QuizViewModel;
import com.csc207.arcade.multiplechoice.interface_adapter.ResultsViewModel;
import com.csc207.arcade.multiplechoice.use_case.QuestionRepository;
import com.csc207.arcade.multiplechoice.use_case.quiz.QuizInteractor;
import com.csc207.arcade.multiplechoice.use_case.submit.SubmitAnswerInteractor;
import com.csc207.arcade.multiplechoice.view.QuizView;
import com.csc207.arcade.multiplechoice.view.ResultsView;

import javax.swing.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Main entry point for the Multiple Choice Quiz application.
 */
public class Main {
    public static void main(String[] args) {
        // Step 1: Initialize data
        System.out.println("Initializing quiz data...");
        DataInitializer.run();
        
        // Step 2: Dependency Injection - Create all components
        QuestionRepository repository = new JsonQuestionRepository();
        repository.loadData();
        
        QuizViewModel quizViewModel = new QuizViewModel();
        ResultsViewModel resultsViewModel = new ResultsViewModel();
        
        QuizPresenter presenter = new QuizPresenter(quizViewModel, resultsViewModel);
        
        QuizInteractor quizInteractor = new QuizInteractor(repository, presenter);
        
        // Note: SubmitAnswerInteractor needs the session from QuizInteractor
        // We'll create it after the quiz starts
        
        // Step 3: Launch UI
        SwingUtilities.invokeLater(() -> {
            // Create a wrapper controller that will be updated with the session
            QuizControllerWrapper controllerWrapper = new QuizControllerWrapper();
            
            QuizView quizView = new QuizView(controllerWrapper, quizViewModel);
            ResultsView resultsView = new ResultsView(resultsViewModel);
            
            // Listen for results to show results view
            resultsViewModel.addPropertyChangeListener(new PropertyChangeListener() {
                private boolean resultsShown = false;
                
                @Override
                public void propertyChange(PropertyChangeEvent evt) {
                    String name = evt.getPropertyName();
                    if (!resultsShown && ("accuracy".equals(name) || "totalTimeMs".equals(name))) {
                        resultsShown = true;
                        SwingUtilities.invokeLater(() -> {
                            quizView.setVisible(false); // Dispose instead of just hiding
                            resultsView.setVisible(true); // Use setVisible instead of deprecated show()
                        });
                    }
                }
            });
            
            quizView.setVisible(true);
            
            // Start the quiz
            quizInteractor.execute(new com.csc207.arcade.multiplechoice.use_case.quiz.QuizInputData());
            
            // Now create the submit answer interactor with the session
            SubmitAnswerInteractor submitAnswerInteractor = new SubmitAnswerInteractor(
                quizInteractor.getCurrentSession(),
                presenter,
                presenter
            );
            
            // Create the actual controller
            QuizController actualController = new QuizController(quizInteractor, submitAnswerInteractor);
            controllerWrapper.setActualController(actualController);
        });
    }
    
    /**
     * Wrapper class to allow lazy initialization of the controller.
     */
    private static class QuizControllerWrapper extends QuizController {
        private QuizController actualController;
        
        public QuizControllerWrapper() {
            super(null, null);
        }
        
        public void setActualController(QuizController controller) {
            this.actualController = controller;
        }
        
        @Override
        public void startQuiz() {
            if (actualController != null) {
                actualController.startQuiz();
            }
        }
        
        @Override
        public void submitAnswer(String answer) {
            if (actualController != null) {
                actualController.submitAnswer(answer);
            }
        }
        
        @Override
        public void advanceToNextQuestion() {
            if (actualController != null) {
                actualController.advanceToNextQuestion();
            }
        }
    }
}
