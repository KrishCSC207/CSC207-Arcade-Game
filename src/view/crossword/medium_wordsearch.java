package view.crossword;

import interface_adapters.crossword.CrosswordController;
import interface_adapters.crossword.CrosswordViewModel;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class medium_wordsearch extends JPanel implements java.beans.PropertyChangeListener {
    private final CrosswordController controller;
    private final CrosswordViewModel viewModel;
    private final JLabel imageLabel;
    private final JPanel answersPanel;
    private final List<JTextField> answerFields;
    private final JButton answerButton;
    private final JLabel feedbackLabel;
    private final JLabel timerLabel;
    private Timer timer;

    public medium_wordsearch(CrosswordController controller, CrosswordViewModel viewModel) {
        this.controller = controller;
        this.viewModel = viewModel;
        this.viewModel.addPropertyChangeListener(this);

        this.answerFields = new ArrayList<>();
        this.answerButton = new JButton("Submit!");
        this.feedbackLabel = new JLabel(" ");
        this.timerLabel = new JLabel("Time: 00:00");
        setLayout(new BorderLayout());

        // top header with Back button (left) and timer (right)
        JPanel header = new JPanel(new BorderLayout());

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        JButton backBtn = new JButton("Back");
        backBtn.addActionListener(e -> {
            stopTimer();
            java.awt.Container c = this.getParent();
            while (c != null && !(c.getLayout() instanceof java.awt.CardLayout)) c = c.getParent();
            if (c != null) ((java.awt.CardLayout) c.getLayout()).show(c, "DECISION");
        });
        leftPanel.add(backBtn);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        timerLabel.setFont(new Font("Monospaced", Font.BOLD, 16));
        rightPanel.add(timerLabel);

        header.add(leftPanel, BorderLayout.WEST);
        header.add(rightPanel, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // Image at the center
        imageLabel = new JLabel("", SwingConstants.CENTER);
        add(imageLabel, BorderLayout.CENTER);

        // Answers panel at the bottom
        answersPanel = new JPanel();
        answersPanel.setLayout(new BoxLayout(answersPanel, BoxLayout.Y_AXIS));
        add(answersPanel, BorderLayout.SOUTH);

        // load puzzle before build view
        controller.startCrossword("MEDIUM");

        // Build UI once from ViewModel
        buildUIFromViewModel();

        // Start timer
        startTimer();
    }

    private void startTimer() {
        stopTimer();
        timer = new Timer(1000, e -> updateTimerDisplay());
        timer.start();
    }

    private void stopTimer() {
        if (timer != null) {
            timer.stop();
            timer = null;
        }
    }

    private void updateTimerDisplay() {
        long elapsed = System.currentTimeMillis() - viewModel.getStartTime();
        long seconds = (elapsed / 1000) % 60;
        long minutes = (elapsed / 1000) / 60;
        timerLabel.setText(String.format("Time: %02d:%02d", minutes, seconds));
    }

    private void buildUIFromViewModel() {
        // refresh image
        String imagePath = viewModel.getImagePath();
        imageLabel.setIcon((imagePath == null || imagePath.isEmpty()) ? null : new ImageIcon(imagePath));

        // clear & rebuild fields
        answersPanel.removeAll();
        answerFields.clear();

        int count = viewModel.getNumSolutions();
        for (int i = 0; i < count; i++) {
            JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JLabel label = new JLabel("Answer " + (i + 1) + ": ");
            JTextField tf = new JTextField(20);
            row.add(label);
            row.add(tf);
            answersPanel.add(row);
            answerFields.add(tf);
        }

        // de-duplicate any old listeners on the button
        for (java.awt.event.ActionListener al : answerButton.getActionListeners()) {
            answerButton.removeActionListener(al);
        }
        answerButton.addActionListener(e -> {
            List<String> userAnswers = new ArrayList<>();
            for (JTextField field : answerFields) userAnswers.add(field.getText());
            controller.submitAnswers(userAnswers, viewModel.getStartTime());
        });

        answersPanel.add(answerButton);
        answersPanel.add(feedbackLabel);

        answersPanel.revalidate();
        answersPanel.repaint();
    }

    @Override
    public void propertyChange(java.beans.PropertyChangeEvent evt) {
        String name = evt.getPropertyName();

        if ("completed".equals(name) && Boolean.TRUE.equals(evt.getNewValue())) {
            stopTimer();
            java.awt.Container c = this.getParent();
            while (c != null && !(c.getLayout() instanceof java.awt.CardLayout)) c = c.getParent();
            if (c != null) ((java.awt.CardLayout) c.getLayout()).show(c, "EXIT");
            return;
        }

        if ("feedbackMessage".equals(name)) {
            feedbackLabel.setText((String) evt.getNewValue());
            return;
        }

        // When Start UC sets the new puzzle data, rebuild from the ViewModel
        if ("imagePath".equals(name) || "numSolutions".equals(name)) {
            buildUIFromViewModel();
        }
    }
}
