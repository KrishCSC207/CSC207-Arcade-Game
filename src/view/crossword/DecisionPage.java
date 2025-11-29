package src.view.crossword;

import javax.swing.*;

public class DecisionPage extends JPanel {
    public DecisionPage() {
        JFrame frame = new JFrame("Wordsearch Page");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setResizable(true);
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.add(new JLabel("Please choose one of the follow for difficulties: "));
        buttonPanel.add(new JButton("Easy"));
        buttonPanel.add(new JButton("Medium"));
        buttonPanel.add(new JButton("Hard"));
        buttonPanel.add(new JButton("Return Home!"));
        frame.add(buttonPanel);
    }
}
