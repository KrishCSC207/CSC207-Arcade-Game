package view.crossword;
// controller work has to be done, including any errors with the plugings
import interface_adapters.crossword.CrosswordController;
import interface_adapters.crossword.CrosswordViewModel;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;

public class easy_wordsearch extends JPanel implements java.beans.PropertyChangeListener {
    private final CrosswordController controller;
    private final CrosswordViewModel viewModel;
    private final JLabel imageLabel;
    private final JPanel answersPanel;
    private final List<JTextField> answerFields;
    private final JButton answerButton;
    private final JLabel feedbackLabel;

    public easy_wordsearch(CrosswordController controller, CrosswordViewModel viewModel) {
        this.controller = controller;
        this.viewModel = viewModel;
        this.viewModel.addPropertyChangeListener(this);

        this.answerFields = new ArrayList<>();
        this.answerButton = new JButton("Submit!");
        this.feedbackLabel = new JLabel(" ");
        setLayout(new BorderLayout());

        // top-left Back button header (add once)
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        JButton backBtn = new JButton("Back");
        backBtn.addActionListener(e -> {
            java.awt.Container c = this.getParent();
            while (c != null && !(c.getLayout() instanceof java.awt.CardLayout)) c = c.getParent();
            if (c != null) ((java.awt.CardLayout) c.getLayout()).show(c, "DECISION");
        });
        header.add(backBtn);
        add(header, java.awt.BorderLayout.NORTH);

        // Image at the top/center
        imageLabel = new JLabel("", SwingConstants.CENTER);
        add(imageLabel, BorderLayout.CENTER);

        // Answers panel at the bottom
        answersPanel = new JPanel();
        answersPanel.setLayout(new BoxLayout(answersPanel, BoxLayout.Y_AXIS));
        add(answersPanel, BorderLayout.SOUTH);

        // load puzzle before build view
        controller.startCrossword("EASY");

        // Build UI once from ViewModel
        buildUIFromViewModel();
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
            java.util.List<String> userAnswers = new java.util.ArrayList<>();
            for (JTextField field : answerFields) userAnswers.add(field.getText());
            controller.submitAnswers(userAnswers);
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
