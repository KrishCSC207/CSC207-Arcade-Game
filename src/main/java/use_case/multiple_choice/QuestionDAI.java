package use_case.multiple_choice;

import entity.QuizQuestion;
import java.util.List;

/**
 * Interface defining the contract for data access to quiz questions.
 */
public interface QuestionDAI {
    /**
     * Loads data from the data source.
     */
    void loadData();


    List<QuizQuestion> getCategorizedQuestions(String category);
}
