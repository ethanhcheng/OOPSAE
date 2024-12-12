package Customer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


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
            stockText.append(product.getId()).append(", ")
                     .append(product.getName()).append(", ")
                     .append(product.getQuantity()).append("\n");
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
            new customer(folderPath);
        });

        submitButton.addActionListener(e -> {
            String productId = productIdField.getText().trim();
            int quantity;

            try {
                quantity = Integer.parseInt(quantityField.getText().trim());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(createOrderFrame, "Please enter a valid quantity.");
                return;
            }

            Product product = stockList.stream()
                    .filter(p -> p.getId().equals(productId))
                    .findFirst()
                    .orElse(null);

            if (product != null && product.getQuantity() >= quantity) {
                String orderId = generateOrderId();
                String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                appendOrderToFile(new Order(orderId, productId, quantity, timestamp));

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
        viewOrderFrame.setSize(800, 600);
        viewOrderFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        viewOrderFrame.setLocationRelativeTo(null);

        // Define column names
        String[] orderColumns = {"Order ID", "Product ID", "Product Name", "Quantity", "Order Date"};
        DefaultTableModel orderTableModel = new DefaultTableModel(orderColumns, 0);
        JTable orderTable = new JTable(orderTableModel);

        // Populate the table with orders
        updateOrderTableModel(orderTableModel, loadOrdersFromFile());

        JScrollPane orderScrollPane = new JScrollPane(orderTable);
        orderScrollPane.setBorder(BorderFactory.createTitledBorder("Order List"));

        // Input Panel
        JPanel inputPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        JTextField orderIdField = new JTextField();
        JButton modifyButton = new JButton("Modify Order");
        JButton trackButton = new JButton("Track Order");

        inputPanel.add(new JLabel("Order ID:"));
        inputPanel.add(orderIdField);
        inputPanel.add(modifyButton);
        inputPanel.add(trackButton);

        modifyButton.addActionListener(e -> {
            String orderId = orderIdField.getText().trim();
            if (orderId.isEmpty()) {
                JOptionPane.showMessageDialog(viewOrderFrame, "Please enter a valid Order ID.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            openModifyOrderDialog(orderId, viewOrderFrame, orderTableModel);
        });

        trackButton.addActionListener(e -> {
            String orderId = orderIdField.getText().trim();
            if (orderId.isEmpty()) {
                JOptionPane.showMessageDialog(viewOrderFrame, "Please enter a valid Order ID.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            openTrackOrderDialog(orderId, viewOrderFrame);
        });

        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> {
            viewOrderFrame.dispose();
            new CustomerInterface(folderPath, "create");
        });

        // Main Panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(orderScrollPane, BorderLayout.CENTER);
        mainPanel.add(inputPanel, BorderLayout.SOUTH);
        mainPanel.add(backButton, BorderLayout.NORTH);

        viewOrderFrame.add(mainPanel);
        viewOrderFrame.setVisible(true);
    }



    private void openModifyOrderDialog(String orderId, JFrame parentFrame, DefaultTableModel orderTableModel) {
        JFrame modifyOrderFrame = new JFrame("Modify Order");
        modifyOrderFrame.setSize(500, 400);
        modifyOrderFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        modifyOrderFrame.setLocationRelativeTo(null);

        List<Order> orderList = loadOrdersFromFile();
        Order selectedOrder = orderList.stream()
                .filter(order -> order.getOrderId().equals(orderId))
                .findFirst()
                .orElse(null);

        if (selectedOrder == null) {
            JOptionPane.showMessageDialog(modifyOrderFrame, "Order not found!");
            modifyOrderFrame.dispose();
            parentFrame.setVisible(true);
            return;
        }

        // Main Panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));

        // Order Details Section
        JTextArea orderDetailsArea = new JTextArea(selectedOrder.toString());
        orderDetailsArea.setEditable(false);
        orderDetailsArea.setBorder(BorderFactory.createTitledBorder("Order Details"));

        // Input Section for quantity
        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        JLabel quantityLabel = new JLabel("Quantity:");
        JTextField quantityField = new JTextField(String.valueOf(selectedOrder.getQuantity()));
        JButton updateButton = new JButton("Update Quantity");
        JButton cancelButton = new JButton("Cancel Order");
        JButton backButton = new JButton("Back");

        inputPanel.add(quantityLabel);
        inputPanel.add(quantityField);
        inputPanel.add(updateButton);
        inputPanel.add(cancelButton);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(backButton);

        // Update Button Action
        updateButton.addActionListener(e -> {
            int newQuantity;
            try {
                newQuantity = Integer.parseInt(quantityField.getText().trim());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(modifyOrderFrame, "Please enter a valid quantity.");
                return;
            }

            if (newQuantity <= 0) {
                JOptionPane.showMessageDialog(modifyOrderFrame, "Quantity cannot be less than or equal to 0.");
                return;
            }

            // Update the order in the list and file
            selectedOrder.setQuantity(newQuantity);
            saveOrdersToFile(orderList);

            // Update the table in the parent frame
            updateOrderTableModel(orderTableModel, orderList);

            JOptionPane.showMessageDialog(modifyOrderFrame, "Order quantity updated successfully!");
            modifyOrderFrame.dispose();
            parentFrame.setVisible(true);
        });

        // Cancel Button Action
        cancelButton.addActionListener(e -> {
            cancelOrder(orderId, orderList);
            saveOrdersToFile(orderList);

            // Update the table in the parent frame
            updateOrderTableModel(orderTableModel, orderList);

            JOptionPane.showMessageDialog(modifyOrderFrame, "Order canceled successfully!");
            modifyOrderFrame.dispose();
            parentFrame.setVisible(true);
        });

        // Back Button Action
        backButton.addActionListener(e -> {
            modifyOrderFrame.dispose();
            parentFrame.setVisible(true);
        });

        // Add Components to Main Panel
        mainPanel.add(new JScrollPane(orderDetailsArea), BorderLayout.CENTER);
        mainPanel.add(inputPanel, BorderLayout.SOUTH);
        mainPanel.add(buttonPanel, BorderLayout.NORTH);

        modifyOrderFrame.add(mainPanel);
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

    private void cancelOrder(String orderId, List<Order> orderList) {
      // Remove the order with the matching ID
      orderList.removeIf(order -> order.toString().startsWith(orderId));

      // Save the updated orders back to order_list.txt
      saveOrdersToFile(orderList);
      JOptionPane.showMessageDialog(null, "Order cancelled successfully!");
    }

    private List<Order> loadOrdersFromFile() {
        List<Order> orders = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(orderFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                // Ensure the line has all required fields
                if (parts.length == 4) {
                    orders.add(new Order(parts[0], parts[1], Integer.parseInt(parts[2]), parts[3]));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return orders;
    }
    
    private void updateOrderTableModel(DefaultTableModel orderTableModel, List<Order> orderList) {
        List<Product> stockList = loadStockFromFile();

        // Clear existing rows
        orderTableModel.setRowCount(0);

        // Add updated rows
        for (Order order : orderList) {
            String productId = order.getProductId();
            String productName = stockList.stream()
                                          .filter(p -> p.getId().equals(productId))
                                          .map(Product::getName)
                                          .findFirst()
                                          .orElse("Unknown Product");

            orderTableModel.addRow(new Object[]{
                order.getOrderId(),
                productId,
                productName,
                order.getQuantity(),
                order.getTimestamp()
            });
        }
    }

    
    private List<Product> loadStockFromFile() {
        List<Product> stockList = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(stockFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Split the line by commas
                String[] parts = line.split(",");
                // Ensure there are at least three columns: ID, Name, Quantity
                if (parts.length >= 3) {
                    String id = parts[0].trim();
                    String name = parts[1].trim();
                    int quantity = Integer.parseInt(parts[2].trim());
                    // Add a Product object with only the first three columns
                    stockList.add(new Product(id, name, quantity));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            System.err.println("Invalid number format in inventory file. Please check the file.");
        }
        return stockList;
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
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            writer.write(String.format("%s,%s,%d,%s%n", order.getOrderId(), order.getProductId(), order.getQuantity(), timestamp));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private String generateOrderId() {
        // Generate a random three-digit number
        Random random = new Random();
        int orderId = 100 + random.nextInt(900); // Ensures a number between 100 and 999

        return String.format("%03d", orderId);
    }

}
