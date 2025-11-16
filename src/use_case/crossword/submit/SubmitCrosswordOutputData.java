package use_case.crossword.submit;

public class SubmitCrosswordOutputData {

    private final boolean allCorrect;

    public SubmitCrosswordOutputData(boolean allCorrect) {
        this.allCorrect = allCorrect;
    }

    public boolean isAllCorrect() {
        return allCorrect;
    }
}
