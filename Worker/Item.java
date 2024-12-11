package Worker;

public class Item {
	 private String productId;
	 private String itemId;
	 private String status;
	 private String userId;

	 public Item(String productId, String itemId, String status, String userId) {
	     this.productId = productId;
	     this.itemId = itemId;
	     this.status = status;
	     this.userId = userId;
	 }

	 // Getters and setters
	 public String getProductId() { return productId; }
	 public String getItemId() { return itemId; }
	 public String getStatus() { return status; }
	 public String getUserId() { return userId; }
	
	 public void setStatus(String status) { this.status = status; }
	 public void setUserId(String userId) { this.userId = userId; }
}

