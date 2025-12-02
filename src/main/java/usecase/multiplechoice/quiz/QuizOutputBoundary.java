package usecase.multiplechoice.quiz;

/**
 * Output boundary for quiz presentation.
 */
public interface QuizOutputBoundary {
    /**
     * Prepares the view to display a quiz question.
     *
     * @param outputData Data for displaying the question
     */
    void prepareQuizView(QuizOutputData outputData);
}
