package use_case.multiple_choice.submit;

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
