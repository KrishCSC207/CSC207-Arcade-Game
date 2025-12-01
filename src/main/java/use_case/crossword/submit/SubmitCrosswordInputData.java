package use_case.crossword.submit;

import java.util.List;

public class SubmitCrosswordInputData {
    private final List<String> userAnswers;
    private final long startTime;

    public SubmitCrosswordInputData(List<String> userAnswers, long startTime) {
        this.userAnswers = userAnswers;
        this.startTime = startTime;
    }

    public List<String> getUserAnswers() {
        return userAnswers;
    }

    public long getStartTime() {
        return startTime;
    }
}