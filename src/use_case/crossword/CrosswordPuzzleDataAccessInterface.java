package use_case.crossword;

import entity.crossword.CrosswordPuzzle;

import java.util.ArrayList;

public interface CrosswordPuzzleDataAccessInterface {
    CrosswordPuzzle loadPuzzle();
    ArrayList<String> getCurrentPuzzleSolutions();
}