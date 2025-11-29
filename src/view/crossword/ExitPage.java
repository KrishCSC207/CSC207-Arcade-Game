package src.view.crossword;

import javax.swing.*;

public class ExitPage extends JPanel {
    public ExitPage() {
        JFrame frame = new JFrame("Wordsearch Page");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setResizable(true);
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.add(new JLabel("Thank you for Playing!"));
        buttonPanel.add(new JLabel("Your time was: ")); //to be implemented by partner
        buttonPanel.add(new JLabel("Awesome work!"));
        buttonPanel.add(new JButton("Please click to return home!"));
        frame.add(buttonPanel);
    }
}
