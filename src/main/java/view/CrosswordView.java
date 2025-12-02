package view;

import interface_adapter.ViewManagerModel;
import interface_adapter.crossword.CrosswordController;
import interface_adapter.crossword.CrosswordViewModel;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Unified crossword views container:
 * - DecisionPanel: choose difficulty (buttons call controller.startCrossword and run provided Runnables)
 * - PuzzlePanel: parameterized by difficulty; contains image, answer fields, timer, submit/back handling
 * - ExitPanel: shows elapsed time when puzzle completes
 */
public class CrosswordView {

    // Factory for Decision UI (keeps the original DecisionPage in place)
    public static JPanel createDecisionPanel(ViewManagerModel viewManagerModel,
                                             CrosswordController controller,
                                             CrosswordViewModel viewModel,
                                             Runnable showEasy,
                                             Runnable showMedium,
                                             Runnable showHard) {
        return new DecisionPanel(viewManagerModel, controller, viewModel, showEasy, showMedium, showHard);
    }

    // Factory for Puzzle UI for a difficulty (keeps original easy/medium/hard constructors compatible)
    public static JPanel createPuzzlePanel(CrosswordController controller,
                                           CrosswordViewModel viewModel,
                                           String difficulty) {
        return new PuzzlePanel(controller, viewModel, difficulty);
    }

    // Factory for Exit UI (keeps original ExitPage constructor compatible)
    public static JPanel createExitPanel(CrosswordViewModel viewModel) {
        return new ExitPanel(viewModel);
    }

    // ----------------------- Implementation details -----------------------

    private static class DecisionPanel extends JPanel {
        DecisionPanel(ViewManagerModel viewManagerModel,
                      CrosswordController controller,
                      CrosswordViewModel viewModel,
                      Runnable showEasy,
                      Runnable showMedium,
                      Runnable showHard) {

            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

            // top header with Back button (left)
            JPanel header = new JPanel(new BorderLayout());
            JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
            JButton backBtn = new JButton("Back");
            backBtn.addActionListener(e -> {
                viewManagerModel.setState("logged in");
                viewManagerModel.firePropertyChange();
            });
            leftPanel.add(backBtn);
            header.add(leftPanel, BorderLayout.WEST);
            add(header);

            add(new JLabel("Please choose one of the following difficulties:"));
            add(Box.createVerticalStrut(12));

            JButton easyBtn   = new JButton("Easy");
            JButton mediumBtn = new JButton("Medium");
            JButton hardBtn   = new JButton("Hard");

            easyBtn.addActionListener(e -> { controller.startCrossword("EASY");     showEasy.run(); });
            mediumBtn.addActionListener(e -> { controller.startCrossword("MEDIUM"); showMedium.run(); });
            hardBtn.addActionListener(e -> { controller.startCrossword("HARD");     showHard.run(); });

            add(easyBtn);
            add(Box.createVerticalStrut(8));
            add(mediumBtn);
            add(Box.createVerticalStrut(8));
            add(hardBtn);
        }
    }

    private static class PuzzlePanel extends JPanel implements java.beans.PropertyChangeListener {
        private final CrosswordController controller;
        private final CrosswordViewModel viewModel;
        private final JLabel imageLabel;
        private final JPanel answersPanel;
        private final List<JTextField> answerFields;
        private final JButton answerButton;
        private final JLabel feedbackLabel;
        private final JLabel timerLabel;
        private Timer timer;
        private final String difficulty;

        PuzzlePanel(CrosswordController controller, CrosswordViewModel viewModel, String difficulty) {
            this.controller = controller;
            this.viewModel = viewModel;
            this.difficulty = difficulty;
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
                Container c = this.getParent();
                while (c != null && !(c.getLayout() instanceof CardLayout)) c = c.getParent();
                if (c != null) ((CardLayout) c.getLayout()).show(c, "DECISION");
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
            controller.startCrossword(difficulty);

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
                Container c = this.getParent();
                while (c != null && !(c.getLayout() instanceof CardLayout)) c = c.getParent();
                if (c != null) ((CardLayout) c.getLayout()).show(c, "EXIT");
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

    private static class ExitPanel extends JPanel implements java.beans.PropertyChangeListener {
        private final JLabel timeLabel;
        private final CrosswordViewModel viewModel;

        ExitPanel(CrosswordViewModel viewModel) {
            this.viewModel = viewModel;
            this.viewModel.addPropertyChangeListener(this);
            setLayout(new BorderLayout());

            JButton backBtn = new JButton("Back");
            backBtn.addActionListener(e -> {
                Container c = this.getParent();
                while (c != null && !(c.getLayout() instanceof CardLayout)) {
                    c = c.getParent();
                }
                if (c != null) {
                    ((CardLayout) c.getLayout()).show(c, "DECISION");
                }
            });

            JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            topPanel.add(backBtn);
            add(topPanel, BorderLayout.NORTH);

            JPanel centerPanel = new JPanel();
            centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

            JLabel titleLabel = new JLabel("Thank you for Playing!");
            titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            timeLabel = new JLabel("Your time was: 00:00");
            timeLabel.setFont(new Font("Monospaced", Font.BOLD, 18));
            timeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel awesomeLabel = new JLabel("Awesome work!");
            awesomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            centerPanel.add(Box.createVerticalStrut(12));
            centerPanel.add(titleLabel);
            centerPanel.add(Box.createVerticalStrut(12));
            centerPanel.add(timeLabel);
            centerPanel.add(Box.createVerticalStrut(12));
            centerPanel.add(awesomeLabel);

            add(centerPanel, BorderLayout.CENTER);

        }

        @Override
        public void propertyChange(java.beans.PropertyChangeEvent evt) {
            if ("elapsedTime".equals(evt.getPropertyName())) {
                long elapsed = viewModel.getElapsedTime();
                long seconds = (elapsed / 1000) % 60;
                long minutes = (elapsed / 1000) / 60;
                timeLabel.setText(String.format("Your time was: %02d:%02d", minutes, seconds));
            }
        }
    }
}

