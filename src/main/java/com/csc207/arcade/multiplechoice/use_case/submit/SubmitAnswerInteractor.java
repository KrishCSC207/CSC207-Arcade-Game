package com.csc207.arcade.multiplechoice.use_case.submit;

import com.csc207.arcade.multiplechoice.entities.QuizQuestion;
import com.csc207.arcade.multiplechoice.entities.QuizSession;
import com.csc207.arcade.multiplechoice.use_case.quiz.QuizOutputBoundary;
import com.csc207.arcade.multiplechoice.use_case.quiz.QuizOutputData;

/**
 * Interactor for submitting and checking answers.
 */
public class SubmitAnswerInteractor implements SubmitAnswerInputBoundary {
    private final QuizSession quizSession;
    private final SubmitAnswerOutputBoundary submitAnswerPresenter;
    private final QuizOutputBoundary quizPresenter;

    public SubmitAnswerInteractor(QuizSession quizSession, 
                                   SubmitAnswerOutputBoundary submitAnswerPresenter,
                                   QuizOutputBoundary quizPresenter) {
        this.quizSession = quizSession;
        this.submitAnswerPresenter = submitAnswerPresenter;
        this.quizPresenter = quizPresenter;
    }

    @Override
    public void execute(SubmitAnswerInputData inputData) {
        String selectedAnswer = inputData.getSelectedAnswer();
        QuizQuestion currentQuestion = quizSession.getCurrentQuestion();
        String correctAnswer = currentQuestion.getCorrectAnswer();
        
        boolean isCorrect = selectedAnswer.equals(correctAnswer);
        
        SubmitAnswerOutputData outputData = new SubmitAnswerOutputData(
            isCorrect, selectedAnswer, correctAnswer
        );
        
        if (isCorrect) {
            // Record the correct answer
            quizSession.recordAnswer(true);
            
            // Notify presenter of success
            submitAnswerPresenter.prepareSuccessView(outputData);
            
            // Note: Do not advance immediately - let the view handle the delay
            // and call advanceToNextQuestion() after showing the green highlight
        } else {
            // Record the incorrect answer
            quizSession.recordAnswer(false);
            
            // Notify presenter of failure (do not advance)
            submitAnswerPresenter.prepareFailView(outputData);
        }
    }
    
    /**
     * Advances to the next question after a correct answer.
     * Should be called by the controller after a delay to show feedback.
     */
    public void advanceToNextQuestion() {
        // Advance to next question
        boolean hasMoreQuestions = quizSession.advanceToNextQuestion();
        
        if (hasMoreQuestions) {
            // Load next question
            QuizQuestion nextQuestion = quizSession.getCurrentQuestion();
            String progressLabel = String.format("Question %d/%d", 
                quizSession.getCurrentQuestionIndex() + 1, 
                quizSession.getTotalQuestions());
            
            QuizOutputData quizOutputData = new QuizOutputData(
                nextQuestion.getImagePath(), 
                progressLabel
            );
            quizPresenter.prepareQuizView(quizOutputData);
        } else {
            // Quiz is over, show results
            quizSession.finishQuiz();
            double accuracy = quizSession.getAccuracy();
            long totalTime = quizSession.getTotalTime();
            submitAnswerPresenter.prepareResultsView(accuracy, totalTime);
        }
    }
}
