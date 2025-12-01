package dto;

public class SeatDTO {
    private int seatNo;
    private boolean isUsed;
    private String memberId;
    private String startTime;
    private String endTime;

    public SeatDTO(int seatNo, boolean isUsed, String memberId, String startTime, String endTime) {
        this.seatNo = seatNo;
        this.isUsed = isUsed;
        this.memberId = memberId;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public int getSeatNo() { return seatNo; }
    public boolean isUsed() { return isUsed; }
    public String getMemberId() { return memberId; }
    public String getStartTime() { return startTime; }
    public String getEndTime() { return endTime; }
}

