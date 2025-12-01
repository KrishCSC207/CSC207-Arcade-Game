package com.csc207.arcade.multiplechoice.view;

import com.csc207.arcade.multiplechoice.interface_adapter.ResultsViewModel;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * View that displays the final quiz results.
 */
public class ResultsView extends JFrame implements PropertyChangeListener {
    private final ResultsViewModel viewModel;
    private final JLabel accuracyLabel;
    private final JLabel timeLabel;

    public ResultsView(ResultsViewModel viewModel) {
        this.viewModel = viewModel;
        viewModel.addPropertyChangeListener(this);
        
        setTitle("Quiz Results");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        
        JPanel panel = new JPanel(new GridLayout(4, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
        
        JLabel titleLabel = new JLabel("Quiz Complete!", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        
        accuracyLabel = new JLabel("Accuracy: 0%", SwingConstants.CENTER);
        accuracyLabel.setFont(new Font("Arial", Font.PLAIN, 24));
        
        timeLabel = new JLabel("Time: 0s", SwingConstants.CENTER);
        timeLabel.setFont(new Font("Arial", Font.PLAIN, 24));
        
        // Add Finish button
        JButton finishButton = new JButton("Finish");
        finishButton.setFont(new Font("Arial", Font.BOLD, 18));
        finishButton.addActionListener(e -> {
            dispose(); // Close the window
            System.exit(0); // Exit the application
        });
        
        panel.add(titleLabel);
        panel.add(accuracyLabel);
        panel.add(timeLabel);
        panel.add(finishButton);
        
        add(panel, BorderLayout.CENTER);
        
        setSize(400, 350);
        setLocationRelativeTo(null);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("accuracy".equals(evt.getPropertyName())) {
            double accuracy = viewModel.getAccuracy();
            int percentage = (int) (accuracy * 100);
            accuracyLabel.setText(String.format("Accuracy: %d%%", percentage));
        } else if ("totalTimeMs".equals(evt.getPropertyName())) {
            long timeMs = viewModel.getTotalTimeMs();
            double timeSec = timeMs / 1000.0;
            timeLabel.setText(String.format("Time: %.1fs", timeSec));
        }
    }

//    public void show() {
//        setVisible(true);
//    }
}
