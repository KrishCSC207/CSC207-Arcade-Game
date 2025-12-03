package usecase.crossword.submit;

import usecase.crossword.CrosswordPuzzleDataAccessInterface;
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
                String userAnswer = userAnswers.get(i).trim().toLowerCase();
                boolean found = false;
                for (int j = 0; j < correctAnswers.size(); j++) {
                    if (userAnswer.equals(correctAnswers.get(j).trim().toLowerCase())) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    allCorrect = false;
                    break;
                }
            }
        }

        long elapsedTime = System.currentTimeMillis() - inputData.getStartTime();
        SubmitCrosswordOutputData outputData = new SubmitCrosswordOutputData(allCorrect, elapsedTime);
        presenter.presentResult(outputData);
    }
}