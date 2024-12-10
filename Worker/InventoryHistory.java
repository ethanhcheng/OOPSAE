package Worker;

import java.time.LocalDateTime;

public class InventoryHistory {
    private String productId;
    private String action;
    private int quantity;
    private LocalDateTime timestamp;

    public InventoryHistory(String productId, String action, int quantity) {
        this.productId = productId;
        this.action = action;
        this.quantity = quantity;
        this.timestamp = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return timestamp + " - " + action + " " + quantity + " of Product ID: " + productId;
    }
}

