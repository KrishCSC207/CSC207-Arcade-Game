package interfaceadapter.multiplechoice;

import interfaceadapter.ViewManagerModel;
import usecase.multiplechoice.quiz.QuizOutputBoundary;
import usecase.multiplechoice.quiz.QuizOutputData;
import usecase.multiplechoice.submit.SubmitAnswerOutputBoundary;
import usecase.multiplechoice.submit.SubmitAnswerOutputData;

/**
 * Presenter that formats data from interactors for the ViewModels.
 */
public class QuizPresenter implements QuizOutputBoundary, SubmitAnswerOutputBoundary {

    private final QuizViewModel quizViewModel;
    private final ResultsViewModel resultsViewModel;
    private final ViewManagerModel viewManagerModel;

    public QuizPresenter(QuizViewModel quizViewModel, ResultsViewModel resultsViewModel, ViewManagerModel viewManagerModel) {
        this.quizViewModel = quizViewModel;
        this.resultsViewModel = resultsViewModel;
        this.viewManagerModel = viewManagerModel;
    }

    @Override
    public void prepareQuizView(QuizOutputData data) {
        quizViewModel.setCurrentImagePath(data.getImagePath());
        quizViewModel.setQuestionProgressLabel(data.getQuestionProgress());
        quizViewModel.setSelectedButton(null);
        quizViewModel.setFeedbackState("NONE");

        // Switch to quiz view when quiz starts
        viewManagerModel.setState("quiz");
        viewManagerModel.firePropertyChange();
    }

    @Override
    public void prepareSuccessView(SubmitAnswerOutputData data) {
        quizViewModel.setSelectedButton(data.getSelectedAnswer());
        quizViewModel.setFeedbackState("CORRECT");
    }

    @Override
    public void prepareFailView(SubmitAnswerOutputData data) {
        quizViewModel.setSelectedButton(data.getSelectedAnswer());
        quizViewModel.setFeedbackState("INCORRECT");
    }

    @Override
    public void prepareResultsView(double accuracy, long totalTimeMs) {
        resultsViewModel.setAccuracy(accuracy);
        resultsViewModel.setTotalTimeMs(totalTimeMs);

        // Switch to results view when quiz is complete
        viewManagerModel.setState("results");
        viewManagerModel.firePropertyChange();
    }
}
