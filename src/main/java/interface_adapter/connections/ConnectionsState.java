package interface_adapter.connections;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap; // To preserve insertion order

/**
 * A "Plain Old Java Object" (POJO) that holds the complete state
 * for the Connections game view. The ViewModel will hold one instance of this.
 */
public class ConnectionsState {

    private String gameTitle = "Connections";
    private int mistakesRemaining = 4;
    private List<String> currentWords = new ArrayList<>();

    // Using LinkedHashMap to store solved categories in the order they were found
    private Map<String, List<String>> solvedCategories = new LinkedHashMap<>();

    private String errorMessage = null;
    private boolean isWin = false;
    private boolean isGameOver = false;

    // Copy constructor
    public ConnectionsState(ConnectionsState copy) {
        this.gameTitle = copy.gameTitle;
        this.mistakesRemaining = copy.mistakesRemaining;
        this.currentWords = new ArrayList<>(copy.currentWords);
        this.solvedCategories = new LinkedHashMap<>(copy.solvedCategories);
        this.errorMessage = copy.errorMessage;
        this.isWin = copy.isWin;
        this.isGameOver = copy.isGameOver;
    }

    // Default constructor
    public ConnectionsState() {}

    // --- Getters ---
    public String getGameTitle() { return gameTitle; }
    public int getMistakesRemaining() { return mistakesRemaining; }
    public List<String> getCurrentWords() { return currentWords; }
    public Map<String, List<String>> getSolvedCategories() { return solvedCategories; }
    public String getErrorMessage() { return errorMessage; }
    public boolean isWin() { return isWin; }
    public boolean isGameOver() { return isGameOver; }

    // --- Setters (for the Presenter to use) ---
    public void setGameTitle(String gameTitle) { this.gameTitle = gameTitle; }
    public void setMistakesRemaining(int mistakes) { this.mistakesRemaining = mistakes; }
    public void setCurrentWords(List<String> words) { this.currentWords = words; }
    public void addSolvedCategory(String category, List<String> words) {
        this.solvedCategories.put(category, words);
    }
    public void setErrorMessage(String error) { this.errorMessage = error; }
    public void setWin(boolean isWin) { this.isWin = isWin; }
    public void setGameOver(boolean isGameOver) { this.isGameOver = isGameOver; }
}