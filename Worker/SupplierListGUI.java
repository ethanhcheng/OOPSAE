package Worker;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class SupplierListGUI extends JFrame {

    public SupplierListGUI() {
        setTitle("Supplier List");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        String[] columns = {"ID", "Name", "Contact Info", "Order Details"};
        Object[][] data = InventoryManager.loadDataFromFile("supplier.txt", 4);

        JTable table = new JTable(new DefaultTableModel(data, columns));
        JScrollPane scrollPane = new JScrollPane(table);

        add(scrollPane, BorderLayout.CENTER);
        setVisible(true);
    }

}
