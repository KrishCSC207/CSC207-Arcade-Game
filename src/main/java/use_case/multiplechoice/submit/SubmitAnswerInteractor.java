package use_case.multiplechoice.submit;

import entity.QuizQuestion;
import entity.QuizSession;
import use_case.multiplechoice.quiz.QuizOutputBoundary;
import use_case.multiplechoice.quiz.QuizOutputData;

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
            quizSession.recordAnswer(true);
            submitAnswerPresenter.prepareSuccessView(outputData);
        } else {
            quizSession.recordAnswer(false);
            submitAnswerPresenter.prepareFailView(outputData);
        }
    }

    /**
     * Advances to the next question after a correct answer.
     * Should be called by the controller after a delay to show feedback.
     */
    public void advance() {
        boolean hasMoreQuestions = quizSession.advanceToNextQuestion();

        if (hasMoreQuestions) {
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
            quizSession.finishQuiz();
            double accuracy = quizSession.getAccuracy();
            long totalTime = quizSession.getTotalTime();
            submitAnswerPresenter.prepareResultsView(accuracy, totalTime);
        }
    }
}
