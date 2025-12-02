package dto;

public class PricePlanDTO {
    private int planId;
    private String planName;
    private int durationMin;
    private int price;

    public PricePlanDTO(int planId, String planName, int durationMin, int price) {
        this.planId = planId;
        this.planName = planName;
        this.durationMin = durationMin;
        this.price = price;
    }

    public int getPlanId() { return planId; }
    public int getDurationMin() { return durationMin; }
    public int getPrice() { return price; }

    @Override
    public String toString() {
        return planName + " (" + price + "원)";
    }
}
