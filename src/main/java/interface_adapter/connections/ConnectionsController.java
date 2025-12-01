package interface_adapter.connections;

import use_case.game.GameInteractor; // Assuming 'use_case' is the package
import use_case.game.GameSubmitGuessInteractor;
import java.util.List;

/**
 * Controller for the Connections game.
 * The View calls methods on this class, which in turn
 * packages the data and calls the appropriate use case interactor.
 */
public class ConnectionsController {

    final GameInteractor loadGameInteractor;
    final GameSubmitGuessInteractor submitGuessInteractor;

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
     * Called by the View when the "Submit" button is clicked.
     * @param selectedWords The 4 words the user selected.
     */
    public void executeSubmit(List<String> selectedWords) {
        // Pass the selected words to the submit guess interactor
        submitGuessInteractor.execute(selectedWords);
    }
}