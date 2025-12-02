package view;

import interface_adapter.multiple_choice.QuizController;
import interface_adapter.multiple_choice.QuizViewModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * View that allows the user to select a quiz category.
 */
public class CategorySelectionView extends JFrame implements PropertyChangeListener {
    private final QuizViewModel quizViewModel;
    private QuizController quizController;

    private final JButton submit;
    private final JRadioButton[] options;
    private final String[] buttonTxt;

    public CategorySelectionView(QuizViewModel quizViewModel) {
        this.quizViewModel = quizViewModel;
        this.quizViewModel.addPropertyChangeListener(this);

        final JLabel title = new JLabel("choose a module", SwingConstants.CENTER);

        final JPanel choices = new JPanel();
        options = new JRadioButton[6];
        buttonTxt = new String[]{"Module 0", "Module 1", "Module 2", "Module 3", "Module 4", "Module 5"};
        ButtonGroup group = new ButtonGroup();
        for (int i = 0; i < options.length; i++) {
            options[i] = new JRadioButton(buttonTxt[i]);
            group.add(options[i]);
            choices.add(options[i]);
        }

        submit = new JButton("start");
        submit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                for (JRadioButton rb : options) {
                    if (rb.isSelected()) {
                        if (quizController != null) {
                            quizController.startQuiz(rb.getText());
                        }
                        return;
                    }
                }
                JOptionPane.showMessageDialog(
                        CategorySelectionView.this, "Please select a module.");
            }
        });

        setLayout(new BorderLayout());
        add(title, BorderLayout.NORTH);
        add(choices, BorderLayout.CENTER);
        JPanel bottom = new JPanel();
        bottom.add(submit);
        add(bottom, BorderLayout.SOUTH);

        setSize(420, 180);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // changed from EXIT_ON_CLOSE
    }

    public void setQuizController(QuizController quizController) {
        this.quizController = quizController;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
    }
}