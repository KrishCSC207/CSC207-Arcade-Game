package view.crossword;

import java.awt.*;
import javax.swing.*;


public class ExitPage extends JPanel {
    public ExitPage() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        add(new JLabel("Thank you for Playing!"));
        add(Box.createVerticalStrut(12));
        add(new JLabel("Your time was: ")); // partner will set this later
        add(Box.createVerticalStrut(12));
        add(new JLabel("Awesome work!"));

        // Optional: a button to go back to Decision later
        // JButton home = new JButton("Return to Home");
        // add(Box.createVerticalStrut(16));
        // add(home);
    }
}
