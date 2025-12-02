package entity;

import java.util.List;

/**
 * Represents a single category in the Connections game.
 * This is an immutable data object (entity).
 */
public class Category {

    private final String categoryName;
    private final List<String> words;

    /**
     * Constructs a new Category.
     *
     * @param categoryName The name of the category (e.g., "Evaluate to True").
     * @param words        The list of words belonging to this category.
     */
    public Category(String categoryName, List<String> words) {
        this.categoryName = categoryName;
        this.words = List.copyOf(words); // Make the list unmodifiable
    }

    public String getCategoryName() {
        return categoryName;
    }

    public List<String> getWords() {
        return words;
    }
}