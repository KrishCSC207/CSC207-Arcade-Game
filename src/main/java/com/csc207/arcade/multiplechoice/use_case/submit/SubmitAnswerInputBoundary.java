package com.csc207.arcade.multiplechoice.use_case.submit;

/**
 * Input boundary for submitting an answer.
 */
public interface SubmitAnswerInputBoundary {
    /**
     * Executes the submit answer use case.
     *
     * @param inputData Input data containing the selected answer
     */
    void execute(SubmitAnswerInputData inputData);
}
