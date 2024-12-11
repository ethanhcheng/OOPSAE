package Worker;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.List;

public class InventoryListGUI extends JFrame {

    public InventoryListGUI() {
        setTitle("Inventory List");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        String[] columns = {"ID", "Name", "Quantity", "Location", "Supplier ID"};
        Object[][] data = InventoryManager.loadDataFromFile("inventory.txt", 5);

        DefaultTableModel model = new DefaultTableModel(data, columns);
        JTable table = new JTable(model);

        // Enable sorting
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        JScrollPane scrollPane = new JScrollPane(table);

        // Controls for sorting and filtering
        JPanel controls = new JPanel();
        controls.setLayout(new BoxLayout(controls, BoxLayout.Y_AXIS));

        // First row: Sort controls
        JPanel sortPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        sortPanel.add(new JLabel("Sort by:"));
        String[] sortOptions = {"Quantity", "Location", "Supplier ID"};
        JComboBox<String> sortBox = new JComboBox<>(sortOptions);
        sortPanel.add(sortBox);
        JButton sortButton = new JButton("Sort");
        sortPanel.add(sortButton);

        // Second row: Filter controls
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.add(new JLabel("Quantity Threshold:"));
        JTextField thresholdField = new JTextField(5);
        filterPanel.add(thresholdField);
        JButton filterButton = new JButton("Filter");
        filterPanel.add(filterButton);
        JButton resetButton = new JButton("Reset");
        filterPanel.add(resetButton);

        // Add panels to controls
        controls.add(sortPanel);
        controls.add(filterPanel);

        // Sorting action
        sortButton.addActionListener(e -> {
            int columnIndex = switch (sortBox.getSelectedIndex()) {
                case 0 -> 2; // Quantity
                case 1 -> 3; // Location
                case 2 -> 4; // Supplier ID
                default -> -1;
            };
            if (columnIndex != -1) {
                sorter.setSortKeys(List.of(new RowSorter.SortKey(columnIndex, SortOrder.ASCENDING)));
                sorter.sort();
            }
        });

        // Filtering action
        filterButton.addActionListener(e -> {
            String thresholdText = thresholdField.getText();
            try {
                int threshold = Integer.parseInt(thresholdText);
                sorter.setRowFilter(new RowFilter<DefaultTableModel, Integer>() {
                    public boolean include(Entry<? extends DefaultTableModel, ? extends Integer> entry) {
                        try {
                            String quantityString = entry.getValue(2).toString();
                            int quantity = Integer.parseInt(quantityString);
                            return quantity < threshold;
                        } catch (NumberFormatException ex) {
                            System.err.println("Invalid quantity value in row: " + entry.getValue(2));
                            return false;
                        }
                    }
                });
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(
                        this,
                        "Invalid quantity threshold! Please enter a valid number.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });

        // Reset action
        resetButton.addActionListener(e -> {
            sorter.setRowFilter(null);
            thresholdField.setText("");
        });

        add(scrollPane, BorderLayout.CENTER);
        add(controls, BorderLayout.SOUTH);
        setVisible(true);
    }

}
