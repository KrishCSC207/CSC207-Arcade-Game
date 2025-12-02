package usecase.crossword.submit;

public class SubmitCrosswordOutputData {
    private final boolean allCorrect;
    private final long elapsedTime;

    public SubmitCrosswordOutputData(boolean allCorrect, long elapsedTime) {
        this.allCorrect = allCorrect;
        this.elapsedTime = elapsedTime;
    }

    public boolean isAllCorrect() {
        return allCorrect;
    }

    public long getElapsedTime() {
        return elapsedTime;
    }
}