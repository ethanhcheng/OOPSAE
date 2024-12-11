package Worker;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class StatusGUI extends JFrame {

    public StatusGUI() {
        setTitle("Status for Each Item");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Table columns
        String[] columns = {"ProductID", "ItemID", "Status", "UserID(if ordered)"};

        // Load data from status.txt
        Object[][] data = InventoryManager.loadDataFromFile("status.txt", 4);

        // Create the table and scroll pane
        DefaultTableModel tableModel = new DefaultTableModel(data, columns);
        JTable table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);

        // Add the table to the frame
        add(scrollPane, BorderLayout.CENTER);
        setVisible(true);
    }

    public static void main(String[] args) {
        new StatusGUI();
    }
}
