package com.csc207.arcade.multiplechoice.interface_adapter;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * ViewModel for the quiz view.
 * Uses PropertyChangeSupport to notify observers of state changes.
 */
public class QuizViewModel {
    private final PropertyChangeSupport support;
    
    private String currentImagePath;
    private String questionProgressLabel;
    private String feedbackState;
    private String incorrectButton;

    public QuizViewModel() {
        this.support = new PropertyChangeSupport(this);
        this.feedbackState = "NONE";
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        support.removePropertyChangeListener(listener);
    }

    public String getCurrentImagePath() {
        return currentImagePath;
    }

    public void setCurrentImagePath(String imagePath) {
        String oldValue = this.currentImagePath;
        this.currentImagePath = imagePath;
        support.firePropertyChange("imagePath", oldValue, imagePath);
    }

    public String getQuestionProgressLabel() {
        return questionProgressLabel;
    }

    public void setQuestionProgressLabel(String label) {
        String oldValue = this.questionProgressLabel;
        this.questionProgressLabel = label;
        support.firePropertyChange("progressLabel", oldValue, label);
    }

    public String getFeedbackState() {
        return feedbackState;
    }

    public void setFeedbackState(String state) {
        String oldValue = this.feedbackState;
        this.feedbackState = state;
        support.firePropertyChange("feedbackState", oldValue, state);
    }

    public String getIncorrectButton() {
        return incorrectButton;
    }

    public void setIncorrectButton(String button) {
        String oldValue = this.incorrectButton;
        this.incorrectButton = button;
        support.firePropertyChange("incorrectButton", oldValue, button);
    }
}
