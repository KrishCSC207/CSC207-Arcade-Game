package use_case.crosswords;

import data_access.DataCrosswordPuzzleDataAccess;
import entity.CrosswordPuzzle;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class DataCrosswordPuzzleDataAccessTest {

    @Test
    void testLoadEasyPuzzle() {
        // Arrange
        DataCrosswordPuzzleDataAccess dataAccess = new DataCrosswordPuzzleDataAccess("EASY");
        Set<String> expectedSolutions = new HashSet<>(List.of("Adapter", "String", "Integer", "Regex"));

        // Act
        CrosswordPuzzle puzzle = dataAccess.loadPuzzle();
        Set<String> actualSolutions = new HashSet<>(puzzle.getSolutions());

        // Assert
        assertNotNull(puzzle);
        assertEquals(expectedSolutions, actualSolutions);
    }

    @Test
    void testLoadMediumPuzzle() {
        // Arrange
        DataCrosswordPuzzleDataAccess dataAccess = new DataCrosswordPuzzleDataAccess("MEDIUM");
        Set<String> expectedSolutions = new HashSet<>(List.of("Compiler", "Machine", "Reference", "Equality", "Pattern", "Ethics", "Principle"));

        // Act
        CrosswordPuzzle puzzle = dataAccess.loadPuzzle();
        Set<String> actualSolutions = new HashSet<>(puzzle.getSolutions());

        // Assert
        assertNotNull(puzzle);
        assertEquals(expectedSolutions, actualSolutions);
    }

    @Test
    void testLoadHardPuzzle() {
        // Arrange
        DataCrosswordPuzzleDataAccess dataAccess = new DataCrosswordPuzzleDataAccess("HARD");
        Set<String> expectedSolutions = new HashSet<>(List.of("Boolean", "Entity", "Interactor", "Relational", "Controller", "Environment", "Interface", "Substitution", "Dependency", "Expression", "Inversion", "Variable"));

        // Act
        CrosswordPuzzle puzzle = dataAccess.loadPuzzle();
        Set<String> actualSolutions = new HashSet<>(puzzle.getSolutions());

        // Assert
        assertNotNull(puzzle);
        assertEquals(expectedSolutions, actualSolutions);
    }

    @Test
    void testGetCurrentPuzzleSolutions() {
        // Arrange
        DataCrosswordPuzzleDataAccess dataAccess = new DataCrosswordPuzzleDataAccess("EASY");
        dataAccess.loadPuzzle();
        Set<String> expectedSolutions = new HashSet<>(List.of("Adapter", "String", "Integer", "Regex"));

        // Act
        List<String> solutions = dataAccess.getCurrentPuzzleSolutions();
        Set<String> actualSolutions = new HashSet<>(solutions);

        // Assert
        assertEquals(expectedSolutions, actualSolutions);
    }
}