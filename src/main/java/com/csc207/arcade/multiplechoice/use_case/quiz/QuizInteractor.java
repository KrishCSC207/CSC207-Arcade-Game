package com.csc207.arcade.multiplechoice.use_case.quiz;

import com.csc207.arcade.multiplechoice.entities.QuizQuestion;
import com.csc207.arcade.multiplechoice.entities.QuizSession;
import com.csc207.arcade.multiplechoice.use_case.QuestionRepository;

import java.util.List;

/**
 * Interactor for starting a quiz and loading the first question.
 */
public class QuizInteractor implements QuizInputBoundary {
    private final QuestionRepository questionRepository;
    private final QuizOutputBoundary quizPresenter;
    private QuizSession currentSession;

    public QuizInteractor(QuestionRepository questionRepository, QuizOutputBoundary quizPresenter) {
        this.questionRepository = questionRepository;
        this.quizPresenter = quizPresenter;
    }

    @Override
    public void execute(QuizInputData inputData) {
        // Load 15 questions
        List<QuizQuestion> questions = questionRepository.getQuestions(15);
        
        // Create a new quiz session
        currentSession = new QuizSession(questions);
        
        // Get the first question
        QuizQuestion firstQuestion = currentSession.getCurrentQuestion();
        
        // Prepare output data
        String progressLabel = String.format("Question %d/%d", 
            currentSession.getCurrentQuestionIndex() + 1, 
            currentSession.getTotalQuestions());
        
        QuizOutputData outputData = new QuizOutputData(
            firstQuestion.getImagePath(), 
            progressLabel
        );
        
        // Send to presenter
        quizPresenter.prepareQuizView(outputData);
    }

    public QuizSession getCurrentSession() {
        return currentSession;
    }
}
