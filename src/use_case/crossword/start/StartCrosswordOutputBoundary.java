package use_case.crossword.start;

public interface StartCrosswordOutputBoundary {


    // puzzleId is the ID of hte loaded pouzzle
    //imagePath is hte path to the image to display
    // numSolutions is the number of words in the crossword being displaued

    void presentCrossword(String puzzleid, String imagepath, int numSolutions);
}