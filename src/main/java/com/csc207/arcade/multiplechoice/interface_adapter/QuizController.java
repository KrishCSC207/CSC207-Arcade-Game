package com.csc207.arcade.multiplechoice.interface_adapter;

import com.csc207.arcade.multiplechoice.use_case.quiz.QuizInputBoundary;
import com.csc207.arcade.multiplechoice.use_case.quiz.QuizInputData;
import com.csc207.arcade.multiplechoice.use_case.submit.SubmitAnswerInputBoundary;
import com.csc207.arcade.multiplechoice.use_case.submit.SubmitAnswerInputData;

/**
 * Controller that handles user interactions for the quiz.
 */
public class QuizController {
    private final QuizInputBoundary quizInteractor;
    private final SubmitAnswerInputBoundary submitAnswerInteractor;

    public QuizController(QuizInputBoundary quizInteractor, 
                         SubmitAnswerInputBoundary submitAnswerInteractor) {
        this.quizInteractor = quizInteractor;
        this.submitAnswerInteractor = submitAnswerInteractor;
    }

    /**
     * Starts a new quiz.
     */
    public void startQuiz() {
        QuizInputData inputData = new QuizInputData();
        quizInteractor.execute(inputData);
    }

    /**
     * Submits an answer for the current question.
     *
     * @param answer The selected answer (A, B, C, or D)
     */
    public void submitAnswer(String answer) {
        SubmitAnswerInputData inputData = new SubmitAnswerInputData(answer);
        submitAnswerInteractor.execute(inputData);
    }
    
    /**
     * Advances to the next question.
     * Called by the view after showing feedback for a correct answer.
     */
    public void advanceToNextQuestion() {
        if (submitAnswerInteractor instanceof com.csc207.arcade.multiplechoice.use_case.submit.SubmitAnswerInteractor) {
            ((com.csc207.arcade.multiplechoice.use_case.submit.SubmitAnswerInteractor) submitAnswerInteractor)
                .advanceToNextQuestion();
        }
    }
}
