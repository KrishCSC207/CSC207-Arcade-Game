package use_case.quiz;

/**
 * Input data object encapsulating question categories.
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