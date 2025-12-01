
package view.crossword;

import interface_adapters.crossword.CrosswordViewModel;
import javax.swing.*;
import java.awt.*;

public class ExitPage extends JPanel implements java.beans.PropertyChangeListener {
    private final JLabel timeLabel;
    private final CrosswordViewModel viewModel;

    public ExitPage(CrosswordViewModel viewModel) {
        this.viewModel = viewModel;
        this.viewModel.addPropertyChangeListener(this);

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        JLabel titleLabel = new JLabel("Thank you for Playing!");
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(titleLabel);

        add(Box.createVerticalStrut(12));

        timeLabel = new JLabel("Your time was: 00:00");
        timeLabel.setFont(new Font("Monospaced", Font.BOLD, 18));
        timeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(timeLabel);

        add(Box.createVerticalStrut(12));

        JLabel awesomeLabel = new JLabel("Awesome work!");
        awesomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(awesomeLabel);
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