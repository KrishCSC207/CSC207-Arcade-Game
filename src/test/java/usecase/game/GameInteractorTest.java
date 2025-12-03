package usecase.game;

import entity.Game;
import entity.Category;
import entity.GameState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class GameInteractorTest {

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

    // Fake DAO that either returns a prepared game or throws
    static class FakeGameDataAccess implements GameDataAccessInterface {
        private final Game gameToReturn;
        private final IOException toThrow;

        FakeGameDataAccess(Game gameToReturn) {
            this.gameToReturn = gameToReturn;
            this.toThrow = null;
        }

        FakeGameDataAccess(IOException toThrow) {
            this.gameToReturn = null;
            this.toThrow = toThrow;
        }

        @Override
        public Game getGameByCode(String gameCode) throws IOException, InterruptedException {
            if (toThrow != null) throw toThrow;
            return gameToReturn;
        }
    }

    // Presenter spy
    static class TestGamePresenter implements GameOutputBoundary {
        String lastTitle;
        List<String> lastWords;
        String lastError;
        boolean presented = false;

        @Override
        public void presentGame(String title, List<String> allShuffledWords) {
            this.lastTitle = title;
            this.lastWords = allShuffledWords;
            this.presented = true;
        }

        @Override
        public void presentError(String errorMessage) {
            this.lastError = errorMessage;
            this.presented = true;
        }
    }

    // Helper to build Game for tests
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
    TestGamePresenter presenter;

    @BeforeEach
    void setup() {
        repo = new InMemoryGameStateRepository();
        presenter = new TestGamePresenter();
    }

    @Test
    void successfulLoad_savesGameAndPresents() {
        Map<String, List<String>> cats = new LinkedHashMap<>();
        cats.put("Animals", Arrays.asList("cat","dog","cow","pig"));
        cats.put("Fruits", Arrays.asList("apple","pear","banana","kiwi"));

        Game testGame = TestGameFactory.createGame("FunGame", cats);

        FakeGameDataAccess dao = new FakeGameDataAccess(testGame);
        GameInteractor interactor = new GameInteractor(dao, repo, presenter);

        interactor.execute("ANYCODE");

        assertTrue(presenter.presented, "Presenter should be invoked");
        assertEquals("FunGame", presenter.lastTitle, "Title forwarded to presenter");
        assertNotNull(repo.getActiveGame(), "Game should be saved in repository");
        assertNotNull(repo.getActiveGameState(), "GameState should be saved in repository");
    }

    @Test
    void loadFailure_presentsError() {
        FakeGameDataAccess dao = new FakeGameDataAccess(new IOException("network error"));
        GameInteractor interactor = new GameInteractor(dao, repo, presenter);

        interactor.execute("BAD");

        assertTrue(presenter.presented, "Presenter should be invoked on error");
        assertNotNull(presenter.lastError);
        assertTrue(presenter.lastError.contains("Failed to load game"));
    }
}
