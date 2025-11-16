package interface_adapters.crossword;

import use_case.crossword.start.StartCrosswordInputBoundary;
import use_case.crossword.submit.SubmitCrosswordInputBoundary;
import use_case.crossword.submit.SubmitCrosswordInputData;
import java.util.List;

public class CrosswordController {

    private final StartCrosswordInputBoundary startUseCase;
    private final SubmitCrosswordInputBoundary submitInteractor;

    public CrosswordController(StartCrosswordInputBoundary startUseCase, SubmitCrosswordInputBoundary submitInteractor) {
        this.startUseCase = startUseCase;
        this.submitInteractor = submitInteractor;
    }

    public void startCrossword() {
        startUseCase.startCrossword();
    }

    public void submitAnswers(List <String> userAnswers){
        SubmitCrosswordInputData inputData = new SubmitCrosswordInputData(userAnswers);
        submitInteractor.execute(inputData);
    }
}