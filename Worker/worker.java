package Worker;

import javax.swing.*;

public class worker extends JFrame {
    public worker() {
        setTitle("Worker Interface");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JLabel label = new JLabel("Select Worker Interface:");
        JButton inventoryManagerButton = new JButton("Inventory Manager");
        JButton stockClerkButton = new JButton("Stock Clerk");

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(label);
        panel.add(Box.createVerticalStrut(20));
        panel.add(inventoryManagerButton);
        panel.add(Box.createVerticalStrut(10));
        panel.add(stockClerkButton);

        inventoryManagerButton.addActionListener(e -> new InventoryManager());
        
        stockClerkButton.addActionListener(e -> new StockClerk());

        add(panel);
        setVisible(true);
    }
}

