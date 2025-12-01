package com.csc207.arcade.multiplechoice.use_case.quiz;

import com.csc207.arcade.multiplechoice.entities.QuizQuestion;
import com.csc207.arcade.multiplechoice.entities.QuizSession;
import com.csc207.arcade.multiplechoice.use_case.QuestionDAI;

import java.util.List;

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
        // 1) Get user-chosen category
        String category = inputData.getCategory();

        // 2) Get questions with respect to the chosen category
        List<QuizQuestion> questions = questionDAO.getCategorizedQuestions(category);

        // 3) Build new Quiz Session
        currentSession = new QuizSession(questions);

        // 4) Get current(first) question and prepare output data
        QuizQuestion firstQuestion = currentSession.getCurrentQuestion();
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
