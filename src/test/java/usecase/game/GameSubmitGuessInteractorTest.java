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
        String gameOverMessage = null; // record message for assertions

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
            this.gameOverMessage = message;
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
        assertEquals("You're out of mistakes!", presenter.gameOverMessage, "Game over message should match");
    }

    @Test
    void incorrectMismatch_decrementsOnly() {
        Map<String, List<String>> cats = new LinkedHashMap<>();
        cats.put("A", Arrays.asList("a1","a2","a3","a4"));
        cats.put("B", Arrays.asList("b1","b2","b3","b4"));

        Game game = TestGameFactory.createGame("G", cats);
        GameState state = new GameState(2); // allow >1 mistake so no game over
        repo.save(game, state);

        TestGuessPresenter presenter = new TestGuessPresenter();
        GameSubmitGuessInteractor interactor = new GameSubmitGuessInteractor(repo, presenter);

        // first word in A, second in B -> should trigger mismatch after firstCategory set
        List<String> selection = Arrays.asList("a1","b1","a2","a3");
        interactor.execute(selection);

        // should decrement mistakes to 1 and NOT call game over
        assertNotNull(presenter.mistakesRemaining);
        assertEquals(1, presenter.mistakesRemaining.intValue());
        assertFalse(presenter.gameOverCalled.get());
        // no correct/alreadyFound/win should be reported
        assertNull(presenter.correctCategoryName);
        assertFalse(presenter.alreadyFound);
        assertFalse(presenter.winCalled.get());
    }

    // New tests for early-return branches and null/empty selections

    @Test
    void noActiveGame_doesNothing() {
        // repo has no active game/state
        TestGuessPresenter presenter = new TestGuessPresenter();
        GameSubmitGuessInteractor interactor = new GameSubmitGuessInteractor(repo, presenter);

        List<String> selection = Arrays.asList("x1","x2","x3","x4");
        interactor.execute(selection);

        // nothing should be presented
        assertNull(presenter.correctCategoryName);
        assertNull(presenter.mistakesRemaining);
        assertFalse(presenter.alreadyFound);
        assertFalse(presenter.winCalled.get());
        assertFalse(presenter.gameOverCalled.get());
    }

    @Test
    void noActiveState_doesNothing() {
        Map<String, List<String>> cats = new LinkedHashMap<>();
        cats.put("C", Arrays.asList("c1","c2","c3","c4"));
        Game game = TestGameFactory.createGame("G", cats);
        // only set game, not state
        repo.save(game, null);

        TestGuessPresenter presenter = new TestGuessPresenter();
        GameSubmitGuessInteractor interactor = new GameSubmitGuessInteractor(repo, presenter);

        interactor.execute(Arrays.asList("c1","c2","c3","c4"));

        assertNull(presenter.correctCategoryName);
        assertNull(presenter.mistakesRemaining);
        assertFalse(presenter.winCalled.get());
    }

    @Test
    void gameStateAlreadyOver_doesNothing() {
        Map<String, List<String>> cats = new LinkedHashMap<>();
        cats.put("C", Arrays.asList("c1","c2","c3","c4"));
        Game game = TestGameFactory.createGame("G", cats);
        // GameState with 0 allowed mistakes => already game over
        GameState state = new GameState(0);
        repo.save(game, state);

        TestGuessPresenter presenter = new TestGuessPresenter();
        GameSubmitGuessInteractor interactor = new GameSubmitGuessInteractor(repo, presenter);

        interactor.execute(Arrays.asList("c1","c2","c3","c4"));

        assertNull(presenter.correctCategoryName);
        assertNull(presenter.mistakesRemaining);
        assertFalse(presenter.winCalled.get());
    }

    @Test
    void emptySelection_doesNothing() {
        Map<String, List<String>> cats = new LinkedHashMap<>();
        cats.put("A", Arrays.asList("a1","a2","a3","a4"));
        Game game = TestGameFactory.createGame("G", cats);
        GameState state = new GameState(2);
        repo.save(game, state);

        TestGuessPresenter presenter = new TestGuessPresenter();
        GameSubmitGuessInteractor interactor = new GameSubmitGuessInteractor(repo, presenter);

        interactor.execute(Collections.emptyList());

        assertNull(presenter.correctCategoryName);
        assertNull(presenter.mistakesRemaining);
        assertFalse(presenter.alreadyFound);
        assertFalse(presenter.winCalled.get());
        assertFalse(presenter.gameOverCalled.get());
    }

    @Test
    void nullSelection_doesNothing() {
        Map<String, List<String>> cats = new LinkedHashMap<>();
        cats.put("B", Arrays.asList("b1","b2","b3","b4"));
        Game game = TestGameFactory.createGame("G", cats);
        GameState state = new GameState(2);
        repo.save(game, state);

        TestGuessPresenter presenter = new TestGuessPresenter();
        GameSubmitGuessInteractor interactor = new GameSubmitGuessInteractor(repo, presenter);

        interactor.execute(null);

        assertNull(presenter.correctCategoryName);
        assertNull(presenter.mistakesRemaining);
        assertFalse(presenter.alreadyFound);
        assertFalse(presenter.winCalled.get());
        assertFalse(presenter.gameOverCalled.get());
    }

    @Test
    void findMatchingCategory_returnsCorrectCategoryOrNull() {
        Map<String, List<String>> cats = new LinkedHashMap<>();
        cats.put("A", Arrays.asList("a1","a2","a3","a4"));
        cats.put("B", Arrays.asList("b1","b2","b3","b4"));
        Game game = TestGameFactory.createGame("G", cats);

        GameSubmitGuessInteractor interactor = new GameSubmitGuessInteractor(repo, new TestGuessPresenter());

        // All from A
        assertNotNull(interactor.findMatchingCategory(game, Arrays.asList("a1","a2","a3","a4")));
        // Mixed
        assertNull(interactor.findMatchingCategory(game, Arrays.asList("a1","b1","a2","a3")));
        // Not in any category
        assertNull(interactor.findMatchingCategory(game, Arrays.asList("x","y","z","w")));
        // Empty selection
        assertNull(interactor.findMatchingCategory(game, Collections.emptyList()));
        // Null selection
        assertNull(interactor.findMatchingCategory(game, null));
    }

    @Test
    void getRemainingWords_returnsCorrectly() {
        Map<String, List<String>> cats = new LinkedHashMap<>();
        cats.put("A", Arrays.asList("a1","a2","a3","a4"));
        cats.put("B", Arrays.asList("b1","b2","b3","b4"));
        Game game = TestGameFactory.createGame("G", cats);
        GameState state = new GameState(2);
        // No categories found yet
        GameSubmitGuessInteractor interactor = new GameSubmitGuessInteractor(repo, new TestGuessPresenter());
        List<String> all = interactor.getRemainingWords(game, state);
        assertEquals(8, all.size());

        // Mark A as found
        state.addFoundCategory(new Category("A", cats.get("A")));
        List<String> rem = interactor.getRemainingWords(game, state);
        for (String w : cats.get("A")) {
            assertFalse(rem.contains(w));
        }
        for (String w : cats.get("B")) {
            assertTrue(rem.contains(w));
        }
    }

    @Test
    void selectionNotFour_doesNothing() {
        Map<String, List<String>> cats = new LinkedHashMap<>();
        cats.put("A", Arrays.asList("a1","a2","a3","a4"));
        Game game = TestGameFactory.createGame("G", cats);
        GameState state = new GameState(2);
        repo.save(game, state);

        TestGuessPresenter presenter = new TestGuessPresenter();
        GameSubmitGuessInteractor interactor = new GameSubmitGuessInteractor(repo, presenter);

        // 3 words
        interactor.execute(Arrays.asList("a1","a2","a3"));
        // 5 words
        interactor.execute(Arrays.asList("a1","a2","a3","a4","a5"));

        assertNull(presenter.correctCategoryName);
        assertNull(presenter.mistakesRemaining);
        assertFalse(presenter.alreadyFound);
        assertFalse(presenter.winCalled.get());
        assertFalse(presenter.gameOverCalled.get());
    }

    @Test
    void selectionWithWordsNotInAnyCategory_doesNothing() {
        Map<String, List<String>> cats = new LinkedHashMap<>();
        cats.put("A", Arrays.asList("a1","a2","a3","a4"));
        Game game = TestGameFactory.createGame("G", cats);
        GameState state = new GameState(2);
        repo.save(game, state);

        TestGuessPresenter presenter = new TestGuessPresenter();
        GameSubmitGuessInteractor interactor = new GameSubmitGuessInteractor(repo, presenter);

        interactor.execute(Arrays.asList("x","y","z","w"));

        assertNotNull(presenter.mistakesRemaining);
        assertEquals(1, presenter.mistakesRemaining.intValue());
    }

    @Test
    void emptySelectionList_doesNothing() {
        Map<String, List<String>> cats = new LinkedHashMap<>();
        cats.put("A", Arrays.asList("a1","a2","a3","a4"));
        Game game = TestGameFactory.createGame("G", cats);
        GameState state = new GameState(2);
        repo.save(game, state);

        TestGuessPresenter presenter = new TestGuessPresenter();
        GameSubmitGuessInteractor interactor = new GameSubmitGuessInteractor(repo, presenter);

        interactor.execute(new ArrayList<>());

        assertNull(presenter.correctCategoryName);
        assertNull(presenter.mistakesRemaining);
        assertFalse(presenter.alreadyFound);
        assertFalse(presenter.winCalled.get());
        assertFalse(presenter.gameOverCalled.get());
    }

    @Test
    void gameWithNoCategories_doesNothing() {
        Game game = new Game("", "Empty", new ArrayList<>(), new ArrayList<>());
        GameState state = new GameState(2);
        repo.save(game, state);

        TestGuessPresenter presenter = new TestGuessPresenter();
        GameSubmitGuessInteractor interactor = new GameSubmitGuessInteractor(repo, presenter);

        interactor.execute(Arrays.asList("a", "b", "c", "d"));

        assertNull(presenter.correctCategoryName);
        assertNull(presenter.mistakesRemaining);
        assertFalse(presenter.alreadyFound);
        assertFalse(presenter.winCalled.get());
        assertFalse(presenter.gameOverCalled.get());
    }
}
