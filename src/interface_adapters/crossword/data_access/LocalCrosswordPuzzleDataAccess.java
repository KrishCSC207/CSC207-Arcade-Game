package interface_adapters.crossword.data_access;

import entity.crossword.CrosswordPuzzle;
import use_case.crossword.CrosswordPuzzleDataAccessInterface;
import java.util.ArrayList;
import java.util.List;

public class LocalCrosswordPuzzleDataAccess implements CrosswordPuzzleDataAccessInterface {

    private CrosswordPuzzle currentPuzzle;

    @Override
    public CrosswordPuzzle loadPuzzle() {
        String id = "crossword1";
        String imagePath = "images/crosswords/Crossword1.png";

        List<String> solutions = new ArrayList<>();
        solutions.add("entity");
        solutions.add("object");
        solutions.add("superclass");
        solutions.add("integer");
        solutions.add("reference");
        solutions.add("interface");
        solutions.add("string");

        this.currentPuzzle = new CrosswordPuzzle(id, imagePath, solutions);
        return this.currentPuzzle;
    }

    @Override
    public ArrayList<String> getCurrentPuzzleSolutions() {
        if (this.currentPuzzle != null) {
            return new ArrayList<>(this.currentPuzzle.getSolutions());
        }
        return new ArrayList<>();
    }
}