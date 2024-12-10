package Worker;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class RegisterProduct extends JFrame {
    public RegisterProduct(List<Product> stockList, List<InventoryHistory> inventoryHistory) {
        setTitle("Register Product");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JLabel idLabel = new JLabel("Product ID:");
        JTextField idField = new JTextField(15);
        JLabel nameLabel = new JLabel("Product Name:");
        JTextField nameField = new JTextField(15);
        JLabel quantityLabel = new JLabel("Quantity:");
        JTextField quantityField = new JTextField(15);
        JLabel locationLabel = new JLabel("Location:");
        JTextField locationField = new JTextField(15);
        JLabel supplierLabel = new JLabel("Supplier ID:");
        JTextField supplierField = new JTextField(15);
        JButton submitButton = new JButton("Submit");

        JPanel panel = new JPanel(new GridLayout(6, 2, 10, 10));
        panel.add(idLabel); panel.add(idField);
        panel.add(nameLabel); panel.add(nameField);
        panel.add(quantityLabel); panel.add(quantityField);
        panel.add(locationLabel); panel.add(locationField);
        panel.add(supplierLabel); panel.add(supplierField);
        panel.add(new JLabel()); panel.add(submitButton);

        submitButton.addActionListener(e -> {
            String id = idField.getText();
            String name = nameField.getText();
            int quantity = Integer.parseInt(quantityField.getText());
            String location = locationField.getText();
            String supplierId = supplierField.getText();

            Product product = new Product(id, name, quantity, location, supplierId);
            stockList.add(product);
            inventoryHistory.add(new InventoryHistory(id, "Added", quantity));

            JOptionPane.showMessageDialog(this, "Product Registered Successfully!");
            dispose();
        });

        add(panel);
        setVisible(true);
    }
}

