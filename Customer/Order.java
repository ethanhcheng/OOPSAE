package Customer;

public class Order {
    private final String productId;
    private final int quantity;

    public Order(String productId, int quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

    public String getProductId() {
        return productId;
    }

    public int getQuantity() {
        return quantity;
    }

    @Override
    public String toString() {
        return productId + "," + quantity;
    }
}