package view;

import interface_adapter.ConnectionsController; // You will create this next

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
 * It is a "dumb" view, meaning it has no game logic.
 * It listens for user input and calls the ConnectionsController.
 * It also has public methods for the Presenter to call to update the UI.
 */
public class ConnectionsGameView extends JFrame {

    private ConnectionsController controller;

    // --- UI Components ---
    private final JPanel gridPanel;
    private final JPanel solvedPanel;
    private final JLabel mistakesLabel;
    private final JButton submitButton;
    private final JButton deselectButton;

    // --- State Management ---
    private final List<JButton> selectedButtons = new ArrayList<>();
    private final Map<String, JButton> wordButtonMap = new HashMap<>();
    private final Color defaultButtonColor = new JButton().getBackground();
    private final Color selectedButtonColor = new Color(173, 216, 230); // Light Blue

    public ConnectionsGameView() {
        // 1. Setup Main Window
        setTitle("Connections");
        setSize(600, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // 2. Setup UI Components

        // Panel for solved categories (at the top)
        solvedPanel = new JPanel();
        solvedPanel.setLayout(new BoxLayout(solvedPanel, BoxLayout.Y_AXIS));
        add(solvedPanel, BorderLayout.NORTH);

        // Grid for game buttons (in the center)
        gridPanel = new JPanel(new GridLayout(4, 4, 10, 10));
        gridPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(gridPanel, BorderLayout.CENTER);

        // Panel for controls (at the bottom)
        JPanel controlPanel = new JPanel(new FlowLayout());
        mistakesLabel = new JLabel("Mistakes remaining: 4");
        submitButton = new JButton("Submit");
        deselectButton = new JButton("Deselect All");

        controlPanel.add(mistakesLabel);
        controlPanel.add(submitButton);
        controlPanel.add(deselectButton);
        add(controlPanel, BorderLayout.SOUTH);

        // 3. Add Event Listeners
        submitButton.addActionListener(e -> handleSubmit());
        deselectButton.addActionListener(e -> deselectAll());
    }

    /**
     * Sets the controller this view will communicate with.
     * This is called from the `app` (main) layer.
     */
    public void setController(ConnectionsController controller) {
        this.controller = controller;
    }

    /**
     * Handles the "Submit" button click.
     * It performs no logic, just packages data for the controller.
     */
    private void handleSubmit() {
        if (selectedButtons.size() != 4) {
            showError("Please select exactly 4 items.");
            return;
        }

        List<String> selectedWords = new ArrayList<>();
        for (JButton button : selectedButtons) {
            // "Word" is stored in the button's "action command"
            selectedWords.add(button.getActionCommand());
        }

        // Call the controller to execute the game logic
        if (controller != null) {
            controller.executeSubmit(selectedWords);
        }
    }

    /**
     * Clears all selected buttons.
     */
    private void deselectAll() {
        for (JButton button : selectedButtons) {
            button.setBackground(defaultButtonColor);
        }
        selectedButtons.clear();
    }

    // --- Public Methods for Presenter to Call ---

    /**
     * Creates the initial game board.
     * This is called by the Presenter.
     *
     * @param title    The game title.
     * @param allWords The list of 16 shuffled words.
     */
    public void displayBoard(String title, List<String> allWords) {
        setTitle(title);
        gridPanel.removeAll();
        wordButtonMap.clear();

        for (String word : allWords) {
            // Use HTML for multi-line button text
            String buttonText = "<html><center>" + word.replace("\n", "<br>") + "</center></html>";
            JButton button = new JButton(buttonText);
            button.setFont(new Font("Arial", Font.BOLD, 12));
            button.setOpaque(true);
            button.setBorder(new LineBorder(Color.BLACK));

            // Store the "raw" word string for logic
            button.setActionCommand(word);

            button.addActionListener(new ButtonClickListener());

            gridPanel.add(button);
            wordButtonMap.put(word, button);
        }
        gridPanel.revalidate();
        gridPanel.repaint();
    }

    /**
     * Shows a correctly guessed group.
     * This is called by the Presenter.
     *
     * @param categoryName   The name of the category.
     * @param words          The words in the category.
     * @param remainingWords The words left on the board.
     */
    public void showCorrectGroup(String categoryName, List<String> words, List<String> remainingWords) {
        // 1. Create the "solved" panel
        JPanel panel = new JPanel(new GridLayout(1, 1));
        panel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        String text = "<html><b>" + categoryName + "</b><br>" + String.join(", ", words) + "</html>";
        JLabel solvedLabel = new JLabel(text);
        solvedLabel.setOpaque(true);
        solvedLabel.setBackground(new Color(240, 240, 160)); // Light Yellow
        solvedLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(solvedLabel);

        solvedPanel.add(panel);

        // 2. Re-draw the grid with only the remaining words
        gridPanel.removeAll();
        selectedButtons.clear(); // Clear selection

        for (String word : remainingWords) {
            JButton button = wordButtonMap.get(word);
            button.setBackground(defaultButtonColor); // Reset color just in case
            gridPanel.add(button);
        }

        solvedPanel.revalidate();
        solvedPanel.repaint();
        gridPanel.revalidate();
        gridPanel.repaint();
    }

    /**
     * Updates the mistake counter label.
     * This is called by the Presenter.
     */
    public void updateMistakes(int mistakesRemaining) {
        mistakesLabel.setText("Mistakes remaining: " + mistakesRemaining);
    }

    /**
     * Shows a generic error message.
     * This is called by the Presenter.
     */
    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Shows the "already found" message.
     * This is called by the Presenter.
     */
    public void showAlreadyFound() {
        JOptionPane.showMessageDialog(this, "You already found that group.", "Already Found", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Shows the win message and disables the game.
     * This is called by the Presenter.
     */
    public void showWin() {
        JOptionPane.showMessageDialog(this, "Congratulations! You found all categories!", "You Win!", JOptionPane.INFORMATION_MESSAGE);
        submitButton.setEnabled(false);
        deselectButton.setEnabled(false);
        gridPanel.removeAll();
        gridPanel.revalidate();
        gridPanel.repaint();
    }

    /**
     * Shows the game over message and disables the game.
     * This is called by the Presenter.
     */
    public void showGameOver(String message) {
        JOptionPane.showMessageDialog(this, message, "Game Over", JOptionPane.WARNING_MESSAGE);
        submitButton.setEnabled(false);
        deselectButton.setEnabled(false);
    }

    /**
     * Internal listener for each of the 16 word buttons.
     */
    private class ButtonClickListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JButton clickedButton = (JButton) e.getSource();

            if (selectedButtons.contains(clickedButton)) {
                // Deselect
                selectedButtons.remove(clickedButton);
                clickedButton.setBackground(defaultButtonColor);
            } else if (selectedButtons.size() < 4) {
                // Select
                selectedButtons.add(clickedButton);
                clickedButton.setBackground(selectedButtonColor);
            }
            // If 4 are already selected, do nothing
        }
    }
}