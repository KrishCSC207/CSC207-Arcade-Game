package view;

import interfaceadapter.multiplechoice.QuizController;
import interfaceadapter.multiplechoice.QuizViewModel;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Main quiz view that displays questions and answer buttons.
 */
public class QuizView extends JPanel implements PropertyChangeListener {
    private final String viewName = "quiz";
    private final QuizController controller;
    private final QuizViewModel viewModel;

    private final ScaledImagePanel imagePanel;
    private final JLabel progressLabel;
    private final JButton buttonA;
    private final JButton buttonB;
    private final JButton buttonC;
    private final JButton buttonD;

    private final Color defaultColor;
    private Timer autoAdvanceTimer;

    public QuizView(QuizController controller, QuizViewModel viewModel) {
        this.controller = controller;
        this.viewModel = viewModel;

        viewModel.addPropertyChangeListener(this);

        setLayout(new BorderLayout());

        imagePanel = new ScaledImagePanel();
        add(imagePanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());

        progressLabel = new JLabel("Question 0/0", SwingConstants.CENTER);
        progressLabel.setFont(new Font("Arial", Font.BOLD, 16));
        bottomPanel.add(progressLabel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        buttonA = new JButton("A");
        buttonB = new JButton("B");
        buttonC = new JButton("C");
        buttonD = new JButton("D");

        defaultColor = buttonA.getBackground();

        Font buttonFont = new Font("Arial", Font.BOLD, 24);
        buttonA.setFont(buttonFont);
        buttonB.setFont(buttonFont);
        buttonC.setFont(buttonFont);
        buttonD.setFont(buttonFont);

        buttonA.addActionListener(e -> controller.submitAnswer("A"));
        buttonB.addActionListener(e -> controller.submitAnswer("B"));
        buttonC.addActionListener(e -> controller.submitAnswer("C"));
        buttonD.addActionListener(e -> controller.submitAnswer("D"));

        buttonPanel.add(buttonA);
        buttonPanel.add(buttonB);
        buttonPanel.add(buttonC);
        buttonPanel.add(buttonD);

        bottomPanel.add(buttonPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    public String getViewName() {
        return viewName;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String propertyName = evt.getPropertyName();

        switch (propertyName) {
            case "imagePath":
                String imagePath = viewModel.getCurrentImagePath();
                if (imagePath != null) {
                    imagePanel.setImage(imagePath);
                }
                break;

            case "progressLabel":
                String label = viewModel.getQuestionProgressLabel();
                if (label != null) {
                    progressLabel.setText(label);
                }
                break;

            case "feedbackState":
                handleFeedbackState();
                break;
        }
    }

    private void handleFeedbackState() {
        String state = viewModel.getFeedbackState();

        if ("INCORRECT".equals(state)) {
            resetButtonColors();
            String selectedButton = viewModel.getSelectedButton();
            if (selectedButton != null) {
                setButtonColor(selectedButton, Color.RED);
            }
        } else if ("CORRECT".equals(state)) {
            resetButtonColors();
            String selectedButton = viewModel.getSelectedButton();
            if (selectedButton != null) {
                setButtonColor(selectedButton, Color.GREEN);
            }

            if (autoAdvanceTimer != null) {
                autoAdvanceTimer.stop();
            }
            autoAdvanceTimer = new Timer(1000, e -> {
                controller.nextQuestion();
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
            case "A":
                buttonA.setBackground(color);
                break;
            case "B":
                buttonB.setBackground(color);
                break;
            case "C":
                buttonC.setBackground(color);
                break;
            case "D":
                buttonD.setBackground(color);
                break;
        }
    }

    private void resetButtonColors() {
        buttonA.setBackground(defaultColor);
        buttonB.setBackground(defaultColor);
        buttonC.setBackground(defaultColor);
        buttonD.setBackground(defaultColor);
    }
}
