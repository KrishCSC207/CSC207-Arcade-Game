package com.csc207.arcade.multiplechoice.use_case.quiz;

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
}
