package use_case.crossword.submit;

import java.util.List;

public class SubmitCrosswordInputData {

    private final List<String> userAnswers;

    public SubmitCrosswordInputData(List<String> userAnswers) {
        this.userAnswers = userAnswers;
    }

    public List<String> getUserAnswers() {
        return userAnswers;
    }
}