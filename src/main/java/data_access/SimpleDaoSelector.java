package data_access;

import entity.CrosswordPuzzle;
import use_case.crossword.CrosswordPuzzleDataAccessInterface;

import java.util.ArrayList;

public class SimpleDaoSelector implements CrosswordPuzzleDataAccessInterface {
    private CrosswordPuzzleDataAccessInterface current =
            new DataCrosswordPuzzleDataAccess("EASY");

    public void select(String difficulty) {
        String key = (difficulty == null || difficulty.isBlank())
                ? "EASY"
                : difficulty.trim().toUpperCase();
        current = new DataCrosswordPuzzleDataAccess(key);
    }

    @Override
    public CrosswordPuzzle loadPuzzle() {
        return current.loadPuzzle();
    }

    @Override
    public ArrayList<String> getCurrentPuzzleSolutions() {
        return current.getCurrentPuzzleSolutions();
    }
}
