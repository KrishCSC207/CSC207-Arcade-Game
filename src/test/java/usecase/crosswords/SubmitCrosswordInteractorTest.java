package usecase.crosswords;

import org.junit.jupiter.api.Test;
import use_case.crossword.CrosswordPuzzleDataAccessInterface;
import use_case.crossword.submit.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SubmitCrosswordInteractorTest {

    @Test
    void testSubmitCorrectAnswers() {
        // Arrange
        MockCrosswordPuzzleDataAccess dataAccess = new MockCrosswordPuzzleDataAccess();
        MockSubmitCrosswordPresenter presenter = new MockSubmitCrosswordPresenter();
        SubmitCrosswordInteractor interactor = new SubmitCrosswordInteractor(dataAccess, presenter);
        List<String> userAnswers = List.of("solution1", "solution2");
        SubmitCrosswordInputData inputData = new SubmitCrosswordInputData(userAnswers, System.currentTimeMillis());

        // Act
        interactor.execute(inputData);

        // Assert
        assertTrue(presenter.outputData.isAllCorrect());
    }

    @Test
    void testSubmitIncorrectAnswers() {
        // Arrange
        MockCrosswordPuzzleDataAccess dataAccess = new MockCrosswordPuzzleDataAccess();
        MockSubmitCrosswordPresenter presenter = new MockSubmitCrosswordPresenter();
        SubmitCrosswordInteractor interactor = new SubmitCrosswordInteractor(dataAccess, presenter);
        List<String> userAnswers = List.of("wrong1", "wrong2");
        SubmitCrosswordInputData inputData = new SubmitCrosswordInputData(userAnswers, System.currentTimeMillis());

        // Act
        interactor.execute(inputData);

        // Assert
        assertFalse(presenter.outputData.isAllCorrect());
    }

    private static class MockCrosswordPuzzleDataAccess implements CrosswordPuzzleDataAccessInterface {
        @Override
        public entity.CrosswordPuzzle loadPuzzle() {
            return new entity.CrosswordPuzzle("test_id", "test_path", List.of("solution1", "solution2"));
        }

        @Override
        public ArrayList<String> getCurrentPuzzleSolutions() {
            return new ArrayList<>(List.of("solution1", "solution2"));
        }
    }

    private static class MockSubmitCrosswordPresenter implements SubmitCrosswordOutputBoundary {
        SubmitCrosswordOutputData outputData;

        @Override
        public void presentResult(SubmitCrosswordOutputData outputData) {
            this.outputData = outputData;
        }
    }
    @Test
    void testSubmitMismatchedCount() {
        MockCrosswordPuzzleDataAccess dataAccess = new MockCrosswordPuzzleDataAccess();
        MockSubmitCrosswordPresenter presenter = new MockSubmitCrosswordPresenter();
        SubmitCrosswordInteractor interactor = new SubmitCrosswordInteractor(dataAccess, presenter);

        List<String> userAnswers = List.of("solution1");
        SubmitCrosswordInputData inputData = new SubmitCrosswordInputData(userAnswers, System.currentTimeMillis());

        interactor.execute(inputData);

        assertFalse(presenter.outputData.isAllCorrect());
    }

    @Test
    void testSubmitCaseAndWhitespace() {
        MockCrosswordPuzzleDataAccess dataAccess = new MockCrosswordPuzzleDataAccess();
        MockSubmitCrosswordPresenter presenter = new MockSubmitCrosswordPresenter();
        SubmitCrosswordInteractor interactor = new SubmitCrosswordInteractor(dataAccess, presenter);

        List<String> userAnswers = List.of("  SOLUTION1  ", "SOLUTION2");
        SubmitCrosswordInputData inputData = new SubmitCrosswordInputData(userAnswers, System.currentTimeMillis());

        interactor.execute(inputData);

        assertTrue(presenter.outputData.isAllCorrect());
    }
}