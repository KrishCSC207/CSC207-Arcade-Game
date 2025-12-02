package interface_adapter.multiple_choice;

import use_case.quiz.QuizOutputBoundary;
import use_case.quiz.QuizOutputData;
import use_case.submit.SubmitAnswerOutputBoundary;
import use_case.submit.SubmitAnswerOutputData;

/**
 * Presenter that formats data from interactors for the ViewModels.
 */
public class QuizPresenter implements QuizOutputBoundary, SubmitAnswerOutputBoundary {

    private final QuizViewModel quizViewModel;
    private final ResultsViewModel resultsViewModel;

    public QuizPresenter(QuizViewModel quizViewModel, ResultsViewModel resultsViewModel) {
        this.quizViewModel = quizViewModel;
        this.resultsViewModel = resultsViewModel;
    }

    @Override
    public void prepareQuizView(QuizOutputData data) {
        quizViewModel.setCurrentImagePath(data.getImagePath());
        quizViewModel.setQuestionProgressLabel(data.getQuestionProgress());

        // Reset feedback state and incorrect button for new question
        quizViewModel.setIncorrectButton(null);
        quizViewModel.setFeedbackState("NONE");
    }

    @Override
    public void prepareSuccessView(SubmitAnswerOutputData data) {
        // Record which button was selected
        quizViewModel.setIncorrectButton(data.getSelectedAnswer());
        // Inform view the choice was correct and display the green color
        quizViewModel.setFeedbackState("CORRECT");
    }

    @Override
    public void prepareFailView(SubmitAnswerOutputData data) {
        quizViewModel.setIncorrectButton(data.getSelectedAnswer());
        quizViewModel.setFeedbackState("INCORRECT");
    }

    @Override
    public void prepareResultsView(double accuracy, long totalTimeMs) {
        resultsViewModel.setAccuracy(accuracy);
        resultsViewModel.setTotalTimeMs(totalTimeMs);
    }
}