package use_case;

import com.csc207.arcade.multiplechoice.data_access.QuestionDAO;
import com.csc207.arcade.multiplechoice.entities.QuizQuestion;
import com.csc207.arcade.multiplechoice.entities.QuizSession;
import com.csc207.arcade.multiplechoice.use_case.QuestionDAI;

import com.csc207.arcade.multiplechoice.use_case.quiz.QuizInputData;
import com.csc207.arcade.multiplechoice.use_case.quiz.QuizInteractor;
import com.csc207.arcade.multiplechoice.use_case.quiz.QuizOutputBoundary;
import com.csc207.arcade.multiplechoice.use_case.quiz.QuizOutputData;
import com.csc207.arcade.multiplechoice.use_case.submit.SubmitAnswerInputData;
import com.csc207.arcade.multiplechoice.use_case.submit.SubmitAnswerInteractor;
import com.csc207.arcade.multiplechoice.use_case.submit.SubmitAnswerOutputBoundary;
import com.csc207.arcade.multiplechoice.use_case.submit.SubmitAnswerOutputData;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SubmitAnswerInteractorTest {
    @Test
    void successTest(){
        // Create a sample of QuizQuestion list returned by the DAO
        QuizQuestion q1 = new QuizQuestion("id1","data/images/id1_M_2_Ans_D.png","Module 2","D");
        QuizQuestion q2 = new QuizQuestion("id15","data/images/id15_M_0_Ans_C.png","Module 0","C");
        List<QuizQuestion> sampleQuestions = new ArrayList<>();
        sampleQuestions.add(q1);
        sampleQuestions.add(q2);
        // Create a quiz session with the sample questions
        QuizSession quizSession = new QuizSession(sampleQuestions);
        // Suppose the user chooses the correct answer "D" for q1
        SubmitAnswerInputData submitAnswerInputData = new SubmitAnswerInputData("D");
        // Track which presenter method was called
        final boolean[] successCalled = {false};
        final boolean[] failCalled = {false};

        // Create a mock presenter to verify output
        SubmitAnswerOutputBoundary mockPresenter = new SubmitAnswerOutputBoundary() {
            @Override
            public void prepareSuccessView(SubmitAnswerOutputData outputData) {
                successCalled[0] = true;
                assertTrue(outputData.isCorrect());
                assertEquals("D", outputData.getSelectedAnswer());
                assertEquals("D", outputData.getCorrectAnswer());
            }

            @Override
            public void prepareFailView(SubmitAnswerOutputData outputData) {
                failCalled[0] = true;
            }

            @Override
            public void prepareResultsView(double accuracy, long totalTimeMs) {
                // Not tested in this case
            }
        };
        // Create the interactor
        SubmitAnswerInteractor interactor = new SubmitAnswerInteractor(quizSession, mockPresenter, null);
        // Execute the interactor with the input data
        interactor.execute(submitAnswerInputData);

        // Verify only success path is called
        assertTrue(successCalled[0]);
        assertFalse(failCalled[0]);

        // Verify the session didn't advance on submit
        assertEquals(0, quizSession.getCurrentQuestionIndex());
        assertSame(q1, quizSession.getCurrentQuestion());
    }




    @Test
    void failureTest(){
        QuizQuestion q1 = new QuizQuestion("id1","data/images/id1_M_2_Ans_D.png","Module 2","D");
        QuizQuestion q2 = new QuizQuestion("id15","data/images/id15_M_0_Ans_C.png","Module 0","C");
        List<QuizQuestion> sampleQuestions = new ArrayList<>();
        sampleQuestions.add(q1);
        sampleQuestions.add(q2);
        // Create a quiz session with the sample questions
        QuizSession quizSession = new QuizSession(sampleQuestions);
        // Suppose the user chooses the incorrect answer "A" for q1
        SubmitAnswerInputData submitAnswerInputData = new SubmitAnswerInputData("A");
        // Track which presenter method was called
        final boolean[] successCalled = {false};
        final boolean[] failCalled = {false};
        // Create a mock presenter to verify output
        SubmitAnswerOutputBoundary mockPresenter = new SubmitAnswerOutputBoundary() {
            @Override
            public void prepareSuccessView(SubmitAnswerOutputData outputData) {
                successCalled[0] = true;
            }

            @Override
            public void prepareFailView(SubmitAnswerOutputData outputData) {
                failCalled[0] = true;
                assertFalse(outputData.isCorrect());
                assertEquals("A", outputData.getSelectedAnswer());
                assertEquals("D", outputData.getCorrectAnswer());
            }

            @Override
            public void prepareResultsView(double accuracy, long totalTimeMs) {
                // Not tested in this case
            }
        };

        // Create the interactor
        SubmitAnswerInteractor interactor = new SubmitAnswerInteractor(quizSession, mockPresenter, null);

        // Execute the interactor with the input data
        interactor.execute(submitAnswerInputData);

        // Verify only failure path is called
        assertFalse(successCalled[0]);
        assertTrue(failCalled[0]);

        // Verify the session didn't advance on submit
        assertEquals(0, quizSession.getCurrentQuestionIndex());
        assertSame(q1, quizSession.getCurrentQuestion());

    }


    @Test
    void advanceTestWithNextQuestion(){
        QuizQuestion q1 = new QuizQuestion("id1","data/images/id1_M_2_Ans_D.png","Module 2","D");
        QuizQuestion q2 = new QuizQuestion("id15","data/images/id15_M_0_Ans_C.png","Module 0","C");
        List<QuizQuestion> sampleQuestions = new ArrayList<>();
        sampleQuestions.add(q1);
        sampleQuestions.add(q2);
        // Create a quiz session with the sample questions
        QuizSession quizSession = new QuizSession(sampleQuestions);


        SubmitAnswerOutputBoundary mockSubmitPresenter = new SubmitAnswerOutputBoundary() {
            @Override
            public void prepareSuccessView(SubmitAnswerOutputData outputData) {
            }

            @Override
            public void prepareFailView(SubmitAnswerOutputData outputData) {
            }

            @Override
            public void prepareResultsView(double accuracy, long totalTimeMs) {
            }
        };

        QuizOutputBoundary mockQuizPresenter = new QuizOutputBoundary() {
            @Override
            public void prepareQuizView(QuizOutputData data) {
                assertEquals("data/images/id15_M_0_Ans_C.png", data.getImagePath());
                assertEquals("Question 2/2", data.getQuestionProgress());
            }
        };




        // Create the interactor
        SubmitAnswerInteractor interactor = new SubmitAnswerInteractor(quizSession, mockSubmitPresenter, mockQuizPresenter);
        // Advance to next question
        interactor.advance();
        // Verify the session advanced to the next question
        assertEquals(1, quizSession.getCurrentQuestionIndex());
        assertSame(q2, quizSession.getCurrentQuestion());
    }

    @Test
    void advanceTestAtLastQuestion(){
        // Simulate being at the last question by only having one question
        QuizQuestion q1 = new QuizQuestion("id1","data/images/id1_M_2_Ans_D.png","Module 2","D");
        List<QuizQuestion> sampleQuestions = new ArrayList<>();
        sampleQuestions.add(q1);
        // Create a quiz session with the sample questions
        QuizSession quizSession = new QuizSession(sampleQuestions);


        SubmitAnswerOutputBoundary mockSubmitPresenter = new SubmitAnswerOutputBoundary() {
            @Override
            public void prepareSuccessView(SubmitAnswerOutputData outputData) {
            }

            @Override
            public void prepareFailView(SubmitAnswerOutputData outputData) {
            }

            @Override
            public void prepareResultsView(double accuracy, long totalTimeMs) {
                assertEquals(accuracy, quizSession.getAccuracy());
                assertEquals(totalTimeMs, quizSession.getTotalTime());
            }
        };

        QuizOutputBoundary mockQuizPresenter = new QuizOutputBoundary() {
            @Override
            public void prepareQuizView(QuizOutputData data) {
                assertEquals("data/images/id1_M_2_Ans_D.png", data.getImagePath());
                assertEquals("Question 1/1", data.getQuestionProgress());
            }
        };




        // Create the interactor
        SubmitAnswerInteractor interactor = new SubmitAnswerInteractor(quizSession, mockSubmitPresenter, mockQuizPresenter);

        interactor.execute(new SubmitAnswerInputData("D"));
        // Advance to next question
        interactor.advance();
        assertTrue(quizSession.isQuizOver());
        assertEquals(1.0, quizSession.getAccuracy());
    }
}
