package use_case.quiz;

import entity.QuizQuestion;
import entity.QuizSession;
import use_case.multiple_choice.QuestionDAI;

import java.util.List;
import java.util.Collections;

/**
 * The QuizInteractor class initializes a new quiz session.
 * It retrieves questions based on the user's selected category.
 */
public class QuizInteractor implements QuizInputBoundary {
    /** Data Access object for stored questions. */
    private final QuestionDAI questionDAO;
    /** Presenter for preparing the quiz view. */
    private final QuizOutputBoundary quizPresenter;
    /** Current quiz session. */
    private QuizSession currentSession;
    /**
     * Constructs a QuizInteractor with the specified Question DAI and Output Boundary.
     *
     * @param questionDao   Data Access Interface for retrieving questions
     * @param quizPresenter Output Boundary for preparing the quiz view
     */
    public QuizInteractor(final QuestionDAI questionDao, final QuizOutputBoundary quizPresenter) {
        this.questionDAO = questionDao;
        this.quizPresenter = quizPresenter;
    }

    @Override
    public void execute(QuizInputData inputData) {
        questionDAO.loadData();
        // 1) Get user-chosen category
        String category = inputData.getCategory();

        // 2) Get questions with respect to the chosen category
        List<QuizQuestion> questions = questionDAO.getCategorizedQuestions(category);

        // Guard against null/empty question lists to avoid NPE
        if (questions == null || questions.isEmpty()) {
            // create an empty session so callers can still inspect it safely
            currentSession = new QuizSession(Collections.emptyList());
            // prepare output indicating no questions are available
            QuizOutputData emptyOutput = new QuizOutputData(null, "No questions available");
            quizPresenter.prepareQuizView(emptyOutput);
            return;
        }

        // 3) Build new Quiz Session
        currentSession = new QuizSession(questions);

        // 4) Get current(first) question and prepare output data
        QuizQuestion firstQuestion = currentSession.getCurrentQuestion();
        // defensive check (shouldn't be null if questions non-empty, but guard anyway)
        if (firstQuestion == null) {
            QuizOutputData emptyOutput = new QuizOutputData(null, "No questions available");
            quizPresenter.prepareQuizView(emptyOutput);
            return;
        }

        String progressLabel = String.format("Question %d/%d",
                currentSession.getCurrentQuestionIndex() + 1,
                currentSession.getTotalQuestions());

        QuizOutputData outputData = new QuizOutputData(firstQuestion.getImagePath(), progressLabel);

        // 5) Present the quiz view
        quizPresenter.prepareQuizView(outputData);
    }

    public QuizSession getCurrentSession() {
        return currentSession;
    }
}
