package com.csc207.arcade.multiplechoice.use_case.submit;

/**
 * Output boundary for answer submission.
 */
public interface SubmitAnswerOutputBoundary {
    /**
     * Prepares the view for a correct answer.
     *
     * @param outputData Data about the correct answer
     */
    void prepareSuccessView(SubmitAnswerOutputData outputData);

    /**
     * Prepares the view for an incorrect answer.
     *
     * @param outputData Data about the incorrect answer
     */
    void prepareFailView(SubmitAnswerOutputData outputData);

    /**
     * Prepares the final results view.
     *
     * @param accuracy Percentage of correct answers (0.0 to 1.0)
     * @param totalTimeMs Total time taken in milliseconds
     */
    void prepareResultsView(double accuracy, long totalTimeMs);
}
