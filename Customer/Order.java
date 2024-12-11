package Customer;

public class Order {
    private String orderId;
    private String productId;
    private int quantity;
    private String timestamp;

    public Order(String orderId, String productId, int quantity, String timestamp) {
        this.orderId = orderId;
        this.productId = productId;
        this.quantity = quantity;
        this.timestamp = timestamp;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getProductId() {
        return productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return String.format("%s,%s,%d,%s", orderId, productId, quantity, timestamp);
    }
}
