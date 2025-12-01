package use_case.multiplechoice.quiz;

import entity.multiplechoice.QuizQuestion;
import entity.multiplechoice.QuizSession;
import use_case.multiplechoice.QuestionDataAccessInterface;

import java.util.List;

/**
 * Interactor for starting a quiz.
 */
public class QuizInteractor implements QuizInputBoundary {
    private final QuestionDataAccessInterface questionDAO;
    private final QuizOutputBoundary quizPresenter;
    private QuizSession currentSession;

    public QuizInteractor(QuestionDataAccessInterface questionDAO, QuizOutputBoundary quizPresenter) {
        this.questionDAO = questionDAO;
        this.quizPresenter = quizPresenter;
    }

    @Override
    public void execute(QuizInputData inputData) {
        String category = inputData.getCategory();
        List<QuizQuestion> questions = questionDAO.getCategorizedQuestions(category);
        currentSession = new QuizSession(questions);

        QuizQuestion firstQuestion = currentSession.getCurrentQuestion();
        String progressLabel = String.format("Question %d/%d",
                currentSession.getCurrentQuestionIndex() + 1,
                currentSession.getTotalQuestions());

        QuizOutputData outputData = new QuizOutputData(
                firstQuestion.getImagePath(),
                progressLabel
        );

        quizPresenter.prepareQuizView(outputData);
    }

    public QuizSession getCurrentSession() {
        return currentSession;
    }
}
