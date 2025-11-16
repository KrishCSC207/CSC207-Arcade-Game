package view.crossword;

import interface_adapters.crossword.CrosswordController;
import interface_adapters.crossword.CrosswordViewModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

public class CrosswordView extends JPanel implements PropertyChangeListener {

    private final CrosswordController controller;
    private final CrosswordViewModel viewModel;

    private final JLabel imageLabel;
    private final JPanel answersPanel;
    private final List<JTextField> answerFields;
    private final JButton answerButton;
    private final JLabel feedbackLabel;

    public CrosswordView(CrosswordController controller, CrosswordViewModel viewModel) {
        this.controller = controller;
        this.viewModel = viewModel;
        this.answerFields = new ArrayList<>();
        this.answerButton = new JButton("Submit!");
        this.feedbackLabel = new JLabel(" ");
        this.viewModel.addPropertyChangeListener(this);

        setLayout(new BorderLayout());

        // Image at the top/center
        imageLabel = new JLabel("", SwingConstants.CENTER);
        add(imageLabel, BorderLayout.CENTER);

        // Answers panel at the bottom
        answersPanel = new JPanel();
        answersPanel.setLayout(new BoxLayout(answersPanel, BoxLayout.Y_AXIS));
        add(answersPanel, BorderLayout.SOUTH);

        // load puzzle before build view
        controller.startCrossword();

        // Build UI once from ViewModel
        buildUIFromViewModel();
    }

    private void buildUIFromViewModel() {
        // load local image
        String imagePath = viewModel.getImagePath();
        if (imagePath != null && !imagePath.isEmpty()) {
            ImageIcon icon = new ImageIcon(imagePath);
            imageLabel.setIcon(icon);
        }

        // create text fields
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
        answersPanel.add(answerButton);
        answersPanel.add(feedbackLabel);
        answerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (e.getSource().equals(answerButton)) {
                    List<String> userAnswers = new ArrayList<>();
                    for (JTextField field : answerFields) {
                        userAnswers.add(field.getText());
                    }
                    controller.submitAnswers(userAnswers);
                }
            }
        });
        //Important to add here the second button to change the view back to home page!!!
    }

//    public List<String> getUserAnswers() {
//        List<String> answers = new ArrayList<>();
//        for (JTextField field : answerFields) {
//            answers.add(field.getText());
//        }
//        return answers;
//    }
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        // Check if the "feedback" property changed
        if ("feedbackMessage".equals(evt.getPropertyName())) {
            String newMessage = (String) evt.getNewValue();
            feedbackLabel.setText(newMessage);
        }
    }
}