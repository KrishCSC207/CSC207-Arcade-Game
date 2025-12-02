package view;

import interface_adapter.multiple_choice.ResultsViewModel;
import interface_adapter.ViewManagerModel;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * View that displays the final quiz results.
 */
public class ResultsView extends JPanel implements PropertyChangeListener {
    private final String viewName = "results";
    private final ResultsViewModel viewModel;
    private final ViewManagerModel viewManagerModel;
    private final JLabel accuracyLabel;
    private final JLabel timeLabel;

    public ResultsView(ResultsViewModel viewModel, ViewManagerModel viewManagerModel) {
        this.viewModel = viewModel;
        this.viewManagerModel = viewManagerModel;
        viewModel.addPropertyChangeListener(this);

        setLayout(new BorderLayout());

        JPanel panel = new JPanel(new GridLayout(4, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        JLabel titleLabel = new JLabel("Quiz Complete!", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));

        accuracyLabel = new JLabel("Accuracy: 0%", SwingConstants.CENTER);
        accuracyLabel.setFont(new Font("Arial", Font.PLAIN, 24));

        timeLabel = new JLabel("Time: 0s", SwingConstants.CENTER);
        timeLabel.setFont(new Font("Arial", Font.PLAIN, 24));

        JButton finishButton = new JButton("Back to Menu");
        finishButton.setFont(new Font("Arial", Font.BOLD, 18));
        finishButton.addActionListener(e -> {
            viewManagerModel.setState("logged in");
            viewManagerModel.firePropertyChange();
        });

        panel.add(titleLabel);
        panel.add(accuracyLabel);
        panel.add(timeLabel);
        panel.add(finishButton);

        add(panel, BorderLayout.CENTER);
    }

    public String getViewName() {
        return viewName;
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
}
