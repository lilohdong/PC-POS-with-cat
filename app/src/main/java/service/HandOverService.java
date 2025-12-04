package service;

import dao.HandOverDAO;
import dto.HandOverDTO;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class HandOverService {

    private HandOverDAO dao;

    public HandOverService() {
        this.dao = new HandOverDAO();
    }

    // 내부 클래스 삭제하고 DTO를 바로 리턴하도록 수정
    public HandOverDTO getInitialData() {
        HandOverDTO lastDto = dao.getLastHandoverData();

        // 데이터가 없으면(첫 오픈) 기본값 설정
        if (lastDto == null) {
            lastDto = new HandOverDTO();
            lastDto.setReceiverId("사장님"); // 초기 Giver
            lastDto.setEndTime(Timestamp.valueOf(LocalDateTime.now().withHour(0).withMinute(0)));
            lastDto.setCashReserve(0);
        }
        return lastDto;
    }

    // 매출 데이터
    public Map<String, Integer> getSales(Timestamp start, Timestamp end) {
        return dao.getSalesData(start, end);
    }

    // 저장
    public boolean save(HandOverDTO dto) {
        return dao.insertHandover(dto);
    }

    // 금고 조회/갱신
    public int getCashSafe() {
        return dao.getCashSafe();
    }


    // 금고 금액 + 업무 차액(diff) 누적을 동시에 갱신
    public void updateCashSafe(int amount, int diffDelta) {
        dao.updateCashSafe(amount, diffDelta);
    }


    // 직원 목록 불러오기 (콤보박스용)
    public List<String> getStaffNames() {
        return dao.getStaffNames();
    }

    // 직원 비밀번호 검증
    public boolean checkStaffPassword(String name, String pw) {
        return dao.verifyStaffPassword(name, pw);
    }
}
