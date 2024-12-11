package Worker;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class SupplierListGUI extends JFrame {

    private JTable table;
    private DefaultTableModel tableModel;

    public SupplierListGUI() {
        setTitle("Supplier List");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Load supplier data
        String[] columns = {"ID", "Name", "Contact Info", "Order Details"};
        Object[][] data = InventoryManager.loadDataFromFile("supplier.txt", 4);

        // Table setup
        tableModel = new DefaultTableModel(data, columns);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);

        // Order placement panel
        JPanel orderPanel = new JPanel(new GridLayout(5, 2, 5, 5));
        orderPanel.setBorder(BorderFactory.createTitledBorder("Place an Order"));

        // Input fields
        JTextField supplierIdField = new JTextField();
        JTextField nameField = new JTextField();
        JTextField contactField = new JTextField();
        JTextField orderDetailsField = new JTextField();

        // Add labels and fields to the panel
        orderPanel.add(new JLabel("Supplier ID:"));
        orderPanel.add(supplierIdField);
        orderPanel.add(new JLabel("Name (if new):"));
        orderPanel.add(nameField);
        orderPanel.add(new JLabel("Contact Info (if new):"));
        orderPanel.add(contactField);
        orderPanel.add(new JLabel("Order Details:"));
        orderPanel.add(orderDetailsField);

        JButton placeOrderButton = new JButton("Place Order");
        orderPanel.add(new JLabel()); // Empty label for spacing
        orderPanel.add(placeOrderButton);

        // Place order button action
        placeOrderButton.addActionListener(e -> {
            String supplierId = supplierIdField.getText().trim();
            String name = nameField.getText().trim();
            String contact = contactField.getText().trim();
            String orderDetails = orderDetailsField.getText().trim();

            if (supplierId.isEmpty() || orderDetails.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Supplier ID and Order Details are required.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            boolean supplierExists = false;
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                if (tableModel.getValueAt(i, 0).equals(supplierId)) {
                    // Update existing supplier's order history
                    String existingOrderDetails = tableModel.getValueAt(i, 3).toString();
                    tableModel.setValueAt(existingOrderDetails + " | " + orderDetails, i, 3);
                    supplierExists = true;
                    updateSupplierFile(supplierId, null, null, orderDetails, false);
                    break;
                }
            }

            if (!supplierExists) {
                // Add new supplier to the list and file
                tableModel.addRow(new Object[]{supplierId, name, contact, orderDetails});
                updateSupplierFile(supplierId, name, contact, orderDetails, true);
            }

            // Clear input fields
            supplierIdField.setText("");
            nameField.setText("");
            contactField.setText("");
            orderDetailsField.setText("");
        });

        // Layout setup
        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);
        add(orderPanel, BorderLayout.SOUTH);
        setVisible(true);
    }

    private void updateSupplierFile(String supplierId, String name, String contact, String orderDetails, boolean isNewSupplier) {
        File file = new File("supplier.txt");
        List<String> lines = new ArrayList<>();

        // Read existing file content
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",", 4);
                if (parts[0].equals(supplierId) && !isNewSupplier) {
                    // Update order history for existing supplier
                    String updatedLine = parts[0] + "," + parts[1] + "," + parts[2] + "," + parts[3] + " | " + orderDetails;
                    lines.add(updatedLine);
                } else {
                    lines.add(line);
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error reading supplier file.", "Error", JOptionPane.ERROR_MESSAGE);
        }

        if (isNewSupplier) {
            // Add new supplier entry
            lines.add(supplierId + "," + name + "," + contact + "," + orderDetails);
        }

        // Write updated content back to the file
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            for (String line : lines) {
                bw.write(line);
                bw.newLine();
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error updating supplier file.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
