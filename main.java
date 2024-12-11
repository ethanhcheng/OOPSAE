import javax.swing.*;

public class main extends JFrame {
    public main() {
        setTitle("Supply Chain System");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JLabel label = new JLabel("Select User Type:");
        JButton workerButton = new JButton("Worker");
        JButton customerButton = new JButton("Customer");
        
        String folderPath= "../files";

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(label);
        panel.add(Box.createVerticalStrut(20));
        panel.add(workerButton);
        panel.add(Box.createVerticalStrut(10));
        panel.add(customerButton);

        workerButton.addActionListener(e -> new Worker.workerlogin());
        customerButton.addActionListener(e -> new Customer.customer(folderPath));

        add(panel);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new main());
    }
}

