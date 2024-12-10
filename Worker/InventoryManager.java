package Worker;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;


public class InventoryManager extends JFrame {
	
    public static Object[][] loadDataFromFile(String filePath, int columnCount) {
        List<Object[]> data = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length == columnCount) {
                    data.add(values);
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error reading file: " + filePath, "Error", JOptionPane.ERROR_MESSAGE);
        }
        return data.toArray(new Object[0][]);
    }

    public InventoryManager() {
        setTitle("Inventory Manager");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main Panel
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 1));

        // Buttons for options
        JButton inventoryButton = new JButton("Display Inventory List");
        JButton supplierButton = new JButton("Display Supplier List");
        JButton statusButton = new JButton("Display Status for Each Item");

        // Add action listeners to buttons
        inventoryButton.addActionListener(e -> new InventoryListGUI());
        supplierButton.addActionListener(e -> new SupplierListGUI());
        statusButton.addActionListener(e -> new StatusGUI());

        // Add buttons to panel
        panel.add(inventoryButton);
        panel.add(supplierButton);
        panel.add(statusButton);

        add(panel);
        setVisible(true);
    }


    public static void main(String[] args) {
        new InventoryManager();
    }
}
