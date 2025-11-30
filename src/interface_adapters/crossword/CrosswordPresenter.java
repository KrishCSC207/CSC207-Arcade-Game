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
        viewModel.setStartTime(System.currentTimeMillis());
    }

    @Override
    public void presentResult(SubmitCrosswordOutputData data) {
        if (data.isAllCorrect()) {
            viewModel.setElapsedTime(data.getElapsedTime());
            viewModel.setFeedbackMessage("All correct! 🎉");
            viewModel.setCompleted(true);
        } else {
            viewModel.setFeedbackMessage("Some answers are incorrect — try again.");
            viewModel.setCompleted(false);
        }
    }
}