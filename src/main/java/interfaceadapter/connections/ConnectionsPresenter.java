package interfaceadapter.connections;

import interfaceadapter.ViewManagerModel; // Your existing ViewManager
import usecase.game.GameOutputBoundary;
import usecase.game.GameSubmitGuessOutputBoundary;
import java.util.List;

/**
 * Presenter for the Connections game.
 * Implements the output boundaries from the use case layer.
 * Its job is to update the ConnectionsViewModel and tell the
 * ViewManagerModel to change views if necessary.
 */
public class ConnectionsPresenter implements GameOutputBoundary, GameSubmitGuessOutputBoundary {

    private final ConnectionsViewModel viewModel;
    private final ViewManagerModel viewManagerModel;

    public ConnectionsPresenter(ConnectionsViewModel viewModel,
                                ViewManagerModel viewManagerModel) {
        this.viewModel = viewModel;
        this.viewManagerModel = viewManagerModel;
    }

    // --- LoadGameOutputBoundary Methods ---

    @Override
    public void presentGame(String title, List<String> allShuffledWords) {
        ConnectionsState state = viewModel.getState();
        state.setGameTitle(title);
        state.setCurrentWords(allShuffledWords);

        viewModel.setState(state);
        viewModel.fireStateChanged();

        // Ensure the game view is active
        viewManagerModel.setState(viewModel.getViewName());
        viewManagerModel.firePropertyChange();
    }

    @Override
    public void presentError(String message) {
        // This is for a fatal error (e.g., API fails)
        ConnectionsState state = viewModel.getState();
        state.setErrorMessage(message); // You might show this on a different view
        viewModel.setState(state);

        // Example: Redirect to a "Menu" or "Error" view
        // viewManagerModel.setActiveView("main_menu");
        // viewManagerModel.firePropertyChanged();

        // For now, we'll just fire an error event for the current view
        viewModel.fireError(message);
    }

    // --- SubmitGuessOutputBoundary Methods ---

    @Override
    public void presentCorrectGuess(String categoryName, List<String> words, List<String> remainingWords) {
        ConnectionsState state = viewModel.getState();
        state.addSolvedCategory(categoryName, words);
        state.setCurrentWords(remainingWords);

        viewModel.setState(state);
        viewModel.fireStateChanged();
    }

    @Override
    public void presentIncorrectGuess(int mistakesRemaining) {
        ConnectionsState state = viewModel.getState();
        state.setMistakesRemaining(mistakesRemaining);

        viewModel.setState(state);
        viewModel.fireStateChanged();
    }

    @Override
    public void presentAlreadyFound() {
        viewModel.fireError("You already found that group!");
    }

    @Override
    public void presentWin() {
        ConnectionsState state = viewModel.getState();
        state.setWin(true);

        viewModel.setState(state);
        viewModel.fireStateChanged();

        // Optional:
        // viewManagerModel.setActiveView("win_screen");
        // viewManagerModel.firePropertyChanged();
    }

    @Override
    public void presentGameOver(String message) {
        ConnectionsState state = viewModel.getState();
        state.setGameOver(true);
        state.setErrorMessage(message); // "You're out of mistakes!"

        viewModel.setState(state);
        viewModel.fireStateChanged();

        // Optional:
        // viewManagerModel.setActiveView("game_over_screen");
        // viewManagerModel.firePropertyChanged();
    }
}