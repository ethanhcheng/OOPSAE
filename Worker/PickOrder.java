package Worker;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PickOrder extends JFrame {
    private static final String PICKING_LIST_PATH = "picking_list.txt";
    private static final String INVENTORY_LIST_PATH = "inventory.txt";

    private static final String[] PICKING_LIST_COLUMNS = {
        "Customer ID", "Product ID", "Quantity", "Order Date and Time"
    };

    private static final String[] INVENTORY_LIST_COLUMNS = {
        "Product ID", "Product Name", "Quantity", "Location", "Supplier ID"
    };

    private JTable pickingListTable;
    private JTable inventoryListTable;
    private DefaultTableModel pickingListModel;
    private DefaultTableModel inventoryListModel;

    public PickOrder() {
        setTitle("Inventory and Picking List");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        pickingListModel = new DefaultTableModel(PICKING_LIST_COLUMNS, 0);
        pickingListTable = new JTable(pickingListModel);
        loadTableFromFile(pickingListTable, pickingListModel, PICKING_LIST_PATH);

        inventoryListModel = new DefaultTableModel(INVENTORY_LIST_COLUMNS, 0);
        inventoryListTable = new JTable(inventoryListModel);
        loadTableFromFile(inventoryListTable, inventoryListModel, INVENTORY_LIST_PATH);

        JScrollPane pickingListScrollPane = new JScrollPane(pickingListTable);
        pickingListScrollPane.setBorder(BorderFactory.createTitledBorder("Picking List"));

        JScrollPane inventoryListScrollPane = new JScrollPane(inventoryListTable);
        inventoryListScrollPane.setBorder(BorderFactory.createTitledBorder("Inventory List"));

        JButton processOrderButton = new JButton("Process Order");
        processOrderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                processSelectedOrder();
            }
        });

        mainPanel.add(pickingListScrollPane);
        mainPanel.add(inventoryListScrollPane);
        mainPanel.add(processOrderButton);

        add(mainPanel);

        setVisible(true);
    }

    private void loadTableFromFile(JTable table, DefaultTableModel model, String filePath) {
        try {
            List<String> lines = Files.readAllLines(Paths.get(filePath));
            
            model.setRowCount(0);
            
            for (String line : lines) {
                String[] rowData = line.split(",");
                model.addRow(rowData);
            }
            
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                "Error reading file: " + e.getMessage(),
                "File Read Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void processSelectedOrder() {
        int selectedRow = pickingListTable.getSelectedRow();
        
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Please select an order to process.",
                "No Order Selected",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        String orderProductId = (String) pickingListModel.getValueAt(selectedRow, 1);
        int orderQuantity = Integer.parseInt((String) pickingListModel.getValueAt(selectedRow, 2));

        int inventoryRowIndex = -1;
        for (int i = 0; i < inventoryListModel.getRowCount(); i++) {
            String inventoryProductId = (String) inventoryListModel.getValueAt(i, 0);
            if (orderProductId.equals(inventoryProductId)) {
                inventoryRowIndex = i;
                break;
            }
        }

        if (inventoryRowIndex == -1) {
            JOptionPane.showMessageDialog(this,
                "Order could not be completed: Product not found in inventory.",
                "Order Processing Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        int currentInventoryQuantity = Integer.parseInt((String) inventoryListModel.getValueAt(inventoryRowIndex, 2));

        if (currentInventoryQuantity < orderQuantity) {
            JOptionPane.showMessageDialog(this,
                "Order could not be completed: Insufficient inventory.",
                "Order Processing Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        int newInventoryQuantity = currentInventoryQuantity - orderQuantity;
        inventoryListModel.setValueAt(String.valueOf(newInventoryQuantity), inventoryRowIndex, 2);

        pickingListModel.removeRow(selectedRow);

        saveTableToFile(pickingListModel, PICKING_LIST_PATH);
        saveTableToFile(inventoryListModel, INVENTORY_LIST_PATH);

        JOptionPane.showMessageDialog(this,
            "Order processed successfully!",
            "Order Processed",
            JOptionPane.INFORMATION_MESSAGE);
    }

    private void saveTableToFile(DefaultTableModel model, String filePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (int row = 0; row < model.getRowCount(); row++) {
                StringBuilder lineBuilder = new StringBuilder();
                for (int col = 0; col < model.getColumnCount(); col++) {
                    lineBuilder.append(model.getValueAt(row, col));
                    if (col < model.getColumnCount() - 1) {
                        lineBuilder.append(",");
                    }
                }
                writer.write(lineBuilder.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                "Error saving file: " + e.getMessage(),
                "File Save Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PickOrder());
    }
}
