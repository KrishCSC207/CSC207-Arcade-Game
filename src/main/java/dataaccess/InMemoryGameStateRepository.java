package dataaccess;

import entity.Game;
import entity.GameState;
import usecase.game.GameStateRepository;

/**
 * Simple in-memory implementation of GameStateRepository to hold the
 * currently active game and its state for the Connections game.
 */
public class InMemoryGameStateRepository implements GameStateRepository {

    private Game activeGame;
    private GameState activeGameState;

    @Override
    public void save(Game game, GameState gameState) {
        this.activeGame = game;
        this.activeGameState = gameState;
    }

    @Override
    public Game getActiveGame() {
        return activeGame;
    }

    @Override
    public GameState getActiveGameState() {
        return activeGameState;
    }
}
