package client.store;

import client.store.view.StorePanel;
import client.store.view.SeatButton;
import dto.MemberDTO;
import dto.PricePlanDTO;
import dto.SeatDTO;
import dto.SeatMemberInfoDTO;
import dao.MemberDAO;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class StoreController {
    private StorePanel view;
    private StoreService service;
    private SeatButton selectedSeat;

    public StoreController(StorePanel view) {
        this.view = view;
        this.service = StoreService.getInstance();
    }

    // 전체 좌석 새로고침
    public void refreshSeats() {
        List<SeatDTO> seats = service.getAllSeats();
        view.getGridPanel().clearSeats();

        for (SeatDTO dto : seats) {
            SeatButton btn = new SeatButton(dto.getSeatNo());
            bindSeatData(btn, dto);

            btn.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    selectSeat(btn);
                }
            });

            view.getGridPanel().addSeat(btn);
        }
        view.updateStatistics();
    }

    // 좌석 데이터 바인딩
    private void bindSeatData(SeatButton btn, SeatDTO dto) {
        if (dto.isUnavailable()) {
            btn.updateStatus(SeatStatus.UNAVAILABLE, null, null);
        } else if (!dto.isUsed()) {
            btn.updateStatus(SeatStatus.AVAILABLE, null, null);
        } else {
            SeatMemberInfoDTO info = service.getSeatDetail(dto.getSeatNo());
            if (info != null) {
                SeatStatus status = info.isMinor() ? SeatStatus.OCCUPIED_CHILD : SeatStatus.OCCUPIED_ADULT;
                LocalDateTime start = info.getLoginTime().toLocalDateTime();
                String remainStr = service.calculateRemainTimeStr(start, info.getSavedRemainTime());
                btn.updateStatus(status, info.getName(), remainStr);
            }
        }
    }

    // 좌석 선택
    private void selectSeat(SeatButton target) {
        if (selectedSeat != null) {
            selectedSeat.setSelected(false);
        }

        if (selectedSeat == target) {
            selectedSeat = null;
            view.updateStatusLabel("선택 해제됨");
        } else {
            selectedSeat = target;
            selectedSeat.setSelected(true);
            view.updateStatusLabel("좌석 " + target.getSeatNumber() + "번 선택됨");
        }
    }

    // 타이머 틱 처리
    public void onTimerTick() {
        for (SeatButton btn : view.getGridPanel().getSeatButtons()) {
            if (btn.getStatus() == SeatStatus.OCCUPIED_CHILD ||
                    btn.getStatus() == SeatStatus.OCCUPIED_ADULT) {

                SeatMemberInfoDTO info = service.getSeatDetail(btn.getSeatNumber());
                if (info != null) {
                    String remain = service.calculateRemainTimeStr(
                            info.getLoginTime().toLocalDateTime(),
                            info.getSavedRemainTime()
                    );
                    btn.updateStatus(btn.getStatus(), info.getName(), remain);
                }
            }
        }
    }

    // 사용 시작 핸들러
    public void handleStart() {
        if (selectedSeat == null) {
            view.showMsg("좌석을 선택해주세요.");
            return;
        }

        if (selectedSeat.getStatus() != SeatStatus.AVAILABLE) {
            view.showMsg("이용 가능한 좌석이 아닙니다.");
            return;
        }

        String memberId = JOptionPane.showInputDialog(view, "회원 ID를 입력하세요:");
        if (memberId == null || memberId.trim().isEmpty()) {
            return;
        }

        String result = service.startSeat(selectedSeat.getSeatNumber(), memberId.trim());

        switch (result) {
            case "SUCCESS":
                view.showMsg("좌석 사용을 시작했습니다.");
                refreshSeats();
                break;
            case "INVALID_MEMBER":
                view.showMsg("존재하지 않는 회원입니다.");
                break;
            case "EMPTY_ID":
                view.showMsg("회원 ID를 입력해주세요.");
                break;
            default:
                view.showMsg("좌석 시작에 실패했습니다.");
        }
    }

    // 사용 종료 핸들러
    public void handleEnd() {
        if (selectedSeat == null) {
            view.showMsg("좌석을 선택해주세요.");
            return;
        }

        if (selectedSeat.getStatus() != SeatStatus.OCCUPIED_CHILD &&
                selectedSeat.getStatus() != SeatStatus.OCCUPIED_ADULT) {
            view.showMsg("사용중인 좌석이 아닙니다.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                view,
                "좌석 " + selectedSeat.getSeatNumber() + "번 사용을 종료하시겠습니까?",
                "사용 종료 확인",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        String result = service.endSeat(selectedSeat.getSeatNumber());

        switch (result) {
            case "SUCCESS":
                view.showMsg("좌석 사용을 종료했습니다.");
                refreshSeats();
                break;
            case "NOT_IN_USE":
                view.showMsg("사용중인 좌석이 아닙니다.");
                break;
            case "DEDUCT_FAILED":
                view.showMsg("시간 차감에 실패했습니다.");
                break;
            default:
                view.showMsg("좌석 종료에 실패했습니다.");
        }
    }

    // 좌석 정보 핸들러
    public void handleSeatInfo() {
        if (selectedSeat == null) {
            view.showMsg("좌석을 선택해주세요.");
            return;
        }

        if (selectedSeat.getStatus() != SeatStatus.OCCUPIED_CHILD &&
                selectedSeat.getStatus() != SeatStatus.OCCUPIED_ADULT) {
            view.showMsg("사용중인 좌석이 아닙니다.");
            return;
        }

        SeatMemberInfoDTO info = service.getSeatDetail(selectedSeat.getSeatNumber());
        if (info == null) {
            view.showMsg("좌석 정보를 가져올 수 없습니다.");
            return;
        }

        MemberDTO member = MemberDAO.getInstance().getMemberById(info.getmId());
        if (member == null) {
            view.showMsg("회원 정보를 가져올 수 없습니다.");
            return;
        }

        // 시간 계산
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = info.getLoginTime().toLocalDateTime();

        long usedMinutes = Duration.between(start, now).toMinutes();
        long remainMinutes = info.getSavedRemainTime() - usedMinutes;
        LocalDateTime endTime = now.plusMinutes(remainMinutes);

        // 시간 포맷터
        DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("HH:mm");
        DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // 정보 구성
        StringBuilder sb = new StringBuilder();
        sb.append("══════════ 좌석 정보 ══════════\n\n");
        sb.append("좌석 번호: ").append(info.getSeatNo()).append("번\n");
        sb.append("회원 ID: ").append(member.getmId()).append("\n");
        sb.append("이름: ").append(member.getName()).append("\n");
        sb.append("생년월일: ").append(member.getBirth().toString()).append("\n");
        sb.append("성별: ").append(member.getSex()).append("\n");
        sb.append("연락처: ").append(member.getPhone()).append("\n");
        sb.append("구분: ").append(info.isMinor() ? "미성년자" : "성인").append("\n");
        sb.append("가입일: ").append(member.getJoinDate().toLocalDateTime().format(dateFmt)).append("\n");
        sb.append("\n────────────────────────\n\n");
        sb.append("시작 시간: ").append(start.format(timeFmt)).append("\n");
        sb.append("사용 시간: ").append(service.formatTime(usedMinutes)).append("\n");
        sb.append("남은 시간: ").append(service.formatTime(remainMinutes)).append("\n");
        sb.append("종료 예정: ").append(endTime.format(timeFmt)).append("\n");

        JOptionPane.showMessageDialog(view, sb.toString(), "좌석 상세 정보", JOptionPane.INFORMATION_MESSAGE);
    }

    // 시간 충전 핸들러
    public void handleCharge() {
        if (selectedSeat == null) {
            view.showMsg("좌석을 선택해주세요.");
            return;
        }

        if (selectedSeat.getStatus() != SeatStatus.OCCUPIED_CHILD &&
                selectedSeat.getStatus() != SeatStatus.OCCUPIED_ADULT) {
            view.showMsg("사용중인 좌석이 아닙니다.");
            return;
        }

        SeatMemberInfoDTO info = service.getSeatDetail(selectedSeat.getSeatNumber());
        if (info == null) {
            view.showMsg("좌석 정보를 가져올 수 없습니다.");
            return;
        }

        List<PricePlanDTO> plans = service.getPricePlans();
        if (plans.isEmpty()) {
            view.showMsg("이용 가능한 요금제가 없습니다.");
            return;
        }

        PricePlanDTO selectedPlan = (PricePlanDTO) JOptionPane.showInputDialog(
                view,
                "충전할 요금제를 선택하세요:",
                "시간 충전",
                JOptionPane.QUESTION_MESSAGE,
                null,
                plans.toArray(),
                plans.get(0)
        );

        if (selectedPlan == null) {
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                view,
                selectedPlan.toString() + "을(를) 충전하시겠습니까?",
                "충전 확인",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        boolean success = service.chargeTime(
                info.getmId(),
                selectedPlan.getPlanId(),
                selectedPlan.getPrice()
        );

        if (success) {
            view.showMsg("시간 충전이 완료되었습니다.");
            refreshSeats();
        } else {
            view.showMsg("시간 충전에 실패했습니다.");
        }
    }

    // 이용 불가 토글 핸들러
    public void handleAvailability() {
        if (selectedSeat == null) {
            view.showMsg("좌석을 선택해주세요.");
            return;
        }

        SeatStatus status = selectedSeat.getStatus();
        boolean isCurrentlyUnavailable = (status == SeatStatus.UNAVAILABLE);

        String action = isCurrentlyUnavailable ? "이용 가능" : "이용 불가";
        int confirm = JOptionPane.showConfirmDialog(
                view,
                "좌석 " + selectedSeat.getSeatNumber() + "번을 " + action + "로 설정하시겠습니까?",
                action + " 설정",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        String result = service.toggleSeatAvailability(
                selectedSeat.getSeatNumber(),
                !isCurrentlyUnavailable
        );

        if (result.equals("SUCCESS")) {
            view.showMsg("좌석이 " + action + "로 설정되었습니다.");
            refreshSeats();
        } else {
            view.showMsg("좌석 상태 변경에 실패했습니다.");
        }
    }

    public SeatButton getSelectedSeat() {
        return selectedSeat;
    }
}