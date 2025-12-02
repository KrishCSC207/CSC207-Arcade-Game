package interfaceadapter.connections;

import interfaceadapter.ViewModel; // Your base ViewModel
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class ConnectionsViewModel extends ViewModel {

    public static final String VIEW_NAME = "connections_game";

    // Labels for property change events
    public static final String STATE_CHANGED_EVENT = "state";
    public static final String ERROR_EVENT = "error";

    private ConnectionsState state = new ConnectionsState();
    private final PropertyChangeSupport support = new PropertyChangeSupport(this);

    public ConnectionsViewModel() {
        super(VIEW_NAME);
    }

    public void setState(ConnectionsState state) {
        this.state = state;
    }

    public ConnectionsState getState() {
        return state;
    }

    /**
     * Notify listeners that the main state has changed.
     * The view will listen for this.
     */
    public void fireStateChanged() {
        support.firePropertyChange(STATE_CHANGED_EVENT, null, this.state);
    }

    /**
     * Notify listeners that a non-fatal error occurred (e.g., "Already Found").
     */
    public void fireError(String errorMessage) {
        support.firePropertyChange(ERROR_EVENT, null, errorMessage);
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        support.removePropertyChangeListener(listener);
    }
}