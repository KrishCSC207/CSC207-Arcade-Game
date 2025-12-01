package use_case.game;

import java.util.List;

/**
 * Output boundary interface for the Load Game use case.
 * The Presenter will implement this.
 */
public interface GameOutputBoundary {

    /**
     * Prepares the view with the initial game data (the shuffled words).
     * @param title The game's title.
     * @param allShuffledWords The 16 words in a shuffled list.
     */
    void presentGame(String title, List<String> allShuffledWords);

    /**
     * Prepares the view to show an error message.
     * @param message The error message to display.
     */
    void presentError(String message);
}