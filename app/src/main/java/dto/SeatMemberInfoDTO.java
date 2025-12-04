package dto;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;

public class SeatMemberInfoDTO {
    private int seatNo;
    private String mId;
    private String name;
    private Timestamp birth;
    private int savedRemainTime; // DB에 저장된 남은 시간 (분)
    private Timestamp loginTime;

    public SeatMemberInfoDTO(int seatNo, String mId, String name, Timestamp birth, int savedRemainTime, Timestamp loginTime) {
        this.seatNo = seatNo;
        this.mId = mId;
        this.name = name;
        this.birth = birth;
        this.savedRemainTime = savedRemainTime;
        this.loginTime = loginTime;
    }

    // 미성년자 여부 판단 로직
    public boolean isMinor() {
        if (birth == null) return false;
        LocalDate birthDate = birth.toLocalDateTime().toLocalDate();
        LocalDate now = LocalDate.now();
        return Period.between(birthDate, now).getYears() < 19;
    }



    // Getter methods
    public int getSeatNo() { return seatNo; }
    public String getmId() { return mId; }
    public String getName() { return name; }
    public Timestamp getLoginTime() { return loginTime; }
    public int getSavedRemainTime() { return savedRemainTime; }
}
