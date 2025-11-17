package com.csc207.arcade.multiplechoice.entities;

import java.util.List;

/**
 * Entity that manages the state of a single quiz playthrough.
 * Tracks progress, timing, and scoring for a quiz session.
 */
public class QuizSession {
    private List<QuizQuestion> questions;
    private int currentQuestionIndex;
    private int correctAnswersCount;
    private long startTime;
    private long endTime;
    private boolean currentQuestionAnsweredIncorrectly;

    /**
     * Creates a new quiz session with the given questions.
     *
     * @param questions List of questions for this session
     */
    public QuizSession(List<QuizQuestion> questions) {
        this.questions = questions;
        this.currentQuestionIndex = 0;
        this.correctAnswersCount = 0;
        this.startTime = System.currentTimeMillis();
        this.currentQuestionAnsweredIncorrectly = false;
    }

    /**
     * Gets the current question being displayed.
     *
     * @return The current QuizQuestion
     */
    public QuizQuestion getCurrentQuestion() {
        if (currentQuestionIndex < questions.size()) {
            return questions.get(currentQuestionIndex);
        }
        return null;
    }

    /**
     * Records whether the answer was correct.
     * Only counts the answer on the first attempt for each question.
     *
     * @param isCorrect true if the answer was correct
     */
    public void recordAnswer(boolean isCorrect) {
        // Only record the answer if this is the first attempt at the current question
        if (!currentQuestionAnsweredIncorrectly) {
            if (isCorrect) {
                correctAnswersCount++;
            } else {
                // Mark that this question was answered incorrectly on first attempt
                currentQuestionAnsweredIncorrectly = true;
            }
        }
    }

    /**
     * Advances to the next question.
     *
     * @return true if there are more questions, false if the quiz is over
     */
    public boolean advanceToNextQuestion() {
        currentQuestionIndex++;
        currentQuestionAnsweredIncorrectly = false; // Reset for the new question
        return currentQuestionIndex < questions.size();
    }

    /**
     * Checks if the quiz is over.
     *
     * @return true if all questions have been answered
     */
    public boolean isQuizOver() {
        return currentQuestionIndex >= questions.size();
    }

    /**
     * Calculates the accuracy as a percentage.
     *
     * @return Accuracy from 0.0 to 1.0
     */
    public double getAccuracy() {
        if (questions.isEmpty()) {
            return 0.0;
        }
        return (double) correctAnswersCount / questions.size();
    }

    /**
     * Gets the total time taken for the quiz in milliseconds.
     *
     * @return Total time in milliseconds
     */
    public long getTotalTime() {
        return endTime - startTime;
    }

    /**
     * Marks the quiz as complete and records the end time.
     */
    public void finishQuiz() {
        this.endTime = System.currentTimeMillis();
    }

    public int getCurrentQuestionIndex() {
        return currentQuestionIndex;
    }

    public int getTotalQuestions() {
        return questions.size();
    }

    public int getCorrectAnswersCount() {
        return correctAnswersCount;
    }
}
