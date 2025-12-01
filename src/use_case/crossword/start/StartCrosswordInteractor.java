package use_case.crossword.start;

import entity.crossword.CrosswordPuzzle;
import use_case.crossword.CrosswordPuzzleDataAccessInterface;

public class StartCrosswordInteractor implements StartCrosswordInputBoundary {

    private final CrosswordPuzzleDataAccessInterface puzzleDataAccess;
    private final StartCrosswordOutputBoundary outputBoundary;

    public StartCrosswordInteractor(CrosswordPuzzleDataAccessInterface puzzleDataAccess,
                                    StartCrosswordOutputBoundary outputBoundary) {
        this.puzzleDataAccess = puzzleDataAccess;
        this.outputBoundary = outputBoundary;
    }

    @Override
    public void startCrossword() {
        CrosswordPuzzle puzzle = puzzleDataAccess.loadPuzzle();
        String id = puzzle.getId();
        String imagePath = puzzle.getImagePath();
        int numSolutions = puzzle.numSolutions();

        outputBoundary.presentCrossword(id, imagePath, numSolutions);
    }
}