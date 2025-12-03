package interfaceadapter.crossword;

import usecase.crossword.start.StartCrosswordInputBoundary;
import usecase.crossword.submit.SubmitCrosswordInputBoundary;
import usecase.crossword.submit.SubmitCrosswordInputData;
import java.util.List;
import dataaccess.SimpleDaoSelector;

public class CrosswordController {

    private final StartCrosswordInputBoundary startUseCase;
    private final SubmitCrosswordInputBoundary submitInteractor;
    private final SimpleDaoSelector selector;

    public CrosswordController(StartCrosswordInputBoundary startUseCase, SubmitCrosswordInputBoundary submitInteractor) {
        this.startUseCase = startUseCase;
        this.submitInteractor = submitInteractor;
        this.selector = null;
    }

    public CrosswordController(StartCrosswordInputBoundary startUseCase,
                               SubmitCrosswordInputBoundary submitInteractor,
                               SimpleDaoSelector selector) {
        this.startUseCase = startUseCase;
        this.submitInteractor = submitInteractor;
        this.selector = selector;
    }

    public void startCrossword() {
        startUseCase.startCrossword();
    }

    public void startCrossword(String difficulty) {
        if (selector != null) {
            selector.select(difficulty);
        }
        startUseCase.startCrossword();
    }

    public void submitAnswers(List<String> userAnswers, long startTime){
        SubmitCrosswordInputData inputData = new SubmitCrosswordInputData(userAnswers, startTime);
        submitInteractor.execute(inputData);
    }
}