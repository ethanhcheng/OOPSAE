package Worker;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class StockClerk extends JFrame {
    private final List<Product> stockList = new ArrayList<>();
    private final List<InventoryHistory> inventoryHistory = new ArrayList<>();

    public StockClerk() {
        setTitle("Stock Clerk Interface");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create buttons
        JButton productRegistrationButton = new JButton("Product Registration");
        JButton pickingItemsButton = new JButton("Picking Items");

        // Style buttons (optional)
        Dimension buttonSize = new Dimension(300, 50);
        productRegistrationButton.setPreferredSize(buttonSize);
        pickingItemsButton.setPreferredSize(buttonSize);

        // Add buttons to a vertical panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(4, 1));
        buttonPanel.add(productRegistrationButton);
        buttonPanel.add(pickingItemsButton);

        // Center the buttons in the frame
        add(buttonPanel, BorderLayout.CENTER);

        // Button actions
        productRegistrationButton.addActionListener(e -> new ProductRegistrationGUI());
        pickingItemsButton.addActionListener(e -> new PickOrder());

        // Set visibility
        setVisible(true);
    }

    public static void main(String[] args) {
        new StockClerk();
    }
}
