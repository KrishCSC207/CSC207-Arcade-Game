package usecase.game;

import entity.Game;
import java.io.IOException;

/**
 * Interface defining the data access operations for the game.
 * This acts as a "port" in Clean Architecture, to be implemented
 * by a "adapter" in the data_access layer.
 */
public interface GameDataAccessInterface {

    /**
     * Fetches a specific game by its unique game code.
     *
     * @param gameCode The unique string code (e.g., "XBZQ") of the game to fetch.
     * @return A Game entity object populated with all categories and words.
     * @throws IOException If a network or parsing error occurs.
     * @throws InterruptedException If the network request is interrupted.
     */
    Game getGameByCode(String gameCode) throws IOException, InterruptedException;

    // Note: You could later add other methods like:
    // List<String> getAllGameCodes();
    // void saveGameState(User user, GameState state);
}