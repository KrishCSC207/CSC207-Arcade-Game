package interfaceadapter.crossword;

import usecase.crossword.start.StartCrosswordOutputBoundary;
import usecase.crossword.submit.SubmitCrosswordOutputBoundary;
import usecase.crossword.submit.SubmitCrosswordOutputData;

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