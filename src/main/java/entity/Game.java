package entity;

import java.util.List;

/**
 * Represents the static definition of a Connections game.
 * It holds the "answer key" (all categories) and the full,
 * shuffled list of words for the board.
 */
public class Game {

    private final String gameId;
    private final String title;
    private final List<Category> categories;
    private final List<String> allShuffledWords;

    /**
     * Constructs a new Game.
     *
     * @param gameId             A unique identifier (e.g., "XBZQ").
     * @param title              The display title (e.g., "CSC108 Week 4 Connections").
     * @param categories         The list of all Category entities for this game.
     * @param allShuffledWords   A pre-shuffled list of all words from all categories.
     */
    public Game(String gameId, String title, List<Category> categories, List<String> allShuffledWords) {
        this.gameId = gameId;
        this.title = title;
        this.categories = List.copyOf(categories);
        this.allShuffledWords = List.copyOf(allShuffledWords);
    }

    // Getters
    public String getGameId() {
        return gameId;
    }

    public String getTitle() {
        return title;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public List<String> getAllShuffledWords() {
        return allShuffledWords;
    }
}