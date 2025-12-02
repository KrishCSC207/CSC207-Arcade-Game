package entity;

/**
 * Entity representing a quiz question.
 */
public class QuizQuestion {
    private String questionId;
    private String imagePath;
    private String category;
    private String correctAnswer;

    public QuizQuestion() {
    }

    public QuizQuestion(String questionId, String imagePath, String category, String correctAnswer) {
        this.questionId = questionId;
        this.imagePath = imagePath;
        this.category = normalize(category);
        this.correctAnswer = correctAnswer;
    }

    private String normalize(String s) {
        return s == null ? "" : s.trim();
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = normalize(category);
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }
}
