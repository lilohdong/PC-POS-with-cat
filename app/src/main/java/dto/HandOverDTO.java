package dto;

import java.sql.Timestamp;

public class HandOverDTO {
    private int hoId;
    private String giverId;
    private String receiverId;
    private Timestamp startTime;
    private Timestamp endTime;
    private int totalSales;
    private int cashSales;
    private int cardSales;
    private int cashReserve; // 다음 사람에게 넘기는 시재 (실제 금고 금액)
    private String memo;

    public HandOverDTO() {}

    // Getters and Setters
    public int getHoId() { return hoId; }
    public void setHoId(int hoId) { this.hoId = hoId; }
    public String getGiverId() { return giverId; }
    public void setGiverId(String giverId) { this.giverId = giverId; }
    public String getReceiverId() { return receiverId; }
    public void setReceiverId(String receiverId) { this.receiverId = receiverId; }
    public Timestamp getStartTime() { return startTime; }
    public void setStartTime(Timestamp startTime) { this.startTime = startTime; }
    public Timestamp getEndTime() { return endTime; }
    public void setEndTime(Timestamp endTime) { this.endTime = endTime; }
    public int getTotalSales() { return totalSales; }
    public void setTotalSales(int totalSales) { this.totalSales = totalSales; }
    public int getCashSales() { return cashSales; }
    public void setCashSales(int cashSales) { this.cashSales = cashSales; }
    public int getCardSales() { return cardSales; }
    public void setCardSales(int cardSales) { this.cardSales = cardSales; }
    public int getCashReserve() { return cashReserve; }
    public void setCashReserve(int cashReserve) { this.cashReserve = cashReserve; }
    public String getMemo() { return memo; }
    public void setMemo(String memo) { this.memo = memo; }
}
