package use_case.game;

import entity.Game;
import entity.GameState;
import java.io.IOException;

/**
 * The interactor for the "Load Game" use case.
 * Implements the business logic for starting a new game.
 */
public class GameInteractor {

    final GameDataAccessInterface gameDataAccess;
    final GameStateRepository gameStateRepository;
    final GameOutputBoundary gamePresenter;
    private static final int MAX_MISTAKES = 4;

    public GameInteractor(GameDataAccessInterface gameDataAccess,
                              GameStateRepository gameStateRepository,
                              GameOutputBoundary gamePresenter) {
        this.gameDataAccess = gameDataAccess;
        this.gameStateRepository = gameStateRepository;
        this.gamePresenter = gamePresenter;
    }

    /**
     * Executes the "Load Game" logic.
     * 1. Fetches the Game from the data access layer.
     * 2. Creates a new GameState.
     * 3. Saves both to the active repository.
     * 4. Tells the presenter to display the game board.
     *
     * @param gameCode The unique code for the game to load (e.g., "XBZQ").
     */
    public void execute(String gameCode) {
        try {
            // 1. Fetch Game from API
            Game game = gameDataAccess.getGameByCode(gameCode);

            // 2. Create a new GameState
            GameState gameState = new GameState(MAX_MISTAKES);

            // 3. Save both to the active repository
            gameStateRepository.save(game, gameState);

            // 4. Tell presenter to display the game
            gamePresenter.presentGame(game.getTitle(), game.getAllShuffledWords());

        } catch (IOException | InterruptedException e) {
            // Handle API or parsing errors
            gamePresenter.presentError("Failed to load game: " + e.getMessage());
        }
    }
}