package com.csc207.arcade.multiplechoice.interface_adapter;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * ViewModel for the results view.
 */
public class ResultsViewModel {
    private final PropertyChangeSupport support;
    
    private double accuracy;
    private long totalTimeMs;

    public ResultsViewModel() {
        this.support = new PropertyChangeSupport(this);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        support.removePropertyChangeListener(listener);
    }

    public double getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(double accuracy) {
        double oldValue = this.accuracy;
        this.accuracy = accuracy;
        support.firePropertyChange("accuracy", null, accuracy);
    }

    public long getTotalTimeMs() {
        return totalTimeMs;
    }

    public void setTotalTimeMs(long totalTimeMs) {
        long oldValue = this.totalTimeMs;
        this.totalTimeMs = totalTimeMs;
        support.firePropertyChange("totalTimeMs", oldValue, totalTimeMs);
    }
}
