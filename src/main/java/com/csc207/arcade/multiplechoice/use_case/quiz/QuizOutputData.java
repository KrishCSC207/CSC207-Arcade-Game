package com.csc207.arcade.multiplechoice.use_case.quiz;

/**
 * Output data for displaying a quiz question.
 */
public class QuizOutputData {
    private final String imagePath;
    private final String questionProgress;

    public QuizOutputData(String imagePath, String questionProgress) {
        this.imagePath = imagePath;
        this.questionProgress = questionProgress;
    }

    public String getImagePath() {
        return imagePath;
    }

    public String getQuestionProgress() {
        return questionProgress;
    }
}
