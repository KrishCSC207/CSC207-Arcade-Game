package interface_adapters.crossword;

import use_case.crossword.start.StartCrosswordOutputBoundary;
import use_case.crossword.submit.SubmitCrosswordOutputBoundary;
import use_case.crossword.submit.SubmitCrosswordOutputData;

public class CrosswordPresenter implements StartCrosswordOutputBoundary, SubmitCrosswordOutputBoundary {

    private final CrosswordViewModel viewModel;

    public CrosswordPresenter(CrosswordViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void presentCrossword(String puzzleId, String imagePath, int numSolutions) {
        viewModel.setPuzzleId(puzzleId);
        viewModel.setImagePath(imagePath);
        viewModel.setNumSolutions(numSolutions);
        //viewModel.setStatusMessage("Crossword loaded.");
    }

    @Override
    public void presentResult(SubmitCrosswordOutputData outputData) {
        if (outputData.isAllCorrect()) {
            viewModel.setSubmissionCorrect(true);
            viewModel.setFeedbackMessage("Congratulations! All answers are correct!");
        } else {
            viewModel.setSubmissionCorrect(false);
            viewModel.setFeedbackMessage("Some answers are incorrect. Try again!");
        }
    }
}