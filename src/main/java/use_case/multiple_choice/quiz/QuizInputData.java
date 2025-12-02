package use_case.multiple_choice.quiz;

/**
 * Input data for starting a quiz.
 */
public class QuizInputData {
    private final String category;

    public QuizInputData(String category) {
        this.category = category;
    }

    public String getCategory() {
        return category;
    }
}
