package dto;

import java.time.LocalDateTime;

public class OrderDataDTO {
    private String oId;
    private String mId;                 //주문자 ID
    private LocalDateTime oTime;
    private int seatNum;
    private LocalDateTime completeTime;
    private String oStatus;             //'PREPARING', 'COMPLETED'..
    private String requestment;
    private String payMethod;           //'CARD', 'CASH'

    //추가: 주문 목록 표시를 위한 총 금액(OrderController에서 계산)
    private int totalAmount;

    // 주문 접수용 생성자 (UI에서 데이터 입력 시 사용)
    public  OrderDataDTO(String mId, int seatNum, String requestment, String payMethod){
        this.mId = mId;
        this.seatNum = seatNum;
        this.requestment = requestment;
        this.payMethod = payMethod;
        this.oStatus = "PREPARING";
        this.oTime = LocalDateTime.now();
    }

    public OrderDataDTO(String oId, int seatNum, LocalDateTime oTime, String oStatus, int totalAmount){
        this.oId = oId;
        this.seatNum = seatNum;
        this.oTime = oTime;
        this.oStatus = oStatus;
        this.totalAmount = totalAmount;
    }

    public String getMId() {return mId;}

    public String getOId() {return oId;}
    public void setOId(String oId) {this.oId = oId;}

    public int getSeatNum() {return seatNum;}
    public LocalDateTime getOTime() {return oTime;}
    public LocalDateTime getCompleteTime() {return completeTime;}
    public void setCompleteTime(LocalDateTime completeTime) {this.completeTime = completeTime;}

    public String getOStatus() {return oStatus;}
    public void setOStatus(String oStatus) {this.oStatus = oStatus;}

    public String getPayMethod() {return payMethod;}
    public String getRequestment() {return requestment;}
    public int getTotalAmount() {return totalAmount;}
    public void setTotalAmount(int totalAmount) {this.totalAmount = totalAmount;}

    public long getCookingTime() {
        if (oTime == null) {return 0;}
        return java.time.Duration.between(oTime, LocalDateTime.now()).toMinutes();
    }

    public long getFinishCookingTime() {
        if (oTime == null || completeTime == null) {return 0;}
        return java.time.Duration.between(oTime, completeTime).toMinutes();
    }
}
