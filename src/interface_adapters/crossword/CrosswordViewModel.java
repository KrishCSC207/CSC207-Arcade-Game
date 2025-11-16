package interface_adapters.crossword;

import java.beans.PropertyChangeListener; //  From clean CA lab
import java.beans.PropertyChangeSupport;


public class CrosswordViewModel {

    private final PropertyChangeSupport support = new PropertyChangeSupport(this);
    private String puzzleId;
    private String imagePath;
    private int numSolutions;
    private String statusMessage;

    private String feedbackMessage;
    private boolean submissionCorrect;

    public CrosswordViewModel() {
        this.puzzleId = "";
        this.imagePath = "";
        this.numSolutions = 0;
        //this.statusMessage = "";
        this.feedbackMessage = "";
        this.submissionCorrect = false;
    }


    public String getPuzzleId() {
        return puzzleId;
    }

    public String getImagePath() {
        return imagePath;
    }

    public int getNumSolutions() {
        return numSolutions;
    }

//    public String getStatusMessage() {
//        return statusMessage;
//    }

    public String getFeedbackMessage() {
        return feedbackMessage;
    }

    public void setFeedbackMessage(String feedbackMessage) {
        String oldMessage = this.feedbackMessage;
        this.feedbackMessage = feedbackMessage;
        // Fire event so the View can update!
        support.firePropertyChange("feedbackMessage", oldMessage, feedbackMessage);
    }

    public boolean isSubmissionCorrect() {
        return submissionCorrect;
    }

    public void setSubmissionCorrect(boolean submissionCorrect) {
        this.submissionCorrect = submissionCorrect;
    }

    public void setPuzzleId(String puzzleId) {
        this.puzzleId = puzzleId;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        support.removePropertyChangeListener(listener);
    }

    public void setNumSolutions(int numSolutions) {
        this.numSolutions = numSolutions;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }
}
