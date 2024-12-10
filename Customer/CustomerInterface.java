package Customer;

import Worker.Product;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerInterface extends JFrame {
    private final File stockFile;
    private final File orderFile;

    public CustomerInterface(String folderPath, String mode) {
        this.stockFile = new File(folderPath, "stock_list.txt");
        this.orderFile = new File(folderPath, "order_list.txt");

        if ("create".equalsIgnoreCase(mode)) {
            openCreateOrderDialog();
        } else if ("modify".equalsIgnoreCase(mode)) {
            openModifyOrderDialog();
        } else if ("track".equalsIgnoreCase(mode)) {
            openTrackOrderDialog();
        }
    }

    private void openCreateOrderDialog() {
        JFrame createOrderFrame = new JFrame("Create New Order");
        createOrderFrame.setSize(600, 400);
        createOrderFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        createOrderFrame.setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout());
        JTextArea stockArea = new JTextArea();
        stockArea.setEditable(false);

        // Load stock data from file
        List<Product> stockList = loadStockFromFile();
        StringBuilder stockText = new StringBuilder("Available Products:\n");
        for (Product product : stockList) {
            stockText.append(product).append("\n");
        }
        stockArea.setText(stockText.toString());
        panel.add(new JScrollPane(stockArea), BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        JLabel productIdLabel = new JLabel("Product ID:");
        JTextField productIdField = new JTextField();
        JLabel quantityLabel = new JLabel("Quantity:");
        JTextField quantityField = new JTextField();
        JButton submitButton = new JButton("Submit Order");

        inputPanel.add(productIdLabel);
        inputPanel.add(productIdField);
        inputPanel.add(quantityLabel);
        inputPanel.add(quantityField);
        inputPanel.add(new JLabel());
        inputPanel.add(submitButton);

        panel.add(inputPanel, BorderLayout.SOUTH);

        submitButton.addActionListener(e -> {
            String productId = productIdField.getText();
            int quantity = Integer.parseInt(quantityField.getText());

            Product product = stockList.stream()
                    .filter(p -> p.getId().equals(productId))
                    .findFirst()
                    .orElse(null);

            if (product != null && product.getQuantity() >= quantity) {
                product.setQuantity(product.getQuantity() - quantity);
                saveStockToFile(stockList);
                appendOrderToFile(new Order(productId, quantity));

                JOptionPane.showMessageDialog(createOrderFrame, "Order Created Successfully!");
                createOrderFrame.dispose();
            } else {
                JOptionPane.showMessageDialog(createOrderFrame, "Insufficient Stock or Invalid Product ID!");
            }
        });

        createOrderFrame.add(panel);
        createOrderFrame.setVisible(true);
    }

    private void openModifyOrderDialog() {
        JFrame modifyOrderFrame = new JFrame("Modify Existing Order");
        modifyOrderFrame.setSize(600, 400);
        modifyOrderFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        modifyOrderFrame.setLocationRelativeTo(null);

        List<Order> orderList = loadOrdersFromFile();
        JTextArea orderArea = new JTextArea();
        orderArea.setEditable(false);

        StringBuilder orderText = new StringBuilder("Current Orders:\n");
        for (int i = 0; i < orderList.size(); i++) {
            orderText.append(i + 1).append(". ").append(orderList.get(i)).append("\n");
        }
        orderArea.setText(orderText.toString());

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JScrollPane(orderArea), BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        JLabel orderIdLabel = new JLabel("Order ID (index):");
        JTextField orderIdField = new JTextField();
        JButton cancelButton = new JButton("Cancel Order");

        inputPanel.add(orderIdLabel);
        inputPanel.add(orderIdField);
        inputPanel.add(new JLabel());
        inputPanel.add(cancelButton);

        panel.add(inputPanel, BorderLayout.SOUTH);

        cancelButton.addActionListener(e -> {
            int orderId = Integer.parseInt(orderIdField.getText()) - 1;

            if (orderId >= 0 && orderId < orderList.size()) {
                orderList.remove(orderId);
                saveOrdersToFile(orderList);
                JOptionPane.showMessageDialog(modifyOrderFrame, "Order Canceled Successfully!");
                modifyOrderFrame.dispose();
            } else {
                JOptionPane.showMessageDialog(modifyOrderFrame, "Invalid Order ID!");
            }
        });

        modifyOrderFrame.add(panel);
        modifyOrderFrame.setVisible(true);
    }

    private void openTrackOrderDialog() {
        JFrame trackOrderFrame = new JFrame("Track Order Status");
        trackOrderFrame.setSize(600, 400);
        trackOrderFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        trackOrderFrame.setLocationRelativeTo(null);

        List<Order> orderList = loadOrdersFromFile();
        JTextArea orderStatusArea = new JTextArea();
        orderStatusArea.setEditable(false);

        StringBuilder orderStatusText = new StringBuilder("Order Status:\n");
        for (Order order : orderList) {
            orderStatusText.append(order).append("\n");
        }
        orderStatusArea.setText(orderStatusText.toString());

        trackOrderFrame.add(new JScrollPane(orderStatusArea));
        trackOrderFrame.setVisible(true);
    }

    private List<Product> loadStockFromFile() {
        List<Product> stockList = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(stockFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                stockList.add(new Product(parts[0], parts[1], Integer.parseInt(parts[2]), parts[3], parts[4]));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stockList;
    }

    private void saveStockToFile(List<Product> stockList) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(stockFile))) {
            for (Product product : stockList) {
                writer.write(product.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<Order> loadOrdersFromFile() {
        List<Order> orders = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(orderFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                orders.add(new Order(parts[0], Integer.parseInt(parts[1])));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return orders;
    }

    private void saveOrdersToFile(List<Order> orders) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(orderFile))) {
            for (Order order : orders) {
                writer.write(order.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void appendOrderToFile(Order order) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(orderFile, true))) {
            writer.write(order.toString());
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

// Supporting Order class
class Order {
    private final String productId;
    private final int quantity;

    public Order(String productId, int quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return productId + "," + quantity;
    }
}
