package use_case.multiple_choice.quiz;

import entity.QuizQuestion;
import entity.QuizSession;
import use_case.multiple_choice.QuestionDAI;

import java.util.List;

/**
 * Interactor for starting a quiz.
 */
public class QuizInteractor implements QuizInputBoundary {
    private final QuestionDAI questionDAO;
    private final QuizOutputBoundary quizPresenter;
    private QuizSession currentSession;

    public QuizInteractor(QuestionDAI questionDAO, QuizOutputBoundary quizPresenter) {
        this.questionDAO = questionDAO;
        this.quizPresenter = quizPresenter;
    }

    @Override
    public void execute(QuizInputData inputData) {
        String category = inputData.getCategory();
        List<QuizQuestion> questions = questionDAO.getCategorizedQuestions(category);
        
        // Guard against null or empty questions list
        if (questions == null || questions.isEmpty()) {
            return;
        }
        
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
