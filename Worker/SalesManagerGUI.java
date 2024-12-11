package Worker;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class SalesManagerGUI extends JFrame {
    private static final String PICKING_LIST_PATH = "picking_list.txt";
    private static final String ORDER_LIST_PATH = "order_list.txt";

    public SalesManagerGUI() {
        setTitle("Sales Manager Interface");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JLabel titleLabel = new JLabel("Sales Manager Options");
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JButton displayPickingListButton = new JButton("Display History Picking List");
        JButton displayNewOrdersButton = new JButton("Display Newly Created Orders");

        // Action listeners for buttons
        displayPickingListButton.addActionListener(e -> displayPickingList());
        displayNewOrdersButton.addActionListener(e -> displayNewOrders());

        JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        buttonPanel.add(displayPickingListButton);
        buttonPanel.add(displayNewOrdersButton);

        setLayout(new BorderLayout());
        add(titleLabel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    private void displayPickingList() {
        JFrame pickingListFrame = new JFrame("History Picking List");
        pickingListFrame.setSize(600, 500);
        pickingListFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        pickingListFrame.setLocationRelativeTo(null);

        // Define column names for the table
        String[] columnNames = {"Customer ID", "Product ID", "Quantity", "Order Placed Time"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);

        File pickingListFile = new File(PICKING_LIST_PATH);
        if (pickingListFile.exists()) {
            List<String[]> pickingList = loadOrderList(pickingListFile);
            for (String[] row : pickingList) {
                tableModel.addRow(row);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Picking list file does not exist.", "Error", JOptionPane.ERROR_MESSAGE);
        }

        JTable table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);

        pickingListFrame.setLayout(new BorderLayout());
        pickingListFrame.add(scrollPane, BorderLayout.CENTER);

        pickingListFrame.setVisible(true);
    }


    private void displayNewOrders() {
        JFrame orderListFrame = new JFrame("Newly Created Orders");
        orderListFrame.setSize(600, 500);
        orderListFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        orderListFrame.setLocationRelativeTo(null);

        String[] columnNames = {"Customer ID", "Product ID", "Quantity", "Order Placed Time"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);

        File orderListFile = new File(ORDER_LIST_PATH);
        if (orderListFile.exists()) {
            List<String[]> orderList = loadOrderList(orderListFile);
            for (String[] row : orderList) {
                tableModel.addRow(row);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Order list file does not exist.", "Error", JOptionPane.ERROR_MESSAGE);
        }

        JTable table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);

        JButton generatePickingListButton = new JButton("Generate Daily Picking List");
        generatePickingListButton.addActionListener(e -> generatePickingList(orderListFrame));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(generatePickingListButton);

        orderListFrame.setLayout(new BorderLayout());
        orderListFrame.add(scrollPane, BorderLayout.CENTER);
        orderListFrame.add(buttonPanel, BorderLayout.SOUTH);

        orderListFrame.setVisible(true);
    }

    private void generatePickingList(JFrame parentFrame) {
        File orderListFile = new File(ORDER_LIST_PATH);
        File pickingListFile = new File(PICKING_LIST_PATH);

        if (!orderListFile.exists()) {
            JOptionPane.showMessageDialog(parentFrame, "Order list file does not exist. Cannot generate picking list.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(orderListFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(pickingListFile, true))) {

            String line;
            while ((line = reader.readLine()) != null) {
                writer.write(line);
                writer.newLine();
            }

            JOptionPane.showMessageDialog(parentFrame, "Daily picking list generated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);

        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(parentFrame, "An error occurred while generating the picking list.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private List<String> loadFileContent(File file) {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }

    private List<String[]> loadOrderList(File file) {
        List<String[]> orders = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 4) {
                    orders.add(parts);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return orders;
    }

    public static void main(String[] args) {
        new SalesManagerGUI();
    }
}