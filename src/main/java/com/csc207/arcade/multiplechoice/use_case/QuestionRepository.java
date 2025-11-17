package com.csc207.arcade.multiplechoice.use_case;

import com.csc207.arcade.multiplechoice.entities.QuizQuestion;
import java.util.List;

/**
 * Interface defining the contract for data access to quiz questions.
 */
public interface QuestionRepository {
    /**
     * Loads data from the data source.
     */
    void loadData();

    /**
     * Gets a random selection of questions.
     *
     * @param count Number of questions to retrieve
     * @return List of random QuizQuestion objects
     */
    List<QuizQuestion> getQuestions(int count);
}
