package usecase.game;

import entity.Category;
import entity.Game;
import entity.GameState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

public class GameSubmitGuessInteractorTest {

    // In-memory repository for tests
    static class InMemoryGameStateRepository implements GameStateRepository {
        private Game activeGame;
        private GameState activeState;

        @Override
        public void save(Game game, GameState state) {
            this.activeGame = game;
            this.activeState = state;
        }

        @Override
        public Game getActiveGame() {
            return activeGame;
        }

        @Override
        public GameState getActiveGameState() {
            return activeState;
        }
    }

    // Presenter spy for guesses
    static class TestGuessPresenter implements GameSubmitGuessOutputBoundary {
        String correctCategoryName;
        List<String> correctCategoryWords;
        List<String> remainingWords;
        boolean alreadyFound = false;
        Integer mistakesRemaining = null;
        AtomicBoolean winCalled = new AtomicBoolean(false);
        AtomicBoolean gameOverCalled = new AtomicBoolean(false);

        @Override
        public void presentCorrectGuess(String categoryName, List<String> categoryWords, List<String> remainingWords) {
            this.correctCategoryName = categoryName;
            this.correctCategoryWords = categoryWords;
            this.remainingWords = remainingWords;
        }

        @Override
        public void presentIncorrectGuess(int mistakesRemaining) {
            this.mistakesRemaining = mistakesRemaining;
        }

        @Override
        public void presentAlreadyFound() {
            this.alreadyFound = true;
        }

        @Override
        public void presentWin() {
            this.winCalled.set(true);
        }

        @Override
        public void presentGameOver(String message) {
            this.gameOverCalled.set(true);
        }
    }

    // Helper to build a Game for tests
    static class TestGameFactory {
        static Game createGame(String title, Map<String, List<String>> categoriesMap) {
            List<Category> categories = new ArrayList<>();
            List<String> allWords = new ArrayList<>();
            for (Map.Entry<String, List<String>> e : categoriesMap.entrySet()) {
                categories.add(new Category(e.getKey(), e.getValue()));
                allWords.addAll(e.getValue());
            }
            // deterministic shuffle for tests
            Collections.shuffle(allWords, new Random(0));
            // entity.Game constructor expects (String code, String title, List<Category>, List<String>)
            return new Game("", title, categories, allWords);
        }
    }

    InMemoryGameStateRepository repo;

    @BeforeEach
    void setup() {
        repo = new InMemoryGameStateRepository();
    }

    @Test
    void correctNewGuess_marksFoundAndReturnsRemaining() {
        Map<String, List<String>> cats = new LinkedHashMap<>();
        cats.put("Animals", Arrays.asList("cat","dog","cow","pig"));
        cats.put("Fruits", Arrays.asList("apple","pear","banana","kiwi"));

        Game game = TestGameFactory.createGame("G", cats);
        GameState state = new GameState(4);
        repo.save(game, state);

        TestGuessPresenter presenter = new TestGuessPresenter();
        GameSubmitGuessInteractor interactor = new GameSubmitGuessInteractor(repo, presenter);

        List<String> selection = Arrays.asList("cat","dog","cow","pig");
        interactor.execute(selection);

        assertEquals("Animals", presenter.correctCategoryName);
        assertNotNull(presenter.remainingWords);
        for (String w : cats.get("Animals")) {
            assertFalse(presenter.remainingWords.contains(w));
        }
        assertFalse(presenter.winCalled.get(), "Should not win yet");
    }

    @Test
    void alreadyFound_presentAlreadyFound() {
        Map<String, List<String>> cats = new LinkedHashMap<>();
        cats.put("Animals", Arrays.asList("cat","dog","cow","pig"));
        cats.put("Fruits", Arrays.asList("apple","pear","banana","kiwi"));

        Game game = TestGameFactory.createGame("G", cats);
        GameState state = new GameState(4);
        state.addFoundCategory(new Category("Animals", cats.get("Animals")));
        repo.save(game, state);

        TestGuessPresenter presenter = new TestGuessPresenter();
        GameSubmitGuessInteractor interactor = new GameSubmitGuessInteractor(repo, presenter);

        List<String> selection = Arrays.asList("cat","dog","cow","pig");
        interactor.execute(selection);

        assertTrue(presenter.alreadyFound, "Presenter should be told category is already found");
    }

    @Test
    void incorrectGuess_decrementsMistakes_andGameOverWhenExhausted() {
        Map<String, List<String>> cats = new LinkedHashMap<>();
        cats.put("A", Arrays.asList("a1","a2","a3","a4"));
        cats.put("B", Arrays.asList("b1","b2","b3","b4"));

        Game game = TestGameFactory.createGame("G", cats);
        GameState state = new GameState(1); // only one mistake allowed
        repo.save(game, state);

        TestGuessPresenter presenter = new TestGuessPresenter();
        GameSubmitGuessInteractor interactor = new GameSubmitGuessInteractor(repo, presenter);

        List<String> selection = Arrays.asList("a1","b1","a2","b2");
        interactor.execute(selection);

        assertNotNull(presenter.mistakesRemaining);
        assertEquals(0, presenter.mistakesRemaining.intValue(), "Mistakes should be decremented to 0");
        assertTrue(presenter.gameOverCalled.get(), "Game over should be presented when mistakes exhausted");
    }
}
