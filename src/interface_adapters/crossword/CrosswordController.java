package interface_adapters.crossword;

import use_case.crossword.start.StartCrosswordInputBoundary;
import use_case.crossword.submit.SubmitCrosswordInputBoundary;
import use_case.crossword.submit.SubmitCrosswordInputData;
import java.util.List;
import interface_adapters.crossword.data_access.SimpleDaoSelector;

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
            selector.select(difficulty);   // tell the shared selector which DAO to use
        }
        startUseCase.startCrossword();     // run your existing start flow
    }

    public void submitAnswers(List<String> userAnswers){
        SubmitCrosswordInputData inputData = new SubmitCrosswordInputData(userAnswers);
        submitInteractor.execute(inputData);
    }
}