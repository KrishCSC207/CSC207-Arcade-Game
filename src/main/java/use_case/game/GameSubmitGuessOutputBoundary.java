package use_case.game;

import java.util.List;

/**
 * Output boundary interface for the Submit Guess use case.
 * The Presenter will implement this.
 */
public interface GameSubmitGuessOutputBoundary {

    /**
     * Prepares the view to show a correct guess.
     * @param categoryName The name of the category (e.g., "Evaluate to True").
     * @param words The list of words in that category.
     * @param remainingWords The list of words still on the board.
     */
    void presentCorrectGuess(String categoryName, List<String> words, List<String> remainingWords);

    /**
     * Prepares the view to show an incorrect guess.
     * @param mistakesRemaining The new number of mistakes left.
     */
    void presentIncorrectGuess(int mistakesRemaining);

    /**
     * Prepares the view to show a message that the guess was already found.
     */
    void presentAlreadyFound();

    /**
     * Prepares the view to show the win state.
     */
    void presentWin();

    /**
     * Prepares the view to show the game over (loss) state.
     * @param message A final message (e.g., "You're out of mistakes!").
     */
    void presentGameOver(String message);
}