package com.csc207.arcade.multiplechoice.data_access;

import com.csc207.arcade.multiplechoice.entities.QuizQuestion;
import com.csc207.arcade.multiplechoice.use_case.QuestionRepository;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Repository implementation that reads questions from a JSON file.
 */
public class JsonQuestionRepository implements QuestionRepository {
    private List<QuizQuestion> allQuestions;
    private final Gson gson;

    public JsonQuestionRepository() {
        this.gson = new Gson();
        this.allQuestions = new ArrayList<>();
    }

    @Override
    public void loadData() {
        try {
            InputStream is = getClass().getClassLoader().getResourceAsStream("data/questions.json");
            if (is == null) {
                System.err.println("Warning: questions.json not found. Using empty question list.");
                return;
            }
            
            InputStreamReader reader = new InputStreamReader(is);
            Type listType = new TypeToken<List<QuizQuestion>>(){}.getType();
            allQuestions = gson.fromJson(reader, listType);
            
            if (allQuestions == null) {
                allQuestions = new ArrayList<>();
            }
            
            reader.close();
            is.close();
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
}
