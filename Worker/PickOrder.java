package Worker;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class PickOrder extends JFrame {
    private static final String PICKING_LIST_PATH = "picking_list.txt";
    private static final String INVENTORY_LIST_PATH = "inventory.txt";

    private static final String[] PICKING_LIST_COLUMNS = {
        "Customer ID", "Product ID", "Quantity", "Order Date and Time"
    };

    private static final String[] INVENTORY_LIST_COLUMNS = {
        "Product ID", "Product Name", "Quantity", "Location", "Supplier ID"
    };

    public PickOrder() {
        setTitle("Inventory and Picking List");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        JTable pickingListTable = createTableFromFile(PICKING_LIST_PATH, PICKING_LIST_COLUMNS, "Picking List");
        JTable inventoryListTable = createTableFromFile(INVENTORY_LIST_PATH, INVENTORY_LIST_COLUMNS, "Inventory List");

        JScrollPane pickingListScrollPane = new JScrollPane(pickingListTable);
        pickingListScrollPane.setBorder(BorderFactory.createTitledBorder("Picking List"));

        JScrollPane inventoryListScrollPane = new JScrollPane(inventoryListTable);
        inventoryListScrollPane.setBorder(BorderFactory.createTitledBorder("Inventory List"));

        mainPanel.add(pickingListScrollPane);
        mainPanel.add(inventoryListScrollPane);

        add(mainPanel);

        setVisible(true);
    }

    private JTable createTableFromFile(String filePath, String[] predefinedColumns, String tableName) {
        try {
            List<String> lines = Files.readAllLines(Paths.get(filePath));
            
            DefaultTableModel model = new DefaultTableModel(predefinedColumns, 0);
            
            if (lines.isEmpty()) {
                return new JTable(model);
            }
            
            for (String line : lines) {
                String[] rowData = line.split(",");
                model.addRow(rowData);
            }
            
            return new JTable(model);
            
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                "Error reading " + tableName + ": " + e.getMessage(),
                "File Read Error",
                JOptionPane.ERROR_MESSAGE);
            return new JTable();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PickOrder());
    }
}
