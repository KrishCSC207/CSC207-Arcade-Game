package com.csc207.arcade.multiplechoice.interface_adapter;

import com.csc207.arcade.multiplechoice.use_case.quiz.QuizInputData;
import com.csc207.arcade.multiplechoice.use_case.quiz.QuizInteractor;
import com.csc207.arcade.multiplechoice.use_case.submit.SubmitAnswerInputData;
import com.csc207.arcade.multiplechoice.use_case.submit.SubmitAnswerInteractor;

/**
 * Controller 仅负责把界面动作转换为用例输入。
 * 不依赖 Presenter，实现与展示层解耦。
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
}