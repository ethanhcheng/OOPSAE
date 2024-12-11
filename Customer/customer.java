package Customer;

import javax.swing.*;

public class customer extends JFrame {
    public customer(String folderPath) {
        setTitle("Customer Interface");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JLabel label = new JLabel("Select an Action:");
        JButton createOrderButton = new JButton("Create New Order");
        JButton viewOrderButton = new JButton("View Orders");

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(label);
        panel.add(Box.createVerticalStrut(20));
        panel.add(createOrderButton);
        panel.add(Box.createVerticalStrut(10));
        panel.add(viewOrderButton);

        createOrderButton.addActionListener(e -> new CustomerInterface(folderPath, "create"));
        viewOrderButton.addActionListener(e -> new CustomerInterface(folderPath, "view"));

        add(panel);
        setVisible(true);
    }
}