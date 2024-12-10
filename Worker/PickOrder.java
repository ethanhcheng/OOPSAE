package Worker;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class PickOrder extends JFrame {
    public PickOrder(List<Product> stockList, List<InventoryHistory> inventoryHistory) {
        setTitle("Pick Order");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JLabel orderIdLabel = new JLabel("Order ID:");
        JTextField orderIdField = new JTextField(15);
        JLabel productIdLabel = new JLabel("Product ID:");
        JTextField productIdField = new JTextField(15);
        JLabel quantityLabel = new JLabel("Quantity:");
        JTextField quantityField = new JTextField(15);
        JButton submitButton = new JButton("Submit");

        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.add(orderIdLabel); panel.add(orderIdField);
        panel.add(productIdLabel); panel.add(productIdField);
        panel.add(quantityLabel); panel.add(quantityField);
        panel.add(new JLabel()); panel.add(submitButton);

        submitButton.addActionListener(e -> {
            String orderId = orderIdField.getText();
            String productId = productIdField.getText();
            int quantity = Integer.parseInt(quantityField.getText());

            Product product = stockList.stream()
                    .filter(p -> p.getId().equals(productId))
                    .findFirst()
                    .orElse(null);

            if (product != null && product.getQuantity() >= quantity) {
                product.setQuantity(product.getQuantity() - quantity);
                inventoryHistory.add(new InventoryHistory(productId, "Removed", quantity));

                JOptionPane.showMessageDialog(this, "Order Picked Successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "Insufficient Stock or Invalid Product ID!");
            }
            dispose();
        });

        add(panel);
        setVisible(true);
    }
}

