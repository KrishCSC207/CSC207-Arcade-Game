package com.csc207.arcade.multiplechoice.view;

import com.csc207.arcade.multiplechoice.interface_adapter.QuizController;
import com.csc207.arcade.multiplechoice.interface_adapter.QuizViewModel;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Main quiz view that displays questions and answer buttons.
 */
public class QuizView extends JFrame implements PropertyChangeListener {
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
        
        // Listen to view model changes
        viewModel.addPropertyChangeListener(this);
        
        // Set up frame
        setTitle("Multiple Choice Quiz");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // Top panel: Image
        imagePanel = new ScaledImagePanel();
        add(imagePanel, BorderLayout.CENTER);
        
        // Bottom panel: Buttons and progress
        JPanel bottomPanel = new JPanel(new BorderLayout());
        
        // Progress label
        progressLabel = new JLabel("Question 0/0", SwingConstants.CENTER);
        progressLabel.setFont(new Font("Arial", Font.BOLD, 16));
        bottomPanel.add(progressLabel, BorderLayout.NORTH);
        
        // Button panel with GridLayout
        JPanel buttonPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        buttonA = new JButton("A");
        buttonB = new JButton("B");
        buttonC = new JButton("C");
        buttonD = new JButton("D");
        
        // Store default button color
        defaultColor = buttonA.getBackground();
        
        // Make buttons bigger
        Font buttonFont = new Font("Arial", Font.BOLD, 24);
        buttonA.setFont(buttonFont);
        buttonB.setFont(buttonFont);
        buttonC.setFont(buttonFont);
        buttonD.setFont(buttonFont);
        
        // Add action listeners
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
        
        pack();
        setSize(800, 600);
        setLocationRelativeTo(null);
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
            // Reset colors first, then highlight the incorrect button in red immediately
            resetButtonColors();
            String incorrectButton = viewModel.getIncorrectButton();
            if (incorrectButton != null) {
                setButtonColor(incorrectButton, Color.RED);
            }
        } else if ("CORRECT".equals(state)) {
            // Reset colors first, then highlight the correct button in green
            resetButtonColors();
            String correctButton = viewModel.getIncorrectButton(); // This now contains the selected answer
            if (correctButton != null) {
                setButtonColor(correctButton, Color.GREEN);
            }
            
            // Auto-advance after a 1-second delay to show the green color
            if (autoAdvanceTimer != null) {
                autoAdvanceTimer.stop();
            }
            autoAdvanceTimer = new Timer(1000, e -> {
                // Advance to next question after showing the green highlight
                controller.advanceToNextQuestion();
                autoAdvanceTimer.stop();
            });
            autoAdvanceTimer.setRepeats(false);
            autoAdvanceTimer.start();
        } else if ("NONE".equals(state)) {
            // Reset button colors when moving to a new question
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
