package com.csc207.arcade.multiplechoice.data_access;

import com.csc207.arcade.multiplechoice.entities.QuizQuestion;
import com.csc207.arcade.multiplechoice.use_case.QuestionDAI;

import java.io.InputStream;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Repository implementation that reads questions from a JSON file.
 */
public class QuestionDAO implements QuestionDAI {
    private List<QuizQuestion> allQuestions;
//    private final Gson gson;

    public QuestionDAO() {
//        this.gson = new Gson();
        this.allQuestions = new ArrayList<>();
    }

    @Override
    public void loadData() {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("data/questions.json")) {

            if (is == null) {
                System.err.println("Warning: questions.json not found.");
                return;
            }

            JSONTokener tokener = new JSONTokener(is);

            JSONArray jsonArray = new JSONArray(tokener);

            allQuestions = new ArrayList<>();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);

                QuizQuestion q = new QuizQuestion();

                q.setQuestionId(obj.optString("questionId"));
                q.setImagePath(obj.optString("imagePath"));
                q.setCorrectAnswer(obj.optString("correctAnswer"));

                allQuestions.add(q);
            }

        } catch (Exception e) {
            System.err.println("Error loading questions: " + e.getMessage());
            e.printStackTrace();
            allQuestions = new ArrayList<>();
        }
    }

    @Override
    public List<QuizQuestion> getQuestions(int count) {
        if (allQuestions.isEmpty()) {
            loadData();
        }
        
        List<QuizQuestion> shuffled = new ArrayList<>(allQuestions);
        Collections.shuffle(shuffled);
        
        int actualCount = Math.min(count, shuffled.size());
        return shuffled.subList(0, actualCount);
    }

    @Override
    public List<QuizQuestion> getCategorizedQuestions(String category) {
        loadData();
        List<QuizQuestion> categorizedQuestions = new ArrayList<>();
        for (QuizQuestion q : allQuestions) {
            if (q.getCategory().equals(category)) {
                categorizedQuestions.add(q);
            }
        }
        return categorizedQuestions;
    }
}
