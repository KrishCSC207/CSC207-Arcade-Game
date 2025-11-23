package com.csc207.arcade.multiplechoice.use_case.quiz;

import com.csc207.arcade.multiplechoice.entities.QuizQuestion;
import com.csc207.arcade.multiplechoice.entities.QuizSession;
import com.csc207.arcade.multiplechoice.use_case.QuestionDAI;

import java.util.List;

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
        // 1) 读取难度（可能为 null，表示不过滤）
        String category = inputData.getCategory();

        // 2) 按难度（若有）抽取 15 道题
        List<QuizQuestion> questions = questionDAO.getCategorizedQuestions(category);

        // 3) 建立会话
        currentSession = new QuizSession(questions);

        // 4) 取第一题并构造输出
        QuizQuestion firstQuestion = currentSession.getCurrentQuestion();
        String progressLabel = String.format("Question %d/%d",
                currentSession.getCurrentQuestionIndex() + 1,
                currentSession.getTotalQuestions());

        QuizOutputData outputData = new QuizOutputData(
                firstQuestion.getImagePath(),
                progressLabel
        );

        // 5) 通知 Presenter 刷新视图
        quizPresenter.prepareQuizView(outputData);
    }

    public QuizSession getCurrentSession() {
        return currentSession;
    }
}