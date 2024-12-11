package Worker;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

class ProductRegistrationGUI extends JFrame {

    private JTable inventoryTable, statusTable;
    private DefaultTableModel inventoryTableModel, statusTableModel;

    public ProductRegistrationGUI() {
        setTitle("Product Registration");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Load data from two files
        Object[][] inventoryData = loadDataFromFile("inventory.txt", 5);
        Object[][] statusData = loadDataFromFile("status.txt", 4);

        // Define columns for each file
        String[] inventoryColumns = {"Product ID", "Name", "Quantity", "Location", "Supplier ID"};
        String[] statusColumns = {"Product ID", "Item ID", "Status", "Timestamp"};

        // Create tables
        inventoryTableModel = new DefaultTableModel(inventoryData, inventoryColumns);
        statusTableModel = new DefaultTableModel(statusData, statusColumns);
        inventoryTable = new JTable(inventoryTableModel);
        statusTable = new JTable(statusTableModel);

        // Scroll panes for tables
        JScrollPane inventoryScrollPane = new JScrollPane(inventoryTable);
        JScrollPane statusScrollPane = new JScrollPane(statusTable);

        // Set up split pane to display both tables side-by-side
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, inventoryScrollPane, statusScrollPane);
        splitPane.setResizeWeight(0.5); // Allocate equal space for both tables

        // Add product panel
        JPanel productPanel = createProductPanel();

        // Add components to the frame
        setLayout(new BorderLayout());
        add(splitPane, BorderLayout.CENTER);
        add(productPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private JPanel createProductPanel() {
        JPanel productPanel = new JPanel(new GridBagLayout());
        productPanel.setBorder(BorderFactory.createTitledBorder("Add/Update Product"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField productIdField = new JTextField();
        JTextField nameField = new JTextField();
        JTextField quantityField = new JTextField();
        JTextField locationField = new JTextField();
        JTextField supplierIdField = new JTextField();

        Dimension textFieldSize = new Dimension(200, 25);
        productIdField.setPreferredSize(textFieldSize);
        nameField.setPreferredSize(textFieldSize);
        quantityField.setPreferredSize(textFieldSize);
        locationField.setPreferredSize(textFieldSize);
        supplierIdField.setPreferredSize(textFieldSize);

        gbc.gridx = 0; gbc.gridy = 0;
        productPanel.add(new JLabel("Product ID:"), gbc);
        gbc.gridx = 1;
        productPanel.add(productIdField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        productPanel.add(new JLabel("Name (if new):"), gbc);
        gbc.gridx = 1;
        productPanel.add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        productPanel.add(new JLabel("Quantity:"), gbc);
        gbc.gridx = 1;
        productPanel.add(quantityField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        productPanel.add(new JLabel("Location (if new):"), gbc);
        gbc.gridx = 1;
        productPanel.add(locationField, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        productPanel.add(new JLabel("Supplier ID (if new):"), gbc);
        gbc.gridx = 1;
        productPanel.add(supplierIdField, gbc);

        JButton addOrUpdateButton = new JButton("Add/Update Product");
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        productPanel.add(addOrUpdateButton, gbc);

        // Button action
        addOrUpdateButton.addActionListener(e -> {
            String productId = productIdField.getText().trim();
            String name = nameField.getText().trim();
            int quantity;
            try {
                quantity = Integer.parseInt(quantityField.getText().trim());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid quantity!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String location = locationField.getText().trim();
            String supplierId = supplierIdField.getText().trim();

            boolean productExists = false;
            for (int i = 0; i < inventoryTableModel.getRowCount(); i++) {
                if (inventoryTableModel.getValueAt(i, 0).equals(productId)) {
                    int currentQuantity = Integer.parseInt(inventoryTableModel.getValueAt(i, 2).toString());
                    inventoryTableModel.setValueAt(currentQuantity + quantity, i, 2);
                    productExists = true;
                    updateStatusFile(productId, quantity);
                    break;
                }
            }

            if (!productExists) {
                inventoryTableModel.addRow(new Object[]{productId, name, quantity, location, supplierId});
                updateStatusFile(productId, quantity);
            }

            saveDataToFile(inventoryTableModel, "inventory.txt");
            productIdField.setText("");
            nameField.setText("");
            quantityField.setText("");
            locationField.setText("");
            supplierIdField.setText("");
        });

        return productPanel;
    }

    private Object[][] loadDataFromFile(String filePath, int columnCount) {
        List<Object[]> data = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",", columnCount);
                if (parts.length == columnCount) {
                    data.add(parts);
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error reading file: " + filePath, "Error", JOptionPane.ERROR_MESSAGE);
        }
        return data.toArray(new Object[0][]);
    }

    private void saveDataToFile(DefaultTableModel tableModel, String filePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                for (int j = 0; j < tableModel.getColumnCount(); j++) {
                    writer.write(tableModel.getValueAt(i, j).toString());
                    if (j < tableModel.getColumnCount() - 1) {
                        writer.write(",");
                    }
                }
                writer.newLine();
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving data to file!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateStatusFile(String productId, int additionalQuantity) {
        File statusFile = new File("status.txt");
        List<String> lines = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(statusFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error reading status file!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int startingIndex = 1;
        for (String line : lines) {
            String[] parts = line.split(",");
            if (parts[0].equals(productId)) {
                String lastItemId = parts[1];
                startingIndex = Math.max(startingIndex, Integer.parseInt(lastItemId.substring(lastItemId.length() - 3)) + 1);
            }
        }

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        for (int i = 0; i < additionalQuantity; i++) {
            String newItemId = String.format("%s%03d", productId, startingIndex + i);
            lines.add(productId + "," + newItemId + ",arrived," + timestamp);
            statusTableModel.addRow(new Object[]{productId, newItemId, "arrived", timestamp});
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(statusFile))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error updating status file!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        new ProductRegistrationGUI();
    }
}
