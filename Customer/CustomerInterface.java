package Customer;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import Worker.Product;

public class CustomerInterface extends JFrame {
    private final File stockFile;
    private final File orderFile;
    private final File statusFile;
    private final String folderPath;

    public CustomerInterface(String folderPath, String mode) {
        this.folderPath = folderPath;
        this.stockFile = new File("inventory.txt");
        this.orderFile = new File("order_list.txt");
        this.statusFile = new File("Status.txt");

        if ("create".equalsIgnoreCase(mode)) {
            openCreateOrderDialog();
        } else if ("view".equalsIgnoreCase(mode)) {
            openViewOrderDialog();
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

        List<Product> stockList = loadStockFromFile();
        StringBuilder stockText = new StringBuilder("Available Products:\nID, Name, Quantity\n");
        for (Product product : stockList) {
            stockText.append(product.getId()).append(", ").append(product.getName()).append(", ").append(product.getQuantity()).append("\n");
        }
        stockArea.setText(stockText.toString());
        panel.add(new JScrollPane(stockArea), BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        JLabel productIdLabel = new JLabel("Product ID:");
        JTextField productIdField = new JTextField();
        JLabel quantityLabel = new JLabel("Quantity:");
        JTextField quantityField = new JTextField();
        JButton submitButton = new JButton("Submit Order");
        JButton backButton = new JButton("Back");

        inputPanel.add(productIdLabel);
        inputPanel.add(productIdField);
        inputPanel.add(quantityLabel);
        inputPanel.add(quantityField);
        inputPanel.add(backButton);
        inputPanel.add(submitButton);

        panel.add(inputPanel, BorderLayout.SOUTH);

        backButton.addActionListener(e -> {
            createOrderFrame.dispose();
            CustomerInterface.this.dispose();
            new customer(folderPath);
        });

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
                appendStatusToFile(productId, quantity);

                JOptionPane.showMessageDialog(createOrderFrame, "Order Created Successfully!");
                createOrderFrame.dispose();
                new customer(folderPath);
            } else {
                JOptionPane.showMessageDialog(createOrderFrame, "Insufficient Stock or Invalid Product ID!");
            }
        });

        createOrderFrame.add(panel);
        createOrderFrame.setVisible(true);
    }

    private void openViewOrderDialog() {
        JFrame viewOrderFrame = new JFrame("View All Orders");
        viewOrderFrame.setSize(600, 400);
        viewOrderFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        viewOrderFrame.setLocationRelativeTo(null);

        JTextArea orderArea = new JTextArea();
        orderArea.setEditable(false);

        StringBuilder orderText = new StringBuilder("Current Orders:\n");
        // Check if the order file exists
        if (!orderFile.exists()) {
            orderText.append("No orders have been created yet.");
        } else {
            List<Order> orderList = loadOrdersFromFile();
            if (orderList.isEmpty()) {
                orderText.append("No orders have been created yet.");
            } else {
                for (Order order : orderList) {
                    orderText.append(order).append("\n");
                }
            }
        }
        
        orderArea.setText(orderText.toString());
        orderArea.revalidate();
        orderArea.repaint();


        JLabel orderIdLabel = new JLabel("Enter Order ID:");
        JTextField orderIdField = new JTextField();
        JButton modifyButton = new JButton("Modify Order");
        JButton trackButton = new JButton("Track Order");
        JButton backButton = new JButton("Back");

        modifyButton.addActionListener(e -> {
            String orderId = orderIdField.getText();
            openModifyOrderDialog(orderId, viewOrderFrame);
        });

        trackButton.addActionListener(e -> {
            String orderId = orderIdField.getText();
            openTrackOrderDialog(orderId, viewOrderFrame);
        });

        backButton.addActionListener(e -> {
            viewOrderFrame.dispose();
            CustomerInterface.this.dispose();
            new customer(folderPath);
        });

        JPanel inputPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        inputPanel.add(orderIdLabel);
        inputPanel.add(orderIdField);
        inputPanel.add(modifyButton);
        inputPanel.add(trackButton);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(new JScrollPane(orderArea), BorderLayout.CENTER);
        mainPanel.add(inputPanel, BorderLayout.SOUTH);
        mainPanel.add(backButton, BorderLayout.NORTH);

        viewOrderFrame.add(mainPanel);
        viewOrderFrame.setVisible(true);
    }

    private void openModifyOrderDialog(String orderId, JFrame parentFrame) {
        JFrame modifyOrderFrame = new JFrame("Modify Order");
        modifyOrderFrame.setSize(400, 300);
        modifyOrderFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        modifyOrderFrame.setLocationRelativeTo(null);

        List<Order> orderList = loadOrdersFromFile();
        JTextArea orderArea = new JTextArea();
        orderArea.setEditable(false);

        StringBuilder orderText = new StringBuilder("Order Details:\n");
        for (Order order : orderList) {
            if (order.toString().startsWith(orderId)) {
                orderText.append(order).append("\n");
            }
        }
        orderArea.setText(orderText.toString());

        JButton cancelButton = new JButton("Cancel Order");
        JButton backButton = new JButton("Back");

        cancelButton.addActionListener(e -> {
            cancelOrder(orderId);
            modifyOrderFrame.dispose();
            parentFrame.setVisible(true);
        });

        backButton.addActionListener(e -> {
            modifyOrderFrame.dispose();
            parentFrame.setVisible(true);
        });

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JScrollPane(orderArea), BorderLayout.CENTER);
        panel.add(cancelButton, BorderLayout.WEST);
        panel.add(backButton, BorderLayout.EAST);

        modifyOrderFrame.add(panel);
        modifyOrderFrame.setVisible(true);
    }

    private void openTrackOrderDialog(String orderId, JFrame parentFrame) {
        JFrame trackOrderFrame = new JFrame("Track Order");
        trackOrderFrame.setSize(400, 300);
        trackOrderFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        trackOrderFrame.setLocationRelativeTo(null);

        JTextArea trackArea = new JTextArea();
        trackArea.setEditable(false);

        StringBuilder trackText = new StringBuilder("Order Tracking:\n");
        try (BufferedReader reader = new BufferedReader(new FileReader(statusFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith(orderId)) {
                    trackText.append(line).append("\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        trackArea.setText(trackText.toString());

        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> {
            trackOrderFrame.dispose();
            parentFrame.setVisible(true);
        });

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JScrollPane(trackArea), BorderLayout.CENTER);
        panel.add(backButton, BorderLayout.SOUTH);

        trackOrderFrame.add(panel);
        trackOrderFrame.setVisible(true);
    }

    private void cancelOrder(String orderId) {
        List<Order> orders = loadOrdersFromFile();
        List<Product> stockList = loadStockFromFile();

        for (Order order : orders) {
            if (order.toString().startsWith(orderId)) {
                Product product = stockList.stream()
                        .filter(p -> p.getId().equals(order.getProductId()))
                        .findFirst()
                        .orElse(null);
                if (product != null) {
                    product.setQuantity(product.getQuantity() + order.getQuantity());
                }
            }
        }
        saveStockToFile(stockList);
        saveOrdersToFile(orders);
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

    private void appendStatusToFile(String productId, int quantity) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(statusFile, true))) {
            String orderId = generateOrderId();
            String dateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            writer.write(String.format("%s,%s,%d,placed,%s%n", orderId, productId, quantity, dateTime));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String generateOrderId() {
        File orderIdFile = new File(folderPath, "order_id.txt");
        int orderId = 1;

        if (orderIdFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(orderIdFile))) {
                String lastOrderId = reader.readLine();
                if (lastOrderId != null) {
                    orderId = Integer.parseInt(lastOrderId) + 1;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(orderIdFile))) {
            writer.write(String.valueOf(orderId));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return String.format("%03d", orderId);
    }
}
