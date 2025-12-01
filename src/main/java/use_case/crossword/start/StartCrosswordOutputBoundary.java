package use_case.crossword.start;

public interface StartCrosswordOutputBoundary {


    /**
     * Present the loaded crossword to the user.
     *
     * @param puzzleId      the ID of the loaded puzzle
     * @param imagePath     path to the image to display
     * @param numSolutions  how many answers/textfields the UI should show
     */

    void presentCrossword(String puzzleid, String imagepath, int numSolutions);
}