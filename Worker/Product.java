package Worker;

public class Product {
    private String id;
    private String name;
    private int quantity;
    private String location;
    private String supplierId;

    public Product(String id, String name, int quantity, String location, String supplierId) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.location = location;
        this.supplierId = supplierId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(String supplierId) {
        this.supplierId = supplierId;
    }

    @Override
    public String toString() {
        return id + "," + name + "," + quantity + "," + location + "," + supplierId;
    }
}
