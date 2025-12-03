package client.store;

import dao.MemberDAO;
import dao.SeatDAO;
import dto.PricePlanDTO;
import dto.SeatDTO;
import dto.SeatMemberInfoDTO;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class StoreService {
    private static StoreService instance = new StoreService();
    private StoreService() {}
    public static StoreService getInstance() { return instance; }

    private SeatDAO seatDAO = SeatDAO.getInstance();
    private MemberDAO memberDAO = MemberDAO.getInstance();

    // 전체 좌석 상태 조회
    public List<SeatDTO> getAllSeats() {
        return seatDAO.getAllSeats();
    }

    // 좌석 상세 정보 조회
    public SeatMemberInfoDTO getSeatDetail(int seatNo) {
        return seatDAO.getSeatMemberInfo(seatNo);
    }

    // 남은 시간 계산
    public String calculateRemainTimeStr(LocalDateTime startTime, int savedRemainMin) {
        long usedMin = Duration.between(startTime, LocalDateTime.now()).toMinutes();
        long remainMin = savedRemainMin - usedMin;
        return formatTime(remainMin);
    }

    // 시간 포맷팅 (HH:MM)
    public String formatTime(long totalMinutes) {
        if (totalMinutes < 0) totalMinutes = 0;
        long h = totalMinutes / 60;
        long m = totalMinutes % 60;
        return String.format("%02d:%02d", h, m);
    }

    // 사용 시작 처리
    public String startSeat(int seatNo, String memberId) {
        // 회원 유효성 검사
        if (memberId == null || memberId.trim().isEmpty()) {
            return "EMPTY_ID";
        }

        if (!memberDAO.isMemberIdValid(memberId)) {
            return "INVALID_MEMBER";
        }

        // 좌석 시작
        boolean success = seatDAO.startSeat(seatNo, memberId);
        return success ? "SUCCESS" : "FAILED";
    }

    // 사용 종료 처리
    public String endSeat(int seatNo) {
        // 좌석 정보 조회
        SeatMemberInfoDTO info = seatDAO.getSeatMemberInfo(seatNo);
        if (info == null) {
            return "NOT_IN_USE";
        }

        // 사용 시간 계산
        LocalDateTime startTime = info.getLoginTime().toLocalDateTime();
        long usedMin = Duration.between(startTime, LocalDateTime.now()).toMinutes();

        // 회원 시간 차감
        boolean deducted = seatDAO.deductUsedTime(info.getmId(), (int) usedMin);
        if (!deducted) {
            return "DEDUCT_FAILED";
        }

        // 좌석 종료
        boolean ended = seatDAO.endSeat(seatNo);
        return ended ? "SUCCESS" : "FAILED";
    }

    // 요금제 목록 조회
    public List<PricePlanDTO> getPricePlans() {
        return seatDAO.getPricePlans();
    }

    // 시간 충전
    public boolean chargeTime(String mId, int planId, int amount) {
        return seatDAO.chargeTimeTransaction(mId, planId, amount);
    }

    // 좌석 이용 불가 설정/해제
    public String toggleSeatAvailability(int seatNo, boolean makeUnavailable) {
        boolean success = seatDAO.toggleSeatAvailability(seatNo, makeUnavailable);
        return success ? "SUCCESS" : "FAILED";
    }

    // 회원 ID 유효성 검사
    public boolean isValidMember(String memberId) {
        return memberDAO.isMemberIdValid(memberId);
    }

    // 좌석이 사용 가능한지 확인
    public boolean isSeatAvailable(SeatDTO seat) {
        return !seat.isUnavailable() && !seat.isUsed();
    }

    // 좌석이 사용중인지 확인
    public boolean isSeatInUse(SeatDTO seat) {
        return seat.isUsed() && !seat.isUnavailable();
    }
}