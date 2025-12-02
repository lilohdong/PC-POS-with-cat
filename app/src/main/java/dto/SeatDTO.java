package dto;

public class SeatDTO {
    private int seatNo;
    private boolean isUsed;
    private boolean isUnavailable;
    private String memberId;
    private String startTime;
    private String endTime;

    public SeatDTO(int seatNo, boolean isUsed, boolean isUnavailable, String memberId, String startTime, String endTime) {
        this.seatNo = seatNo;
        this.isUsed = isUsed;
        this.isUnavailable = isUnavailable;
        this.memberId = memberId;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public int getSeatNo() { return seatNo; }
    public boolean isUnavailable() { return isUnavailable; }
    public boolean isUsed() { return isUsed; }
    public String getMemberId() { return memberId; }
    public String getStartTime() { return startTime; }
    public String getEndTime() { return endTime; }
}

