package com.csc207.arcade.multiplechoice.use_case.quiz;

/**
 * 输入数据对象，封装题目类别。
 */
public class QuizInputData {
    private final String category;

    public QuizInputData(String category) {
        this.category = category;
    }

    public String getCategory() {
        return category;
    }
}