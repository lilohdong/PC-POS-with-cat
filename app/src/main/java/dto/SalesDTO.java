package dto;

public class SalesDTO {
    private String salesId;
    private String memberId;
    private String salesDate;
    private String salesTime;
    private String product;
    private int quantity;
    private int price;

    public SalesDTO() {}

    public SalesDTO(String salesId, String memberId, String salesDate, String salesTime, String product, int quantity, int price) {
        this.salesId=salesId;
        this.memberId=memberId;
        this.salesDate=salesDate;
        this.salesTime=salesTime;
        this.product=product;
        this.price=price;
    }
    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getSalesDate() {
        return salesDate;
    }

    public void setSalesDate(String salesDate) {
        this.salesDate = salesDate;
    }

    public String getSalesTime() {
        return salesTime;
    }

    public void setSalesTime(String salesTime) {
        this.salesTime = salesTime;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    public String getSalesId() {
        return salesId;
    }

    public void setSalesId(String salesId) {
        this.salesId = salesId;
    }
}
