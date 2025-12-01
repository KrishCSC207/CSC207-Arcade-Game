package use_case.game;

import entity.Category;
import entity.Game;
import entity.GameState;

import java.util.List;
import java.util.stream.Collectors;

/**
 * The interactor for the "Submit Guess" use case.
 * Implements the core game logic.
 */
public class GameSubmitGuessInteractor {

    final GameStateRepository gameStateRepository;
    final GameSubmitGuessOutputBoundary guessPresenter;

    public GameSubmitGuessInteractor(GameStateRepository gameStateRepository,
                                 GameSubmitGuessOutputBoundary guessPresenter) {
        this.gameStateRepository = gameStateRepository;
        this.guessPresenter = guessPresenter;
    }

    /**
     * Executes the "Submit Guess" logic.
     * 1. Gets the active Game and GameState.
     * 2. Checks if the 4 selected words belong to the same category.
     * 3. Updates the GameState.
     * 4. Calls the appropriate presenter method (correct, incorrect, win, loss).
     *
     * @param selectedWords The list of 4 words the user selected.
     */
    public void execute(List<String> selectedWords) {
        Game game = gameStateRepository.getActiveGame();
        GameState gameState = gameStateRepository.getActiveGameState();

        if (game == null || gameState == null || gameState.isGameOver()) {
            return; // Game not loaded or is already over
        }

        Category matchedCategory = findMatchingCategory(game, selectedWords);

        if (matchedCategory != null) {
            // --- CORRECT GUESS ---

            // Check if this category was already found
            if (gameState.getFoundCategories().contains(matchedCategory)) {
                guessPresenter.presentAlreadyFound();
            } else {
                gameState.addFoundCategory(matchedCategory);

                // Get the words still on the board
                List<String> remainingWords = getRemainingWords(game, gameState);

                guessPresenter.presentCorrectGuess(matchedCategory.getCategoryName(),
                        matchedCategory.getWords(), remainingWords);

                // Check for win condition
                if (gameState.getFoundCategories().size() == game.getCategories().size()) {
                    guessPresenter.presentWin();
                }
            }
        } else {
            // --- INCORRECT GUESS ---
            gameState.decrementMistakes();
            guessPresenter.presentIncorrectGuess(gameState.getMistakesRemaining());

            // Check for game over condition
            if (gameState.isGameOver()) {
                guessPresenter.presentGameOver("You're out of mistakes!");
            }
        }
    }

    /**
     * Helper method to check if the 4 words belong to a single category.
     * @return The matching Category if all words belong to one, or null otherwise.
     */
    private Category findMatchingCategory(Game game, List<String> selectedWords) {
        // Find the category of the first word
        Category firstCategory = null;
        for (Category category : game.getCategories()) {
            if (category.getWords().contains(selectedWords.get(0))) {
                firstCategory = category;
                break;
            }
        }

        if (firstCategory == null) {
            return null; // Should not happen
        }

        // Check if all other words are in that same category
        for (int i = 1; i < selectedWords.size(); i++) {
            if (!firstCategory.getWords().contains(selectedWords.get(i))) {
                return null; // Words do not match
            }
        }

        return firstCategory; // All words match!
    }

    /**
     * Helper method to get all words that are not in a "found" category.
     */
    private List<String> getRemainingWords(Game game, GameState gameState) {
        List<String> foundWords = gameState.getFoundCategories().stream()
                .flatMap(category -> category.getWords().stream())
                .collect(Collectors.toList());

        return game.getAllShuffledWords().stream()
                .filter(word -> !foundWords.contains(word))
                .collect(Collectors.toList());
    }
}