package Worker;

import javax.swing.*;

public class worker extends JFrame {
    public worker() {
        setTitle("Worker Interface");
        setSize(400, 250); // Adjusted size to fit the new button
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JLabel label = new JLabel("Select Worker Interface:");
        JButton inventoryManagerButton = new JButton("Inventory Manager");
        JButton stockClerkButton = new JButton("Stock Clerk");
        JButton salesManagerButton = new JButton("Sales Manager"); // New button

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(label);
        panel.add(Box.createVerticalStrut(20));
        panel.add(inventoryManagerButton);
        panel.add(Box.createVerticalStrut(10));
        panel.add(stockClerkButton);
        panel.add(Box.createVerticalStrut(10));
        panel.add(salesManagerButton); // Add Sales Manager button to the panel

        // Add action listeners for the buttons
        inventoryManagerButton.addActionListener(e -> new InventoryManager());
        stockClerkButton.addActionListener(e -> new StockClerk());
        salesManagerButton.addActionListener(e -> new SalesManagerGUI()); // New action listener

        add(panel);
        setVisible(true);
    }
}
