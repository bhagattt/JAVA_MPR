package JAVA_MPR;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ProbabilisticEOQCalculator extends JFrame {
    private JTextField demandField, setupCostField, holdingCostField, shortageCostField, minDemandField, maxDemandField;
    private JTextArea resultArea;
    private JButton calculateButton;

    public ProbabilisticEOQCalculator() {
        setTitle("Probabilistic EOQ Calculator");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridLayout(6, 2));
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
        add(new JScrollPane(resultArea), BorderLayout.CENTER);

        calculateButton = new JButton("Calculate");
        calculateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                calculateEOQ();
            }
        });
        add(calculateButton, BorderLayout.SOUTH);
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

            // Initial calculation
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
        if (R <= minDemand) {
            return (maxDemand - minDemand) / 2;
        } else if (R >= maxDemand) {
            return 0;
        } else {
            return Math.pow(maxDemand - R, 2) / (2 * (maxDemand - minDemand));
        }
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