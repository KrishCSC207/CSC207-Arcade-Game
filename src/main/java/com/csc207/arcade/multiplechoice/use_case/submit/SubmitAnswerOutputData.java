package com.csc207.arcade.multiplechoice.use_case.submit;

/**
 * Output data for answer submission.
 */
public class SubmitAnswerOutputData {
    private final boolean isCorrect;
    private final String selectedAnswer;
    private final String correctAnswer;

    public SubmitAnswerOutputData(boolean isCorrect, String selectedAnswer, String correctAnswer) {
        this.isCorrect = isCorrect;
        this.selectedAnswer = selectedAnswer;
        this.correctAnswer = correctAnswer;
    }

    public boolean isCorrect() {
        return isCorrect;
    }

    public String getSelectedAnswer() {
        return selectedAnswer;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }
}
