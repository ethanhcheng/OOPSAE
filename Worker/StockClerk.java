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

        JButton registerProductButton = new JButton("Register Product");
        JButton checkRegisteredProductsButton = new JButton("Check Registered Products");
        JButton viewInventoryButton = new JButton("View Inventory");
        JButton saveInventoryButton = new JButton("Save Inventory");

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(registerProductButton);
        panel.add(Box.createVerticalStrut(10));
        panel.add(checkRegisteredProductsButton);
        panel.add(Box.createVerticalStrut(10));
        panel.add(viewInventoryButton);
        panel.add(Box.createVerticalStrut(10));
        panel.add(saveInventoryButton);

        registerProductButton.addActionListener(e -> new RegisterProduct(stockList, inventoryHistory));
        checkRegisteredProductsButton.addActionListener(e -> checkRegisteredProducts());
        viewInventoryButton.addActionListener(e -> viewInventoryFromFile());
        saveInventoryButton.addActionListener(e -> saveInventoryToFile());

        add(panel);
        setVisible(true);
    }

    private void checkRegisteredProducts() {
        if (stockList.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No products registered yet!");
            return;
        }

        String[] columns = {"Product ID", "Name", "Quantity", "Location", "Supplier ID", "Actions"};
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0);

        for (Product product : stockList) {
            Object[] row = {
                product.getId(),
                product.getName(),
                product.getQuantity(),
                product.getLocation(),
                product.getSupplierId(),
                "Edit/Delete"
            };
            tableModel.addRow(row);
        }

        JTable table = new JTable(tableModel) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5;
            }
        };

        table.getColumnModel().getColumn(5).setCellRenderer(new ButtonRenderer());
        table.getColumnModel().getColumn(5).setCellEditor(new ButtonEditor(new JCheckBox(), table, tableModel));

        JScrollPane scrollPane = new JScrollPane(table);
        JOptionPane.showMessageDialog(this, scrollPane, "Registered Products", JOptionPane.INFORMATION_MESSAGE);
    }

    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setText("Edit/Delete");
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            return this;
        }
    }

    class ButtonEditor extends DefaultCellEditor {
        private final JTable table;
        private final DefaultTableModel tableModel;

        public ButtonEditor(JCheckBox checkBox, JTable table, DefaultTableModel tableModel) {
            super(checkBox);
            this.table = table;
            this.tableModel = tableModel;
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            int option = JOptionPane.showOptionDialog(
                null,
                "Do you want to edit or delete this entry?",
                "Edit/Delete",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                new String[]{"Edit", "Delete", "Cancel"},
                "Cancel"
            );

            if (option == JOptionPane.YES_OPTION) {
                editEntry(row);
            } else if (option == JOptionPane.NO_OPTION) {
                deleteEntry(row);
            }

            return super.getTableCellEditorComponent(table, value, isSelected, row, column);
        }

        private void editEntry(int row) {
            String id = (String) tableModel.getValueAt(row, 0);
            String name = (String) tableModel.getValueAt(row, 1);
            int quantity = Integer.parseInt(tableModel.getValueAt(row, 2).toString());
            String location = (String) tableModel.getValueAt(row, 3);
            String supplierId = (String) tableModel.getValueAt(row, 4);

            JTextField idField = new JTextField(id);
            JTextField nameField = new JTextField(name);
            JTextField quantityField = new JTextField(String.valueOf(quantity));
            JTextField locationField = new JTextField(location);
            JTextField supplierIdField = new JTextField(supplierId);

            JPanel panel = new JPanel(new GridLayout(5, 2));
            panel.add(new JLabel("Product ID:"));
            panel.add(idField);
            panel.add(new JLabel("Name:"));
            panel.add(nameField);
            panel.add(new JLabel("Quantity:"));
            panel.add(quantityField);
            panel.add(new JLabel("Location:"));
            panel.add(locationField);
            panel.add(new JLabel("Supplier ID:"));
            panel.add(supplierIdField);

            int result = JOptionPane.showConfirmDialog(null, panel, "Edit Product", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                tableModel.setValueAt(idField.getText(), row, 0);
                tableModel.setValueAt(nameField.getText(), row, 1);
                tableModel.setValueAt(Integer.parseInt(quantityField.getText()), row, 2);
                tableModel.setValueAt(locationField.getText(), row, 3);
                tableModel.setValueAt(supplierIdField.getText(), row, 4);
                stockList.get(row).setId(idField.getText());
                stockList.get(row).setName(nameField.getText());
                stockList.get(row).setQuantity(Integer.parseInt(quantityField.getText()));
                stockList.get(row).setLocation(locationField.getText());
                stockList.get(row).setSupplierId(supplierIdField.getText());
            }
        }

        private void deleteEntry(int row) {
            stockList.remove(row);
            tableModel.removeRow(row);
        }
    }

    private void viewInventoryFromFile() {
        File inventoryFile = new File("inventory.txt");
        if (!inventoryFile.exists()) {
            JOptionPane.showMessageDialog(this, "No inventory file found.");
            return;
        }

        List<Product> inventory = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(inventoryFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 5) {
                    String id = parts[0];
                    String name = parts[1];
                    int quantity = Integer.parseInt(parts[2]);
                    String location = parts[3];
                    String supplierId = parts[4];

                    inventory.add(new Product(id, name, quantity, location, supplierId));
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error reading inventory file: " + e.getMessage());
            return;
        }

        if (inventory.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Inventory is empty.");
            return;
        }

        String[] columns = {"Product ID", "Name", "Quantity", "Location", "Supplier ID"};
        String[][] data = new String[inventory.size()][columns.length];

        for (int i = 0; i < inventory.size(); i++) {
            Product product = inventory.get(i);
            data[i][0] = product.getId();
            data[i][1] = product.getName();
            data[i][2] = String.valueOf(product.getQuantity());
            data[i][3] = product.getLocation();
            data[i][4] = product.getSupplierId();
        }

        JTable table = new JTable(data, columns);
        JScrollPane scrollPane = new JScrollPane(table);
        JOptionPane.showMessageDialog(this, scrollPane, "Inventory", JOptionPane.INFORMATION_MESSAGE);
    }

    private void saveInventoryToFile() {
        File inventoryFile = new File("inventory.txt");

        if (inventoryFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(inventoryFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length == 5) {
                        String id = parts[0];
                        String name = parts[1];
                        int quantity = Integer.parseInt(parts[2]);
                        String location = parts[3];
                        String supplierId = parts[4];

                        stockList.add(new Product(id, name, quantity, location, supplierId));
                    }
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error loading existing inventory: " + e.getMessage());
                return;
            }
        }

        try (FileWriter writer = new FileWriter(inventoryFile, false)) {
            for (Product product : stockList) {
                writer.write(product.toString() + "\n");
            }
            JOptionPane.showMessageDialog(this, "Inventory saved to inventory.txt!");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving inventory: " + e.getMessage());
        }
    }
}
