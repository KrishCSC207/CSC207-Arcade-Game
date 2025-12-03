package usecase.multiplechoice.quiz;

import entity.QuizSession;

/**
 * Input boundary for starting a quiz.
 */
public interface QuizInputBoundary {
    /**
     * Executes the start quiz use case.
     *
     * @param inputData Input data for the quiz
     */
    void execute(QuizInputData inputData);

    public QuizSession getCurrentSession();
}
