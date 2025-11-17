package com.csc207.arcade.multiplechoice.interface_adapter;

import com.csc207.arcade.multiplechoice.use_case.quiz.QuizOutputBoundary;
import com.csc207.arcade.multiplechoice.use_case.quiz.QuizOutputData;
import com.csc207.arcade.multiplechoice.use_case.submit.SubmitAnswerOutputBoundary;
import com.csc207.arcade.multiplechoice.use_case.submit.SubmitAnswerOutputData;

/**
 * Presenter that formats data from interactors for the ViewModels.
 */
public class QuizPresenter implements QuizOutputBoundary, SubmitAnswerOutputBoundary {

    private final QuizViewModel quizViewModel;
    private final ResultsViewModel resultsViewModel;

    public QuizPresenter(QuizViewModel quizViewModel, ResultsViewModel resultsViewModel) {
        this.quizViewModel = quizViewModel;
        this.resultsViewModel = resultsViewModel;
    }

    @Override
    public void prepareQuizView(QuizOutputData data) {
        quizViewModel.setCurrentImagePath(data.getImagePath());
        quizViewModel.setQuestionProgressLabel(data.getQuestionProgress());

        // 新题时清空按钮 & 状态
        quizViewModel.setIncorrectButton(null);
        quizViewModel.setFeedbackState("NONE");
    }

    @Override
    public void prepareSuccessView(SubmitAnswerOutputData data) {
        // ✅ 先记录用户点了哪个按钮
        quizViewModel.setIncorrectButton(data.getSelectedAnswer());
        // ✅ 再发 feedbackState 事件，让 View 用刚更新的按钮来决定颜色
        quizViewModel.setFeedbackState("CORRECT");
    }

    @Override
    public void prepareFailView(SubmitAnswerOutputData data) {
        quizViewModel.setIncorrectButton(data.getSelectedAnswer());
        quizViewModel.setFeedbackState("INCORRECT");
    }

    @Override
    public void prepareResultsView(double accuracy, long totalTimeMs) {
        // 结果页：写入 ResultsViewModel，触发 PropertyChange
        resultsViewModel.setAccuracy(accuracy);
        resultsViewModel.setTotalTimeMs(totalTimeMs);
    }
}