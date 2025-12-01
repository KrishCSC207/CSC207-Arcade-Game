package data_access;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import entity.Category;
import entity.Game;
import use_case.game.GameDataAccessInterface;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Implements the GameDataAccessInterface by fetching data from the
 * UofT CS teaching API.
 */
public class ApiGameDataAccess implements GameDataAccessInterface {

    private static final String API_BASE_URL = "https://vm006.teach.cs.toronto.edu/backend/api/connectionsgames/";

    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Fetches a game by its code.
     * This implementation handles the API's pagination by searching
     * page by page until the game is found or all pages are exhausted.
     */
    @Override
    public Game getGameByCode(String gameCode) throws IOException, InterruptedException {
        String currentUrl = API_BASE_URL + "?format=json";

        while (currentUrl != null) {
            // 1. Make the API Request
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(currentUrl))
                    .header("Accept", "application/json")
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new IOException("API request failed with status code: " + response.statusCode());
            }

            // 2. Parse the List Response
            ApiListResponse pageResponse = objectMapper.readValue(response.body(), ApiListResponse.class);

            // 3. Search for the game in the current page's results
            for (ApiGameResponse apiGame : pageResponse.results) {
                if (apiGame.gameCode.equals(gameCode)) {
                    // 4. Found! Convert the API DTO to our Game Entity
                    return convertToGameEntity(apiGame);
                }
            }

            // 5. Not found on this page, go to the next page
            currentUrl = pageResponse.next;
        }

        // 6. Searched all pages and not found
        throw new IOException("Game with code '" + gameCode + "' not found.");
    }

    /**
     * Helper method to convert the API's JSON-based game object
     * into our application's Game entity.
     */
    private Game convertToGameEntity(ApiGameResponse apiGame) {
        List<Category> categoryEntities = new ArrayList<>();
        List<String> allWords = new ArrayList<>();

        for (ApiCategory apiCategory : apiGame.gameCategories) {
            // Create the immutable Category entity
            Category category = new Category(apiCategory.categoryName, apiCategory.words);
            categoryEntities.add(category);

            // Add all words to a master list
            allWords.addAll(apiCategory.words);
        }

        // Shuffle the words for the game board
        Collections.shuffle(allWords);

        // Create and return the final Game entity
        return new Game(apiGame.gameCode, apiGame.title, categoryEntities, allWords);
    }

    // --- Internal DTO Classes for JSON Parsing ---
    // These classes are private because nothing outside of this
    // data access object needs to know about the API's specific JSON structure.

    /**
     * DTO for the paginated list response.
     * (e.g., /api/connectionsgames/)
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class ApiListResponse {
        @JsonProperty("next")
        public String next; // URL for the next page, or null

        @JsonProperty("results")
        public List<ApiGameResponse> results;
    }

    /**
     * DTO for a single game object *within* the API response.
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class ApiGameResponse {
        @JsonProperty("id")
        public int id;

        @JsonProperty("game_code")
        public String gameCode;

        @JsonProperty("title")
        public String title;

        @JsonProperty("game")
        public List<ApiCategory> gameCategories;
    }

    /**
     * DTO for a single category object *within* the game object.
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class ApiCategory {
        @JsonProperty("category")
        public String categoryName;

        @JsonProperty("words")
        public List<String> words;
    }
}