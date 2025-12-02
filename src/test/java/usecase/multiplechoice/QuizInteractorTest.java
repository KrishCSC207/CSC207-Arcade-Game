package usecase.multiplechoice;

import dataaccess.QuestionDAO;
import entity.QuizQuestion;
import entity.QuizSession;

import usecase.multiplechoice.quiz.QuizInputData;
import usecase.multiplechoice.quiz.QuizInteractor;
import usecase.multiplechoice.quiz.QuizOutputBoundary;
import usecase.multiplechoice.quiz.QuizOutputData;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class QuizInteractorTest {
    @Test
    void successTest() {
        // Create a sample of QuizQuestion list returned by the DAO
        QuizQuestion q1 = new QuizQuestion("id1","data/images/id1_M_2_Ans_D.png","Module 2","D");
        QuizQuestion q2 = new QuizQuestion("id15","data/images/id15_M_0_Ans_C.png","Module 0","C");
        List<QuizQuestion> sampleQuestions = new ArrayList<>();
        sampleQuestions.add(q1);
        sampleQuestions.add(q2);

        QuestionDAI mockDAO = new QuestionDAO() {
            @Override
            public List<QuizQuestion> getCategorizedQuestions(String category) {
                return sampleQuestions;
            }
        };

        // Capturing presenter to verify output
        QuizOutputBoundary mockPresenter = new QuizOutputBoundary() {
            @Override
            public void prepareQuizView(QuizOutputData outputData) {
                assertEquals("Question 1/2", outputData.getQuestionProgress());
                assertEquals(q1.getImagePath(), outputData.getImagePath());

            }
        };

        // Create interactor and execute with a category
        QuizInteractor interactor = new QuizInteractor(mockDAO, mockPresenter);

        // Build input data
        QuizInputData inputData = new QuizInputData("Module 2");

        // Run
        interactor.execute(inputData);

        // Verify that a QuizSession has been created correctly
        QuizSession session = interactor.getCurrentSession();
        assertNotNull(session);
        assertEquals(2, session.getTotalQuestions());
        assertEquals(0, session.getCurrentQuestionIndex());
    }
}
