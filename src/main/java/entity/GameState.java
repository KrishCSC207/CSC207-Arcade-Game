package entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the mutable state of a single, active game session.
 * It tracks the player's progress.
 */
public class GameState {

    private int mistakesRemaining;
    private final List<Category> foundCategories;

    /**
     * Constructs a new GameState for a session.
     *
     * @param maxMistakes The number of attempts the player starts with.
     */
    public GameState(int maxMistakes) {
        this.mistakesRemaining = maxMistakes;
        this.foundCategories = new ArrayList<>();
    }

    // Getters
    public int getMistakesRemaining() {
        return mistakesRemaining;
    }

    public List<Category> getFoundCategories() {
        return foundCategories;
    }

    // Mutators (to be called by the use_case layer)

    /**
     * Adds a newly found category to the list of found categories.
     *
     * @param category The Category that was correctly guessed.
     */
    public void addFoundCategory(Category category) {
        this.foundCategories.add(category);
    }

    /**
     * Decrements the number of mistakes remaining.
     */
    public void decrementMistakes() {
        if (this.mistakesRemaining > 0) {
            this.mistakesRemaining--;
        }
    }

    /**
     * Checks if the game is over (player has no mistakes left).
     *
     * @return true if no mistakes are left, false otherwise.
     */
    public boolean isGameOver() {
        return this.mistakesRemaining == 0;
    }
}