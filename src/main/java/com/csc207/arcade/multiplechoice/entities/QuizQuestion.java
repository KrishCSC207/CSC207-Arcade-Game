package com.csc207.arcade.multiplechoice.entities;

/**
 * Entity representing a single quiz question.
 * This is a Plain Old Java Object (POJO) that holds the core data for a question.
 */
public class QuizQuestion {
    private String questionId;
    private String imagePath;
    private int level;
    private String correctAnswer;

    /**
     * Default constructor for deserialization.
     */
    public QuizQuestion() {
    }

    /**
     * Constructor with all fields.
     *
     * @param questionId    Unique identifier for the question
     * @param imagePath     Path to the question image (relative to resources)
     * @param level         Difficulty level of the question
     * @param correctAnswer The correct answer (A, B, C, or D)
     */
    public QuizQuestion(String questionId, String imagePath, int level, String correctAnswer) {
        this.questionId = questionId;
        this.imagePath = imagePath;
        this.level = level;
        this.correctAnswer = correctAnswer;
    }

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }
}
