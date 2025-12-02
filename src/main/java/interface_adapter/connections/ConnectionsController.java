package interface_adapter.connections;

import use_case.game.GameInteractor; // Assuming 'use_case' is the package
import use_case.game.GameSubmitGuessInteractor;
import java.util.List;
import java.util.Random;
import java.util.Arrays;

/**
 * Controller for the Connections game.
 * The View calls methods on this class, which in turn
 * packages the data and calls the appropriate use case interactor.
 */
public class ConnectionsController {

    final GameInteractor loadGameInteractor;
    final GameSubmitGuessInteractor submitGuessInteractor;

    // Default fallback game codes (used when no list is supplied).
    // Keep small and safe; you can replace these with your real codes.
    private static final List<String> DEFAULT_GAME_CODES =
            Arrays.asList("XBZQ", "QSXV", "WKPG", "XMYF");

    private final Random rng = new Random();

    public ConnectionsController(GameInteractor loadGameInteractor,
                                 GameSubmitGuessInteractor submitGuessInteractor) {
        this.loadGameInteractor = loadGameInteractor;
        this.submitGuessInteractor = submitGuessInteractor;
    }

    /**
     * Called by the View when the game screen is first loaded.
     * @param gameCode The ID of the game to load (e.g., "XBZQ").
     */
    public void executeLoad(String gameCode) {
        // Here you would pass any input data.
        // For loading, we just need the game code.
        loadGameInteractor.execute(gameCode);
    }

    /**
     * Randomly selects one code from the provided list and loads it.
     * If availableCodes is null or empty, falls back to DEFAULT_GAME_CODES.
     *
     * @param availableCodes list of possible game codes to choose from (may be null)
     */
    public void executeLoadRandom(List<String> availableCodes) {
        List<String> pool = (availableCodes == null || availableCodes.isEmpty())
                ? DEFAULT_GAME_CODES
                : availableCodes;

        int idx = rng.nextInt(pool.size());
        String chosenCode = pool.get(idx);
        executeLoad(chosenCode);
    }

    /**
     * Randomly selects one code from the default pool and loads it.
     */
    public void executeLoadRandom() {
        executeLoadRandom(null);
    }

    /**
     * Called by the View when the "Submit" button is clicked.
     * @param selectedWords The 4 words the user selected.
     */
    public void executeSubmit(List<String> selectedWords) {
        // Pass the selected words to the submit guess interactor
        submitGuessInteractor.execute(selectedWords);
    }
}