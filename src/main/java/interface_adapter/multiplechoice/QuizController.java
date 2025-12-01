package interface_adapter.multiplechoice;

import use_case.multiplechoice.quiz.QuizInputData;
import use_case.multiplechoice.quiz.QuizInteractor;
import use_case.multiplechoice.submit.SubmitAnswerInputData;
import use_case.multiplechoice.submit.SubmitAnswerInteractor;

/**
 * Controller that converts UI actions to use case inputs.
 */
public class QuizController {
    private final QuizInteractor quizInteractor;
    private SubmitAnswerInteractor submitAnswerInteractor;

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
            SubmitAnswerInputData inputData = new SubmitAnswerInputData(answer);
            submitAnswerInteractor.execute(inputData);
        }
    }

    public void nextQuestion() {
        if (submitAnswerInteractor != null) {
            submitAnswerInteractor.advance();
        }
    }

    public QuizInteractor getQuizInteractor() {
        return quizInteractor;
    }
}
