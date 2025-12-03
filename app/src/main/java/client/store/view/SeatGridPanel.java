package client.store.view;

import client.store.SeatStatus;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class SeatGridPanel extends JPanel {
    private static final int GRID_ROWS = 8;
    private static final int GRID_COLS = 10;

    // 좌석 버튼들을 관리할 리스트
    private List<SeatButton> seatButtons = new ArrayList<>();

    public SeatGridPanel() {
        setLayout(new GridLayout(GRID_ROWS, GRID_COLS, 3, 3));
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
    }

    // 좌석 버튼 초기화 (화면 갱신 전 기존 버튼 제거)
    public void clearSeats() {
        this.removeAll();
        this.seatButtons.clear();
        this.revalidate();
        this.repaint();
    }

    // 좌석 버튼 추가
    public void addSeat(SeatButton btn) {
        this.seatButtons.add(btn);
        this.add(btn);
    }

    // 현재 등록된 모든 좌석 버튼 반환 (Controller용)
    public List<SeatButton> getSeatButtons() {
        return seatButtons;
    }

    // 통계 계산을 위한 메서드 (전체, 이용가능, 사용중) 반환
    public int[] countStatus() {
        int total = seatButtons.size();
        int available = 0;
        int occupied = 0;

        for (SeatButton btn : seatButtons) {
            SeatStatus status = btn.getStatus();
            if (status == SeatStatus.AVAILABLE) {
                available++;
            } else if (status == SeatStatus.OCCUPIED_CHILD || status == SeatStatus.OCCUPIED_ADULT) {
                occupied++;
            }
        }
        return new int[]{total, available, occupied};
    }
}
