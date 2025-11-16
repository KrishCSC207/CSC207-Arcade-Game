package use_case.crossword.submit;

import use_case.crossword.CrosswordPuzzleDataAccessInterface;
import java.util.List;

public class SubmitCrosswordInteractor implements SubmitCrosswordInputBoundary {

    private final CrosswordPuzzleDataAccessInterface dataAccess;
    private final SubmitCrosswordOutputBoundary presenter;

    public SubmitCrosswordInteractor(CrosswordPuzzleDataAccessInterface dataAccess,
                                     SubmitCrosswordOutputBoundary presenter) {
        this.dataAccess = dataAccess;
        this.presenter = presenter;
    }

    @Override
    public void execute(SubmitCrosswordInputData inputData) {
        List<String> userAnswers = inputData.getUserAnswers();

        List<String> correctAnswers = dataAccess.getCurrentPuzzleSolutions();

        boolean allCorrect = true;
        if (userAnswers.size() != correctAnswers.size()) {
            allCorrect = false;
        } else {
            for (int i = 0; i < userAnswers.size(); i++) {
                if (!userAnswers.get(i).trim().equalsIgnoreCase(correctAnswers.get(i))) {
                    allCorrect = false;
                    break;
                }
            }
        }
        SubmitCrosswordOutputData outputData = new SubmitCrosswordOutputData(allCorrect);
        presenter.presentResult(outputData);
    }
}