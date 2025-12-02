package view;

// Imports for the new architecture
import interface_adapter.connections.ConnectionsController;
import interface_adapter.connections.ConnectionsState;
import interface_adapter.connections.ConnectionsViewModel;
import interface_adapter.ViewManagerModel;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

// Standard Swing/AWT imports
import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The main UI for the Connections game.
 * MODIFIED: This is now a JPanel that listens to a ViewModel.
 * It implements PropertyChangeListener to react to state updates
 * from the ConnectionsPresenter.
 */
public class ConnectionsGameView extends JPanel implements PropertyChangeListener {

    // This view's name, to be used by the ViewManager
    public final String viewName = ConnectionsViewModel.VIEW_NAME;

    // The ViewModel and Controller
    private final ConnectionsViewModel viewModel;
    private ConnectionsController controller;
    private ViewManagerModel viewManagerModel;

    // --- UI Components ---
    private final JPanel gridPanel;
    private final JPanel solvedPanel;
    private final JLabel mistakesLabel;
    private final JButton submitButton;
    private final JButton deselectButton;

    // --- State Management ---
    private final List<JButton> selectedButtons = new ArrayList<>();

    // This map will store all 16 buttons, mapping their word (String) to the JButton
    private final Map<String, JButton> wordButtonMap = new HashMap<>();

    private final Color defaultButtonColor = new JButton().getBackground();
    private final Color selectedButtonColor = new Color(173, 216, 230); // Light Blue

    /**
     * CONSTRUCTOR (MODIFIED)
     * Takes the ViewModel and Controller.
     * No longer a JFrame, it's a JPanel.
     */
    public ConnectionsGameView(ConnectionsViewModel viewModel, ConnectionsController controller) {
        this.viewModel = viewModel;
        this.controller = controller;

        // Subscribe this view to the ViewModel
        this.viewModel.addPropertyChangeListener(this);

        // 1. Setup Main Panel (this)
        this.setLayout(new BorderLayout());

        //Heaer Pannel
        JPanel headerPanel = new JPanel(new BorderLayout());
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> {
            if (viewManagerModel != null) {
                // Reset game state for next play
                resetGameState();
                viewManagerModel.setState("logged in");
                viewManagerModel.firePropertyChange();
            }
        });
        leftPanel.add(backButton);
        headerPanel.add(leftPanel, BorderLayout.WEST);

        // 2. Setup UI Components
        solvedPanel = new JPanel();
        solvedPanel.setLayout(new BoxLayout(solvedPanel, BoxLayout.Y_AXIS));
        // Create a top panel that holds both header and solved panel
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.add(headerPanel);
        topPanel.add(solvedPanel);
        this.add(topPanel, BorderLayout.NORTH);

        gridPanel = new JPanel(new GridLayout(4, 4, 10, 10));
        gridPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        this.add(gridPanel, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel(new FlowLayout());
        mistakesLabel = new JLabel("Mistakes remaining: 4");
        submitButton = new JButton("Submit");
        deselectButton = new JButton("Deselect All");

        controlPanel.add(mistakesLabel);
        controlPanel.add(submitButton);
        controlPanel.add(deselectButton);
        this.add(controlPanel, BorderLayout.SOUTH);

        // 3. Add Event Listeners
        submitButton.addActionListener(e -> handleSubmit());
        deselectButton.addActionListener(e -> deselectAll());
    }

    public void setViewManagerModel(ViewManagerModel viewManagerModel) {
        this.viewManagerModel = viewManagerModel;
    }

    public void setController(ConnectionsController controller) {
        this.controller = controller;
    }

    /**
     * Handles the "Submit" button click.
     * This logic is unchanged: it just packages data for the controller.
     */
    private void handleSubmit() {
        if (selectedButtons.size() != 4) {
            // This is a view-only error, no need to call the controller
            JOptionPane.showMessageDialog(this, "Please select exactly 4 items.");
            return;
        }

        List<String> selectedWords = new ArrayList<>();
        for (JButton button : selectedButtons) {
            selectedWords.add(button.getActionCommand());
        }

        // Call the controller to execute the game logic
        if (controller != null) {
            controller.executeSubmit(selectedWords);
        }
    }

    /**
     * Clears all selected buttons. (Unchanged)
     */
    private void deselectAll() {
        for (JButton button : selectedButtons) {
            button.setBackground(defaultButtonColor);
        }
        selectedButtons.clear();
    }

    /**
     * Resets the game state to allow for a new game to be played.
     */
    private void resetGameState() {
        // Clear UI state
        deselectAll();  // This clears selectedButtons
        wordButtonMap.clear();
        solvedPanel.removeAll();
        gridPanel.removeAll();

        // Reset button states
        submitButton.setEnabled(true);
        deselectButton.setEnabled(true);
        mistakesLabel.setText("Mistakes remaining: 4");

        // Reset ViewModel state - ConnectionsState() initializes with proper defaults:
        // mistakesRemaining=4, empty lists, isWin/isGameOver=false
        viewModel.setState(new ConnectionsState());

        // Repaint
        this.revalidate();
        this.repaint();
    }

    /**
     * NEW: The core logic of the ViewModel pattern.
     * This method is called by the ViewModel when the state changes.
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(ConnectionsViewModel.STATE_CHANGED_EVENT)) {
            // --- MAIN STATE UPDATE ---
            // The Presenter has updated the ViewModel, now we update the UI
            ConnectionsState state = (ConnectionsState) evt.getNewValue();
            updateViewFromState(state);

        } else if (evt.getPropertyName().equals(ConnectionsViewModel.ERROR_EVENT)) {
            // --- NON-FATAL ERROR POPUP ---
            // The Presenter wants to show a popup (e.g., "Already Found")
            String errorMessage = (String) evt.getNewValue();
            JOptionPane.showMessageDialog(this, errorMessage);
        }
    }

    /**
     * NEW: Helper method to update all UI components based on the new state.
     */
    private void updateViewFromState(ConnectionsState state) {

        // 1. Create the buttons on the *first* load
        if (wordButtonMap.isEmpty() && !state.getCurrentWords().isEmpty()) {
            // This runs once when the game is loaded
            createWordButtons(state.getCurrentWords());
        }

        // 2. Update mistakes label
        mistakesLabel.setText("Mistakes remaining: " + state.getMistakesRemaining());

        // 3. Rebuild the solved panel
        solvedPanel.removeAll();
        for (Map.Entry<String, List<String>> entry : state.getSolvedCategories().entrySet()) {
            solvedPanel.add(createSolvedPanel(entry.getKey(), entry.getValue()));
        }

        // 4. Rebuild the grid panel with remaining words
        gridPanel.removeAll();
        selectedButtons.clear();
        for (String word : state.getCurrentWords()) {
            JButton button = wordButtonMap.get(word); // Get the button from our map
            if (button != null) {
                button.setBackground(defaultButtonColor); // Reset color
                gridPanel.add(button);
            }
        }

        // 5. Handle Win/Loss state
        if (state.isWin()) {
            showWin(); // Show popup
            submitButton.setEnabled(false); // Disable game
            deselectButton.setEnabled(false);
        } else if (state.isGameOver()) {
            showGameOver(state.getErrorMessage()); // Show popup
            submitButton.setEnabled(false); // Disable game
            deselectButton.setEnabled(false);
        }

        // 6. Repaint all panels
        this.revalidate();
        this.repaint();
    }

    /**
     * NEW: Helper method to create all 16 buttons ONCE
     * and store them in the wordButtonMap.
     */
    private void createWordButtons(List<String> allWords) {
        wordButtonMap.clear();
        for (String word : allWords) {
            String buttonText = "<html><center>" + word.replace("\n", "<br>") + "</center></html>";
            JButton button = new JButton(buttonText);
            // Respect globally configured UI font size; just make it bold
            button.setFont(button.getFont().deriveFont(Font.BOLD));
            button.setOpaque(true);
            button.setBorder(new LineBorder(Color.BLACK));
            button.setActionCommand(word); // Store raw word
            button.addActionListener(new ButtonClickListener());

            wordButtonMap.put(word, button); // Store in map
        }
    }

    /**
     * NEW: Helper method to create a single "solved" panel.
     */
    private JPanel createSolvedPanel(String categoryName, List<String> words) {
        JPanel panel = new JPanel(new GridLayout(1, 1));
        panel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        String text = "<html><b>" + categoryName + "</b><br>" + String.join(", ", words) + "</html>";
        JLabel solvedLabel = new JLabel(text);
        solvedLabel.setOpaque(true);
        solvedLabel.setBackground(new Color(240, 240, 160)); // Light Yellow
        solvedLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(solvedLabel);
        return panel;
    }

    // --- Popups (unchanged, but now private) ---
    private void showWin() {
        JOptionPane.showMessageDialog(this, "Congratulations! You found all categories!", "You Win!", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showGameOver(String message) {
        JOptionPane.showMessageDialog(this, message, "Game Over", JOptionPane.WARNING_MESSAGE);
    }

    /**
     * Internal listener for each of the 16 word buttons. (Unchanged)
     */
    private class ButtonClickListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JButton clickedButton = (JButton) e.getSource();

            if (selectedButtons.contains(clickedButton)) {
                selectedButtons.remove(clickedButton);
                clickedButton.setBackground(defaultButtonColor);
            } else if (selectedButtons.size() < 4) {
                selectedButtons.add(clickedButton);
                clickedButton.setBackground(selectedButtonColor);
            }
        }
    }
}