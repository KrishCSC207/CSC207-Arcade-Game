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

        QuizOutputData outputData = new QuizOutputData(
                firstQuestion.getImagePath(),
                progressLabel
        );

        // 5) Present the quiz view
        quizPresenter.prepareQuizView(outputData);
    }

    public QuizSession getCurrentSession() {
        return currentSession;
    }
}