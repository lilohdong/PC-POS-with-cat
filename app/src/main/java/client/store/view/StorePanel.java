package client.store.view;

import client.store.StoreController;

import javax.swing.*;
import java.awt.*;

public class StorePanel extends JPanel {
    private StoreController controller;

    private StoreHeaderPanel headerPanel;
    private SeatGridPanel gridPanel;
    private StoreControlPanel controlPanel;
    private Timer timer;
    private int timerTick = 0;

    public StorePanel() {
        setLayout(new BorderLayout());

        // 컴포넌트 생성
        headerPanel = new StoreHeaderPanel();
        gridPanel = new SeatGridPanel();
        controlPanel = new StoreControlPanel();

        // 레이아웃 배치
        add(headerPanel, BorderLayout.NORTH);
        add(new JScrollPane(gridPanel), BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);

        // 컨트롤러 연결
        this.controller = new StoreController(this);

        // 초기화
        initEventLinks();
        controller.refreshSeats();
        startGlobalTimer();
    }

    private void initEventLinks() {
        // 하단 버튼 이벤트를 컨트롤러로 연결
        controlPanel.getStartBtn().addActionListener(e -> controller.handleStart());
        controlPanel.getEndBtn().addActionListener(e -> controller.handleEnd());
        controlPanel.getInfoBtn().addActionListener(e -> controller.handleSeatInfo());
        controlPanel.getChargeBtn().addActionListener(e -> controller.handleCharge());
        controlPanel.getAvailBtn().addActionListener(e -> controller.handleAvailability());
        controlPanel.getRefreshBtn().addActionListener(e -> controller.refreshSeats());
    }

    private void startGlobalTimer() {
        // 1초마다 실행
        timer = new Timer(1000, e -> {
            timerTick++;

            // 매초 시계 갱신
            headerPanel.updateCurrentTime();

            // 60초마다 좌석 시간 갱신
            if (timerTick % 60 == 0) {
                controller.onTimerTick();
                timerTick = 0;
            }
        });
        timer.start();
    }

    // Getter 메서드
    public SeatGridPanel getGridPanel() {
        return gridPanel;
    }

    // UI 업데이트 메서드
    public void updateStatusLabel(String text) {
        controlPanel.setStatusText(text);
    }

    public void showMsg(String msg) {
        JOptionPane.showMessageDialog(this, msg);
    }

    public void updateStatistics() {
        int[] counts = gridPanel.countStatus();
        headerPanel.updateStats(counts[0], counts[1], counts[2]);
    }

    // 패널이 닫힐 때 타이머 정리
    public void cleanup() {
        if (timer != null && timer.isRunning()) {
            timer.stop();
        }
    }
}