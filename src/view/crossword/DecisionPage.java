package view.crossword;

import interface_adapters.crossword.CrosswordController;
import interface_adapters.crossword.CrosswordViewModel;

import javax.swing.*;
import java.awt.*;

public class DecisionPage extends JPanel {
    public DecisionPage(CrosswordController controller,
                        CrosswordViewModel viewModel,
                        Runnable showEasy,
                        Runnable showMedium,
                        Runnable showHard) {

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

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

