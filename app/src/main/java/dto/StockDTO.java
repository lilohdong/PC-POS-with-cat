package dto;

public class StockDTO {
    private String id;
    private String name;
    private String category;
    private int unitPrice;
    private int totalQuantity;
    private int minQuantity;
    private String location;

    public StockDTO() {}

    public StockDTO(String id, String name, String category, int unitPrice,
                         int totalQuantity, int minQuantity, String location) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.unitPrice = unitPrice;
        this.totalQuantity = totalQuantity;
        this.minQuantity = minQuantity;
        this.location = location;
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

    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }

    public int getUnitPrice() {
        return unitPrice;
    }
    public void setUnitPrice(int unitPrice) {
        this.unitPrice = unitPrice;
    }

    public int getTotalQuantity() {
        return totalQuantity;
    }
    public void setTotalQuantity(int totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public int getMinQuantity() {
        return minQuantity;
    }
    public void setMinQuantity(int minQuantity) {
        this.minQuantity = minQuantity;
    }

    public String getLocation() { return location; }
    public void setLocation(String location) {
        this.location = location;
    }
}