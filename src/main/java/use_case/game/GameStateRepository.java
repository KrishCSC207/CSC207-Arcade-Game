package use_case.game;

import entity.Game;
import entity.GameState;

/**
 * An interface for a repository that holds the *active* game session state.
 * This allows the LoadGame and SubmitGuess interactors to share the same
 * Game and GameState objects.
 * * An implementation of this (in data_access) could store this in memory,
 * in a file, or in a database.
 */
public interface GameStateRepository {

    /**
     * Saves the active Game and GameState.
     * @param game The Game entity (the "answer key").
     * @param gameState The current state of the user's session.
     */
    void save(Game game, GameState gameState);

    /**
     * @return The currently active Game.
     */
    Game getActiveGame();

    /**
     * @return The currently active GameState.
     */
    GameState getActiveGameState();
}