package com.csc207.arcade.multiplechoice.app;

import com.csc207.arcade.multiplechoice.entities.QuizQuestion;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class to scan image files and generate questions.json.
 * This should be run once at application startup.
 */
public class DataInitializer {
    private static final Pattern FILENAME_PATTERN = Pattern.compile("(id\\d+)_level(\\d+)_answer([ABCD])\\.png");

    public static void main(String[] args) {
        run();
    }

    /**
     * Scans the images directory and generates questions.json.
     */
    public static void run() {
        // Get the images directory path
        String resourcesPath = "src/main/resources/data/images/";
        File imagesDir = new File(resourcesPath);
        
        if (!imagesDir.exists() || !imagesDir.isDirectory()) {
            System.out.println("Images directory not found at: " + resourcesPath);
            System.out.println("Creating sample questions.json with demo data...");
            createSampleData();
            return;
        }

        File[] imageFiles = imagesDir.listFiles((dir, name) -> name.endsWith(".png"));
        
        if (imageFiles == null || imageFiles.length == 0) {
            System.out.println("No image files found. Creating sample questions.json...");
            createSampleData();
            return;
        }

        List<QuizQuestion> questions = new ArrayList<>();
        
        for (File imageFile : imageFiles) {
            String filename = imageFile.getName();
            Matcher matcher = FILENAME_PATTERN.matcher(filename);
            
            if (matcher.matches()) {
                String questionId = matcher.group(1);
                int level = Integer.parseInt(matcher.group(2));
                String correctAnswer = matcher.group(3);
                String imagePath = "data/images/" + filename;
                
                QuizQuestion question = new QuizQuestion(questionId, imagePath, level, correctAnswer);
                questions.add(question);
            }
        }
        
        saveQuestionsToJson(questions);
    }

    /**
     * Creates sample data for demonstration purposes.
     */
    private static void createSampleData() {
        List<QuizQuestion> sampleQuestions = new ArrayList<>();
        
        // Create 15 sample questions
        for (int i = 1; i <= 15; i++) {
            String id = String.format("id%03d", i);
            int level = (i % 3) + 1; // Levels 1-3
            String answer = String.valueOf((char)('A' + (i % 4))); // Rotate through A, B, C, D
            String imagePath = "data/images/" + id + "_level" + level + "_answer" + answer + ".png";
            
            sampleQuestions.add(new QuizQuestion(id, imagePath, level, answer));
        }
        
        saveQuestionsToJson(sampleQuestions);
    }

    /**
     * Saves the questions list to JSON file.
     */
    private static void saveQuestionsToJson(List<QuizQuestion> questions) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(questions);
        
        String outputPath = "src/main/resources/data/questions.json";
        File outputFile = new File(outputPath);
        
        // Create parent directories if they don't exist
        outputFile.getParentFile().mkdirs();
        
        try (FileWriter writer = new FileWriter(outputFile)) {
            writer.write(json);
            System.out.println("Successfully generated questions.json with " + questions.size() + " questions.");
        } catch (IOException e) {
            System.err.println("Error writing questions.json: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
