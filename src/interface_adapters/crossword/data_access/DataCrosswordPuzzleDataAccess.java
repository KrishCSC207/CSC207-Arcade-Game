package interface_adapters.crossword.data_access;

import entity.crossword.CrosswordPuzzle;
import use_case.crossword.CrosswordPuzzleDataAccessInterface;

import java.util.*;

public class DataCrosswordPuzzleDataAccess implements CrosswordPuzzleDataAccessInterface {

    private final String difficulty;     // "EASY" | "MEDIUM" | "HARD"
    private CrosswordPuzzle current;

    public DataCrosswordPuzzleDataAccess(String difficulty) {
        this.difficulty = difficulty;
    }

    @Override
    public CrosswordPuzzle loadPuzzle() {
        String key = (difficulty == null ? "EASY" : difficulty).toUpperCase(Locale.ROOT);
        List<CrosswordPuzzle> list = CrosswordData.DB.get(key);
        if (list == null || list.isEmpty()) {
            // fallback: EASY, then any
            list = CrosswordData.DB.getOrDefault("EASY",
                    CrosswordData.DB.values().stream().findFirst().orElse(List.of()));
        }
        current = list.isEmpty() ? new CrosswordPuzzle("", "", List.of()) : list.get(0);
        return current;
    }

    @Override
    public ArrayList<String> getCurrentPuzzleSolutions() {
        return current == null ? new ArrayList<>() : new ArrayList<>(current.getSolutions());
    }
}


