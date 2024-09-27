import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ProbabilisticEOQCalculator extends JFrame {
    private JTextField demandField, setupCostField, holdingCostField, shortageCostField, minDemandField, maxDemandField;
    private JTextArea resultArea;
    private JButton calculateButton, clearButton;

    public ProbabilisticEOQCalculator() {
        setTitle("Probabilistic EOQ Calculator");
        setSize(450, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JPanel inputPanel = new JPanel(new GridLayout(6, 2, 5, 5));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        inputPanel.add(new JLabel("Demand (D):"));
        demandField = new JTextField();
        inputPanel.add(demandField);
        inputPanel.add(new JLabel("Setup Cost (K):"));
        setupCostField = new JTextField();
        inputPanel.add(setupCostField);
        inputPanel.add(new JLabel("Holding Cost (h):"));
        holdingCostField = new JTextField();
        inputPanel.add(holdingCostField);
        inputPanel.add(new JLabel("Shortage Cost (p):"));
        shortageCostField = new JTextField();
        inputPanel.add(shortageCostField);
        inputPanel.add(new JLabel("Min Demand:"));
        minDemandField = new JTextField();
        inputPanel.add(minDemandField);
        inputPanel.add(new JLabel("Max Demand:"));
        maxDemandField = new JTextField();
        inputPanel.add(maxDemandField);

        add(inputPanel, BorderLayout.NORTH);

        resultArea = new JTextArea();
        resultArea.setEditable(false);
        resultArea.setLineWrap(true);
        resultArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(resultArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        calculateButton = createStyledButton("Calculate");
        calculateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                calculateEOQ();
            }
        });
        buttonPanel.add(calculateButton);

        clearButton = createStyledButton("Clear");
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearFields();
            }
        });
        buttonPanel.add(clearButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(new Color(70, 130, 180));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(100, 160, 210));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(70, 130, 180));
            }
        });
        return button;
    }

    private void calculateEOQ() {
        try {
            double D = Double.parseDouble(demandField.getText());
            double K = Double.parseDouble(setupCostField.getText());
            double h = Double.parseDouble(holdingCostField.getText());
            double p = Double.parseDouble(shortageCostField.getText());
            double minDemand = Double.parseDouble(minDemandField.getText());
            double maxDemand = Double.parseDouble(maxDemandField.getText());

            double E = (minDemand + maxDemand) / 2; // Expected demand during lead time

            // Initial calculation(Classical EOQ)
            double y = Math.sqrt(2 * D * K / h);
            double R = maxDemand - (y * h) / (p * D) * (maxDemand - minDemand);

            resultArea.setText("Iteration 0:\n");
            resultArea.append(String.format("y = %.2f, R = %.2f\n\n", y, R));

            for (int i = 1; i <= 10; i++) { // Max 10 iterations
                double S = calculateS(R, minDemand, maxDemand);
                double oldY = y;
                double oldR = R;

                y = Math.sqrt(2 * D * (K + p * S) / h);
                R = maxDemand - (y * h) / (p * D) * (maxDemand - minDemand);

                resultArea.append(String.format("Iteration %d:\n", i));
                resultArea.append(String.format("S = %.4f\n", S));
                resultArea.append(String.format("y = %.2f, R = %.2f\n\n", y, R));

                if (Math.abs(y - oldY) < 0.01 && Math.abs(R - oldR) < 0.01) {
                    break;
                }
            }

            resultArea.append(String.format("Final result:\nOptimal Order Quantity (y*) ≈ %.2f\n", y));
            resultArea.append(String.format("Reorder Point (R*) ≈ %.2f\n", R));

        } catch (NumberFormatException ex) {
            resultArea.setText("Invalid input. Please enter valid numbers.");
        }
    }

    private double calculateS(double R, double minDemand, double maxDemand) {
        if (R <= minDemand) { //calculates S by checking for conditions
            return (maxDemand - minDemand) / 2;
        } else if (R >= maxDemand) {
            return 0;
        } else {
            return Math.pow(maxDemand - R, 2) / (2 * (maxDemand - minDemand));
        }
    }

    private void clearFields() {
        demandField.setText("");
        setupCostField.setText("");
        holdingCostField.setText("");
        shortageCostField.setText("");
        minDemandField.setText("");
        maxDemandField.setText("");
        resultArea.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ProbabilisticEOQCalculator().setVisible(true);
            }
        });
    }
}