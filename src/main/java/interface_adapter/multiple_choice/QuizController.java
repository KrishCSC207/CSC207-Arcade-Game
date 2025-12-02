package interface_adapter.multiple_choice;

import use_case.multiple_choice.quiz.QuizInputData;
import use_case.multiple_choice.quiz.QuizInteractor;
import use_case.multiple_choice.submit.SubmitAnswerInputData;
import use_case.multiple_choice.submit.SubmitAnswerInteractor;

/**
 * Controller that converts UI actions to use case inputs.
 */
public class QuizController {
    private final use_case.multiple_choice.quiz.QuizInteractor quizInteractor;
    private use_case.multiple_choice.submit.SubmitAnswerInteractor submitAnswerInteractor;

    public QuizController(QuizInteractor quizInteractor) {
        this.quizInteractor = quizInteractor;
    }

    public void startQuiz(String category) {
        quizInteractor.execute(new QuizInputData(category));
    }

    public void setSubmitAnswerInteractor(SubmitAnswerInteractor submitAnswerInteractor) {
        this.submitAnswerInteractor = submitAnswerInteractor;
    }

    public boolean hasSubmitAnswerInteractor() {
        return submitAnswerInteractor != null;
    }

    public void submitAnswer(String answer) {
        if (submitAnswerInteractor != null) {
            use_case.multiple_choice.submit.SubmitAnswerInputData inputData = new SubmitAnswerInputData(answer);
            submitAnswerInteractor.execute(inputData);
        }
    }

    public void nextQuestion() {
        if (submitAnswerInteractor != null) {
            submitAnswerInteractor.advance();
        }
    }
}