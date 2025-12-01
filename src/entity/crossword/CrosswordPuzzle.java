package entity.crossword;

import java.util.List;

public class CrosswordPuzzle {

    private final String id; //  Way to identify each crossword

    private final String imagePath;

    private final List<String> solutions;

    public CrosswordPuzzle(String id, String imagePath, List<String> solutions) {
        this.id = id;
        this.solutions = solutions;
        this.imagePath = imagePath;
    }

    public String getId() {
        return id;
    }

    public String getImagePath() {
        return imagePath;
    }

    public List<String> getSolutions() {
        return solutions;
    }

    public String getSolutionAt(int index) {
        return solutions.get(index);
    }

    public int numSolutions() {
        return solutions.size();
    }
}