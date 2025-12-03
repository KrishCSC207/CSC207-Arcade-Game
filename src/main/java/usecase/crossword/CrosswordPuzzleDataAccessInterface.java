package usecase.crossword;

import entity.CrosswordPuzzle;

import java.util.ArrayList;

public interface CrosswordPuzzleDataAccessInterface {
    CrosswordPuzzle loadPuzzle();
    ArrayList<String> getCurrentPuzzleSolutions();
}