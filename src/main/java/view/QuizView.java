package view;

import interface_adapter.multiple_choice.QuizController;
import interface_adapter.multiple_choice.QuizViewModel;
import interface_adapter.multiple_choice.ResultsViewModel;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.InputStream;

/**
 * Embedded QuizView: selection dialog (modal) -> quiz -> results.
 * Now a JPanel so it can be added to the app cardPanel.
 */
public class QuizView extends JPanel implements PropertyChangeListener {
    private final QuizViewModel quizViewModel;
    // NEW: optional results VM
    private final ResultsViewModel resultsViewModel;
    private QuizController quizController;

    // Cards
    private final JPanel cards;
    private final CardLayout cardLayout;

    // Quiz UI
    private final ScaledImagePanel imagePanel;
    private final JLabel progressLabel;
    private final JButton buttonA, buttonB, buttonC, buttonD;
    private final Color defaultColor;
    private Timer autoAdvanceTimer;

    // Results UI
    private final JLabel accuracyLabel;
    private final JLabel timeLabel;

    public QuizView(QuizViewModel quizViewModel) {
        this(quizViewModel, null);
    }

    public QuizView(QuizViewModel quizViewModel, ResultsViewModel resultsViewModel) {
        super(new BorderLayout());
        this.quizViewModel = quizViewModel;
        this.resultsViewModel = resultsViewModel;

        // Listen to both VMs as appropriate
        this.quizViewModel.addPropertyChangeListener(this);
        if (this.resultsViewModel != null) {
            this.resultsViewModel.addPropertyChangeListener(this);
        }

        cardLayout = new CardLayout();
        cards = new JPanel(cardLayout);

        // --- Quiz Card (main) ---
        JPanel quizCard = new JPanel(new BorderLayout(10, 10));

        // Top: toolbar with Select Module button and progress label
        JPanel topBar = new JPanel(new BorderLayout());
        JButton selectModuleBtn = new JButton("Select Module");
        selectModuleBtn.addActionListener(e -> openSelectionDialog());
        topBar.add(selectModuleBtn, BorderLayout.WEST);

        progressLabel = new JLabel("Question 0/0", SwingConstants.CENTER);
        progressLabel.setFont(new Font("Arial", Font.BOLD, 16));
        topBar.add(progressLabel, BorderLayout.CENTER);

        quizCard.add(topBar, BorderLayout.NORTH);

        // Center: image panel
        imagePanel = new ScaledImagePanel();
        quizCard.add(imagePanel, BorderLayout.CENTER);

        // Bottom: answer buttons
        JPanel bottomPanel = new JPanel(new BorderLayout());
        JPanel buttonPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        buttonA = new JButton("A");
        buttonB = new JButton("B");
        buttonC = new JButton("C");
        buttonD = new JButton("D");
        Font buttonFont = new Font("Arial", Font.BOLD, 24);
        buttonA.setFont(buttonFont);
        buttonB.setFont(buttonFont);
        buttonC.setFont(buttonFont);
        buttonD.setFont(buttonFont);

        buttonA.addActionListener(e -> {
            if (quizController != null) quizController.submitAnswer("A");
        });
        buttonB.addActionListener(e -> {
            if (quizController != null) quizController.submitAnswer("B");
        });
        buttonC.addActionListener(e -> {
            if (quizController != null) quizController.submitAnswer("C");
        });
        buttonD.addActionListener(e -> {
            if (quizController != null) quizController.submitAnswer("D");
        });

        buttonPanel.add(buttonA);
        buttonPanel.add(buttonB);
        buttonPanel.add(buttonC);
        buttonPanel.add(buttonD);

        bottomPanel.add(buttonPanel, BorderLayout.CENTER);
        quizCard.add(bottomPanel, BorderLayout.SOUTH);

        defaultColor = buttonA.getBackground();

        // --- Results Card ---
        JPanel resultsCard = new JPanel(new GridLayout(4, 1, 10, 10));
        resultsCard.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        JLabel resTitle = new JLabel("Quiz Complete!", SwingConstants.CENTER);
        resTitle.setFont(new Font("Arial", Font.BOLD, 28));
        accuracyLabel = new JLabel("Accuracy: 0%", SwingConstants.CENTER);
        accuracyLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        timeLabel = new JLabel("Time: 0s", SwingConstants.CENTER);
        timeLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        JButton finishButton = new JButton("Finish");
        finishButton.setFont(new Font("Arial", Font.BOLD, 16));
        finishButton.addActionListener(e -> {
            // When finished, return to logged-in home by firing a property change
            // The container (AppBuilder) controls the main cards; simply show results card here
            // Optionally caller may switch back to logged in state.
            // For now, hide results by switching to QUIZ card or let controller handle navigation.
        });

        resultsCard.add(resTitle);
        resultsCard.add(accuracyLabel);
        resultsCard.add(timeLabel);
        JPanel finishPanel = new JPanel();
        finishPanel.add(finishButton);
        resultsCard.add(finishPanel);

        // Add cards
        cards.add(quizCard, "QUIZ");
        cards.add(resultsCard, "RESULTS");

        add(cards, BorderLayout.CENTER);
        cardLayout.show(cards, "QUIZ");
    }

    public void setQuizController(QuizController quizController) {
        this.quizController = quizController;
    }

    // Modal selection dialog: appears when pressing Select Module button or when showWithSelection() called
    private void openSelectionDialog() {
        JDialog dlg = new JDialog(SwingUtilities.getWindowAncestor(this), "Choose a module", Dialog.ModalityType.APPLICATION_MODAL);
        dlg.setLayout(new BorderLayout(8, 8));
        dlg.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JLabel title = new JLabel("Choose a module", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        dlg.add(title, BorderLayout.NORTH);

        JPanel choices = new JPanel();
        final JRadioButton[] options = new JRadioButton[6];
        final String[] buttonTxt = new String[]{"Module 0", "Module 1", "Module 2", "Module 3", "Module 4", "Module 5"};
        ButtonGroup group = new ButtonGroup();
        for (int i = 0; i < options.length; i++) {
            options[i] = new JRadioButton(buttonTxt[i]);
            group.add(options[i]);
            choices.add(options[i]);
        }
        dlg.add(choices, BorderLayout.CENTER);

        JButton startBtn = new JButton("Start");
        startBtn.addActionListener((ActionEvent evt) -> {
            for (JRadioButton rb : options) {
                if (rb.isSelected()) {
                    if (quizController != null) {
                        quizController.startQuiz(rb.getText());
                    }
                    // show quiz card and close dialog
                    SwingUtilities.invokeLater(() -> {
                        cardLayout.show(cards, "QUIZ");
                        dlg.dispose();
                    });
                    return;
                }
            }
            JOptionPane.showMessageDialog(this, "Please select a module.");
        });

        JPanel bottom = new JPanel();
        bottom.add(startBtn);
        dlg.add(bottom, BorderLayout.SOUTH);

        dlg.pack();
        dlg.setSize(420, 220);
        dlg.setLocationRelativeTo(SwingUtilities.getWindowAncestor(this));
        dlg.setVisible(true);
    }

    // Public helper to ensure the panel is visible and open the selection dialog.
    // AppBuilder should set the ViewManager state to "multipleChoice" before calling this.
    public void showWithSelection() {
        SwingUtilities.invokeLater(() -> openSelectionDialog());
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String name = evt.getPropertyName();
        Object src = evt.getSource();

        SwingUtilities.invokeLater(() -> {
            // Quiz-related properties come from quizViewModel
            if (src == quizViewModel) {
                switch (name) {
                    case "imagePath": {
                        String imagePath = quizViewModel.getCurrentImagePath();
                        if (imagePath != null) {
                            imagePanel.setImage(imagePath);
                        } else {
                            imagePanel.clearImage();
                        }
                        break;
                    }

                    case "progressLabel": {
                        String label = quizViewModel.getQuestionProgressLabel();
                        if (label != null) progressLabel.setText(label);
                        break;
                    }

                    case "feedbackState": {
                        handleFeedbackState();
                        break;
                    }

                    default:
                        break;
                }
                return;
            }

            // Results-related properties come from resultsViewModel (if provided)
            if (resultsViewModel != null && src == resultsViewModel) {
                switch (name) {
                    case "accuracy": {
                        double accuracy = resultsViewModel.getAccuracy();
                        int percentage = (int) (accuracy * 100);
                        accuracyLabel.setText(String.format("Accuracy: %d%%", percentage));
                        // show results card when accuracy available
                        cardLayout.show(cards, "RESULTS");
                        break;
                    }

                    case "totalTimeMs": {
                        long timeMs = resultsViewModel.getTotalTimeMs();
                        timeLabel.setText(String.format("Time: %.1fs", timeMs / 1000.0));
                        break;
                    }

                    default:
                        break;
                }
            }
        });
    }

    private void handleFeedbackState() {
        String state = quizViewModel.getFeedbackState();

        if ("INCORRECT".equals(state)) {
            resetButtonColors();
            String incorrectButton = quizViewModel.getIncorrectButton();
            if (incorrectButton != null) setButtonColor(incorrectButton, Color.RED);
        } else if ("CORRECT".equals(state)) {
            resetButtonColors();
            String selected = quizViewModel.getIncorrectButton(); // VM uses this field to carry selected answer
            if (selected != null) setButtonColor(selected, Color.GREEN);

            if (autoAdvanceTimer != null) autoAdvanceTimer.stop();
            autoAdvanceTimer = new Timer(1000, e -> {
                if (quizController != null) quizController.nextQuestion();
                autoAdvanceTimer.stop();
            });
            autoAdvanceTimer.setRepeats(false);
            autoAdvanceTimer.start();
        } else if ("NONE".equals(state)) {
            resetButtonColors();
        }
    }

    private void setButtonColor(String button, Color color) {
        switch (button) {
            case "A": buttonA.setBackground(color); break;
            case "B": buttonB.setBackground(color); break;
            case "C": buttonC.setBackground(color); break;
            case "D": buttonD.setBackground(color); break;
        }
    }

    private void resetButtonColors() {
        buttonA.setBackground(defaultColor);
        buttonB.setBackground(defaultColor);
        buttonC.setBackground(defaultColor);
        buttonD.setBackground(defaultColor);
    }

    // Minimal embedded scaled image panel to avoid external dependency
    private static class ScaledImagePanel extends JPanel {
        private BufferedImage image;

        ScaledImagePanel() {
            setPreferredSize(new Dimension(700, 420));
            setBackground(Color.WHITE);
        }

        void setImage(String imagePath) {
            try {
                InputStream is = getClass().getClassLoader().getResourceAsStream(imagePath);
                if (is != null) {
                    this.image = ImageIO.read(is);
                    is.close();
                } else {
                    System.err.println("Image not found: " + imagePath);
                    this.image = null;
                }
            } catch (Exception e) {
                System.err.println("Error loading image: " + e.getMessage());
                this.image = null;
            }
            repaint();
        }

        void clearImage() {
            this.image = null;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (image != null) {
                g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
            } else {
                g.setColor(Color.GRAY);
                String text = "No Image Available";
                FontMetrics fm = g.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(text)) / 2;
                int y = getHeight() / 2;
                g.drawString(text, x, y);
            }
        }
    }
}
