package com.csc207.arcade.multiplechoice.use_case.submit;

/**
 * Input data for submitting an answer.
 */
public class SubmitAnswerInputData {
    private final String selectedAnswer;

    public SubmitAnswerInputData(String selectedAnswer) {
        this.selectedAnswer = selectedAnswer;
    }

    public String getSelectedAnswer() {
        return selectedAnswer;
    }
}
