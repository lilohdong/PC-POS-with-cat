package client.store.view;

import client.store.StoreController;

import javax.swing.*;
import java.awt.*;

public class StorePanel extends JPanel {
    private StoreController controller;

    private StoreHeaderPanel headerPanel;
    private SeatGridPanel gridPanel;
    private StoreControlPanel controlPanel;
    private Timer timer, secondTimer;
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
        secondTimer = new Timer(1000, e -> {
            headerPanel.updateCurrentTime();  // yyyy-MM-dd HH:mm:ss 실시간 갱신
        });
        secondTimer.start();
        // 기존 타이머를 60초(1분) 주기로 통합 + 안정성 강화
        timer = new Timer(60000, e -> {  // 1000ms → 60000ms (1분)
            // 1. 매분마다 DB의 remain_time 갱신 + 시간 만료 자동 종료 처리
            controller.onTimerTick();
            controller.refreshSeats();
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