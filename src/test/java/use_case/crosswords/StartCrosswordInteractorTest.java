package use_case.crosswords;

import entity.CrosswordPuzzle;
import org.junit.jupiter.api.Test;
import use_case.crossword.CrosswordPuzzleDataAccessInterface;
import use_case.crossword.start.StartCrosswordInteractor;
import use_case.crossword.start.StartCrosswordOutputBoundary;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StartCrosswordInteractorTest {

    @Test
    void testStartCrossword() {
        // Arrange
        MockCrosswordPuzzleDataAccess dataAccess = new MockCrosswordPuzzleDataAccess();
        MockStartCrosswordPresenter presenter = new MockStartCrosswordPresenter();
        StartCrosswordInteractor interactor = new StartCrosswordInteractor(dataAccess, presenter);

        // Act
        interactor.startCrossword();

        // Assert
        assertEquals("test_id", presenter.presentedId);
        assertEquals("test_path", presenter.presentedImagePath);
        assertEquals(2, presenter.presentedNumSolutions);
    }

    private static class MockCrosswordPuzzleDataAccess implements CrosswordPuzzleDataAccessInterface {
        @Override
        public CrosswordPuzzle loadPuzzle() {
            return new CrosswordPuzzle("test_id", "test_path", List.of("solution1", "solution2"));
        }

        @Override
        public ArrayList<String> getCurrentPuzzleSolutions() {
            return new ArrayList<>(List.of("solution1", "solution2"));
        }
    }

    private static class MockStartCrosswordPresenter implements StartCrosswordOutputBoundary {
        String presentedId;
        String presentedImagePath;
        int presentedNumSolutions;

        @Override
        public void presentCrossword(String id, String imagePath, int numSolutions) {
            this.presentedId = id;
            this.presentedImagePath = imagePath;
            this.presentedNumSolutions = numSolutions;
        }
    }
}