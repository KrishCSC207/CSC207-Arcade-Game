package data_access;

import entity.QuizQuestion;
import use_case.multiple_choice.QuestionDAI;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class QuestionDAO implements QuestionDAI {
    private List<QuizQuestion> allQuestions = new ArrayList<>();
    private boolean loaded = false;

    @Override
    public void loadData() {
        if (loaded) {
            return;
        }
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("data/questions.json")) {
            if (is == null) {
                System.err.println("Warning: data/questions.json not found.");
                return;
            }
            JSONArray jsonArray = new JSONArray(new JSONTokener(is));
            List<QuizQuestion> tmp = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                QuizQuestion q = new QuizQuestion();
                q.setQuestionId(obj.optString("questionId", ""));
                q.setImagePath(obj.optString("imagePath", ""));
                q.setCategory(obj.optString("category", ""));
                q.setCorrectAnswer(obj.optString("correctAnswer", ""));
                tmp.add(q);
            }
            this.allQuestions = tmp;
            this.loaded = true;
            System.out.println("Loaded questions count=" + allQuestions.size());
        } catch (Exception e) {
            System.err.println("Error loading questions: " + e.getMessage());
            e.printStackTrace();
            allQuestions = new ArrayList<>();
        }
    }

    @Override
    public List<QuizQuestion> getCategorizedQuestions(String category) {
        loadData();
        List<QuizQuestion> result = new ArrayList<>();
        for (QuizQuestion question : allQuestions) {
            if (question.getCategory().equals(category)) {
                result.add(question);
            }
        }
        return result;
    }
}