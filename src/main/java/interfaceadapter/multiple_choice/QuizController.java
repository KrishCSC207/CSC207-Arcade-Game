package interfaceadapter.multiple_choice;

import usecase.multiplechoice.quiz.QuizInputBoundary;
import usecase.multiplechoice.quiz.QuizInputData;
import usecase.multiplechoice.quiz.QuizInteractor;
import usecase.multiplechoice.submit.SubmitAnswerInputData;
import usecase.multiplechoice.submit.SubmitAnswerInteractor;

/**
 * Controller that converts UI actions to use case inputs.
 */
public class QuizController {
    private final QuizInputBoundary quizInteractor;
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

    public QuizInputBoundary getQuizInteractor() {
        return quizInteractor;
    }
}
