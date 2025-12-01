package client.store;

import util.Sizes;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class StorePanel extends JPanel {
    private static final int GRID_ROWS = 8;
    private static final int GRID_COLS = 10;
    private static final Color COLOR_AVAILABLE = new Color(200, 200, 200);
    private static final Color COLOR_CHILD_USER = new Color(180, 180, 255);
    private static final Color COLOR_ADULT_USER = new Color(255, 180, 180);
    private static final Color COLOR_SELECTED = new Color(255, 255, 150);
    private static final Color COLOR_NOTICE = new Color(150, 255, 150);
    private static final Color COLOR_MAINTENANCE = new Color(150, 150, 150);
    private static final Color COLOR_PREMIUM = new Color(255, 215, 0);

    private JPanel seatGridPanel;
    private Map<String, SeatPanel> seats;
    private JLabel statusLabel;
    private JLabel totalSeatsLabel;
    private JLabel availableSeatsLabel;
    private JLabel occupiedSeatsLabel;
    private JLabel revenueLabel;
    private SeatPanel selectedSeat;
    private Timer updateTimer;

    public StorePanel() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(Sizes.PANEL_WIDTH, Sizes.PANEL_HEIGHT));

        initializeComponents();
        setupSeats();
        updateStatistics();
        startTimer();
    }

    private void initializeComponents() {
        // 상단 헤더 패널
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 제목 및 시간
        JPanel titlePanel = new JPanel(new BorderLayout());

        JLabel titleLabel = new JLabel("◎ PC방 좌석 관리", JLabel.LEFT);
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 24));

        JLabel timeLabel = new JLabel();
        timeLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
        Timer timer = new Timer(1000, e -> {
            timeLabel.setText(LocalDateTime.now().format(
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        });
        timer.start();

        titlePanel.add(titleLabel, BorderLayout.WEST);
        titlePanel.add(timeLabel, BorderLayout.EAST);
        headerPanel.add(titlePanel, BorderLayout.NORTH);

        // 통계 정보 패널
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 5));
        statsPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        totalSeatsLabel = new JLabel("전체: 80석");
        availableSeatsLabel = new JLabel("이용가능: 0석");
        occupiedSeatsLabel = new JLabel("사용중: 0석");
        revenueLabel = new JLabel("일일매출: ₩0");

        totalSeatsLabel.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        availableSeatsLabel.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        occupiedSeatsLabel.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        revenueLabel.setFont(new Font("맑은 고딕", Font.BOLD, 14));

        totalSeatsLabel.setForeground(Color.BLACK);
        availableSeatsLabel.setForeground(new Color(0, 150, 0));
        occupiedSeatsLabel.setForeground(new Color(200, 0, 0));
        revenueLabel.setForeground(new Color(0, 0, 200));

        statsPanel.add(totalSeatsLabel);
        statsPanel.add(new JSeparator(JSeparator.VERTICAL));
        statsPanel.add(availableSeatsLabel);
        statsPanel.add(new JSeparator(JSeparator.VERTICAL));
        statsPanel.add(occupiedSeatsLabel);
        statsPanel.add(new JSeparator(JSeparator.VERTICAL));
        statsPanel.add(revenueLabel);

        headerPanel.add(statsPanel, BorderLayout.CENTER);

        // 범례 패널
        JPanel legendPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 5));
        legendPanel.add(createLegendItem("이용가능", COLOR_AVAILABLE));
        legendPanel.add(createLegendItem("미성년자", COLOR_CHILD_USER));
        legendPanel.add(createLegendItem("성인", COLOR_ADULT_USER));
        legendPanel.add(createLegendItem("프리미엄", COLOR_PREMIUM));
        legendPanel.add(createLegendItem("점검중", COLOR_MAINTENANCE));
        legendPanel.add(createLegendItem("선택됨", COLOR_SELECTED));

        headerPanel.add(legendPanel, BorderLayout.SOUTH);
        add(headerPanel, BorderLayout.NORTH);

        // 중앙 좌석 배치 패널
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        // 공지사항 영역
        JPanel noticeArea = new JPanel();
        noticeArea.setBackground(COLOR_NOTICE);
        noticeArea.setPreferredSize(new Dimension(0, 40));
        noticeArea.setBorder(BorderFactory.createLineBorder(new Color(0, 150, 0), 2));

        JLabel noticeLabel = new JLabel("📢 공지: 오늘 저녁 8시부터 10시까지 이벤트! 2시간 이용시 1시간 무료 추가!");
        noticeLabel.setFont(new Font("맑은 고딕", Font.BOLD, 13));
        noticeLabel.setForeground(new Color(0, 100, 0));
        noticeArea.add(noticeLabel);

        centerPanel.add(noticeArea, BorderLayout.NORTH);

        // 좌석 그리드 패널
        seatGridPanel = new JPanel(new GridLayout(GRID_ROWS, GRID_COLS, 3, 3));
        seatGridPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        seats = new HashMap<>();

        JScrollPane scrollPane = new JScrollPane(seatGridPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        // 하단 컨트롤 패널
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 상태 표시
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusLabel = new JLabel("좌석을 선택하세요");
        statusLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        statusPanel.add(statusLabel);

        bottomPanel.add(statusPanel, BorderLayout.WEST);

        // 버튼 패널
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));

        JButton startButton = createButton("사용 시작", new Color(146, 160, 250));
        JButton endButton = createButton("사용 종료", new Color(255, 150, 150));
        JButton moveButton = createButton("자리 이동", new Color(150, 200, 150));
        JButton chargeButton = createButton("시간 충전", new Color(255, 200, 100));
        JButton maintenanceButton = createButton("좌석 점검", new Color(200, 200, 200));
        JButton refreshButton = createButton("새로고침", new Color(180, 180, 180));

        startButton.addActionListener(e -> startUsingSeat());
        endButton.addActionListener(e -> endUsingSeat());
        moveButton.addActionListener(e -> moveSeat());
        chargeButton.addActionListener(e -> chargeTime());
        maintenanceButton.addActionListener(e -> toggleMaintenance());
        refreshButton.addActionListener(e -> refreshSeats());

        buttonPanel.add(startButton);
        buttonPanel.add(endButton);
        buttonPanel.add(moveButton);
        buttonPanel.add(chargeButton);
        buttonPanel.add(maintenanceButton);
        buttonPanel.add(refreshButton);

        bottomPanel.add(buttonPanel, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private JButton createButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(100, 35));
        button.setFont(new Font("맑은 고딕", Font.BOLD, 12));
        button.setBackground(bgColor);
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // 호버 효과
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(bgColor.brighter());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });

        return button;
    }

    private JPanel createLegendItem(String text, Color color) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 3, 0));

        JPanel colorBox = new JPanel();
        colorBox.setBackground(color);
        colorBox.setPreferredSize(new Dimension(20, 20));
        colorBox.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        JLabel label = new JLabel(text);
        label.setFont(new Font("맑은 고딕", Font.PLAIN, 12));

        panel.add(colorBox);
        panel.add(label);
        return panel;
    }

    private void setupSeats() {
        seatGridPanel.removeAll();
        seats.clear();

        for (int row = 0; row < GRID_ROWS; row++) {
            for (int col = 0; col < GRID_COLS; col++) {
                int seatNumber = row * GRID_COLS + col + 1;
                SeatPanel seat = new SeatPanel(seatNumber, row, col);

                // 초기 상태 설정 (데모 데이터)
                if (Math.random() < 0.3) {
                    if (Math.random() < 0.5) {
                        seat.setStatus(SeatStatus.OCCUPIED_CHILD);
                        seat.setUserInfo("학생", generateRandomTime());
                    } else {
                        seat.setStatus(SeatStatus.OCCUPIED_ADULT);
                        seat.setUserInfo("일반", generateRandomTime());
                    }
                } else if (Math.random() < 0.05) {
                    seat.setStatus(SeatStatus.MAINTENANCE);
                } else if (col == 0 || col == GRID_COLS - 1) {
                    seat.setStatus(SeatStatus.PREMIUM);
                }

                String key = seatNumber + "";
                seats.put(key, seat);
                seatGridPanel.add(seat);
            }
        }

        seatGridPanel.revalidate();
        seatGridPanel.repaint();
    }

    private String generateRandomTime() {
        int hours = (int)(Math.random() * 3);
        int minutes = (int)(Math.random() * 60);
        return String.format("%02d:%02d", hours, minutes);
    }

    private void updateStatistics() {
        int total = 0;
        int available = 0;
        int occupied = 0;
        int revenue = 0;

        for (SeatPanel seat : seats.values()) {
            total++;
            SeatStatus status = seat.getStatus();

            if (status == SeatStatus.AVAILABLE || status == SeatStatus.PREMIUM) {
                available++;
            } else if (status == SeatStatus.OCCUPIED_CHILD ||
                    status == SeatStatus.OCCUPIED_ADULT) {
                occupied++;
                revenue += (status == SeatStatus.OCCUPIED_ADULT) ? 1000 : 800;
            }
        }

        totalSeatsLabel.setText("전체: " + total + "석");
        availableSeatsLabel.setText("이용가능: " + available + "석");
        occupiedSeatsLabel.setText("사용중: " + occupied + "석");
        revenueLabel.setText(String.format("일일매출: ₩%,d", revenue * 30));
    }

    private void startTimer() {
        updateTimer = new Timer(60000, e -> { // 1분마다 업데이트
            for (SeatPanel seat : seats.values()) {
                if (seat.getStatus() == SeatStatus.OCCUPIED_CHILD ||
                        seat.getStatus() == SeatStatus.OCCUPIED_ADULT) {
                    seat.updateTime();
                }
            }
        });
        updateTimer.start();
    }

    private void startUsingSeat() {
        if (selectedSeat != null &&
                (selectedSeat.getStatus() == SeatStatus.AVAILABLE ||
                        selectedSeat.getStatus() == SeatStatus.PREMIUM)) {

            JDialog dialog = new JDialog((Frame)SwingUtilities.getWindowAncestor(this),
                    "좌석 사용 시작", true);
            dialog.setLayout(new BorderLayout());
            dialog.setSize(400, 300);
            dialog.setLocationRelativeTo(this);

            JPanel panel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            // 사용자 유형
            gbc.gridx = 0; gbc.gridy = 0;
            panel.add(new JLabel("사용자 유형:"), gbc);

            gbc.gridx = 1;
            JComboBox<String> userTypeCombo = new JComboBox<>(new String[]{"성인", "미성년자"});
            panel.add(userTypeCombo, gbc);

            // 이용 시간
            gbc.gridx = 0; gbc.gridy = 1;
            panel.add(new JLabel("이용 시간:"), gbc);

            gbc.gridx = 1;
            JComboBox<String> timeCombo = new JComboBox<>(new String[]{
                    "1시간", "2시간", "3시간", "5시간", "10시간", "무제한"
            });
            panel.add(timeCombo, gbc);

            // 회원 ID
            gbc.gridx = 0; gbc.gridy = 2;
            panel.add(new JLabel("회원 ID:"), gbc);

            gbc.gridx = 1;
            JTextField memberIdField = new JTextField(20);
            panel.add(memberIdField, gbc);

            dialog.add(panel, BorderLayout.CENTER);

            // 버튼 패널
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JButton confirmButton = new JButton("확인");
            JButton cancelButton = new JButton("취소");

            confirmButton.addActionListener(e -> {
                String userType = (String)userTypeCombo.getSelectedItem();
                if ("미성년자".equals(userType)) {
                    selectedSeat.setStatus(SeatStatus.OCCUPIED_CHILD);
                    selectedSeat.setUserInfo("학생", "00:00");
                } else {
                    selectedSeat.setStatus(SeatStatus.OCCUPIED_ADULT);
                    selectedSeat.setUserInfo("일반", "00:00");
                }

                statusLabel.setText("좌석 " + selectedSeat.getSeatNumber() +
                        "번 사용 시작 - " + userType);
                selectedSeat.setSelected(false);
                selectedSeat = null;
                updateStatistics();
                dialog.dispose();
            });

            cancelButton.addActionListener(e -> dialog.dispose());

            buttonPanel.add(confirmButton);
            buttonPanel.add(cancelButton);
            dialog.add(buttonPanel, BorderLayout.SOUTH);

            dialog.setVisible(true);
        }
    }

    private void endUsingSeat() {
        if (selectedSeat != null &&
                (selectedSeat.getStatus() == SeatStatus.OCCUPIED_CHILD ||
                        selectedSeat.getStatus() == SeatStatus.OCCUPIED_ADULT)) {

            int confirm = JOptionPane.showConfirmDialog(this,
                    "좌석 " + selectedSeat.getSeatNumber() + "번 사용을 종료하시겠습니까?\n" +
                            "사용 시간: " + selectedSeat.getTimeLabel(),
                    "사용 종료",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                // 요금 계산 표시
                JOptionPane.showMessageDialog(this,
                        "이용 요금: ₩" + calculateFee(selectedSeat.getTimeLabel()),
                        "정산",
                        JOptionPane.INFORMATION_MESSAGE);

                selectedSeat.setStatus(SeatStatus.AVAILABLE);
                selectedSeat.setUserInfo("", "");
                statusLabel.setText("좌석 " + selectedSeat.getSeatNumber() + "번 사용 종료");
                selectedSeat.setSelected(false);
                selectedSeat = null;
                updateStatistics();
            }
        }
    }

    private int calculateFee(String time) {
        // 간단한 요금 계산 (실제로는 더 복잡한 로직 필요)
        return (int)(Math.random() * 10000) + 1000;
    }

    private void moveSeat() {
        if (selectedSeat != null &&
                (selectedSeat.getStatus() == SeatStatus.OCCUPIED_CHILD ||
                        selectedSeat.getStatus() == SeatStatus.OCCUPIED_ADULT)) {
            statusLabel.setText("이동할 좌석을 선택하세요 (좌석 " +
                    selectedSeat.getSeatNumber() + "번에서 이동)");
        }
    }

    private void chargeTime() {
        if (selectedSeat != null &&
                (selectedSeat.getStatus() == SeatStatus.OCCUPIED_CHILD ||
                        selectedSeat.getStatus() == SeatStatus.OCCUPIED_ADULT)) {

            String input = JOptionPane.showInputDialog(this,
                    "충전할 시간을 입력하세요 (시간 단위):",
                    "시간 충전",
                    JOptionPane.QUESTION_MESSAGE);

            if (input != null && !input.isEmpty()) {
                try {
                    int hours = Integer.parseInt(input);
                    statusLabel.setText("좌석 " + selectedSeat.getSeatNumber() +
                            "번에 " + hours + "시간 충전 완료");
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this,
                            "올바른 숫자를 입력하세요",
                            "오류",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void toggleMaintenance() {
        if (selectedSeat != null) {
            if (selectedSeat.getStatus() == SeatStatus.MAINTENANCE) {
                selectedSeat.setStatus(SeatStatus.AVAILABLE);
                statusLabel.setText("좌석 " + selectedSeat.getSeatNumber() + "번 점검 완료");
            } else if (selectedSeat.getStatus() == SeatStatus.AVAILABLE) {
                selectedSeat.setStatus(SeatStatus.MAINTENANCE);
                statusLabel.setText("좌석 " + selectedSeat.getSeatNumber() + "번 점검 중");
            }
            selectedSeat.setSelected(false);
            selectedSeat = null;
            updateStatistics();
        }
    }

    private void refreshSeats() {
        updateStatistics();
        statusLabel.setText("좌석 정보를 새로고침했습니다");
    }

    // 좌석 패널 내부 클래스
    class SeatPanel extends JPanel {
        private int seatNumber;
        private int row, col;
        private SeatStatus status;
        private boolean selected;
        private JLabel numberLabel;
        private JLabel statusLabel;
        private JLabel timeLabel;
        private JPanel indicatorPanel;
        private String currentTime = "00:00";

        public SeatPanel(int seatNumber, int row, int col) {
            this.seatNumber = seatNumber;
            this.row = row;
            this.col = col;
            this.status = SeatStatus.AVAILABLE;
            this.selected = false;
            setForeground(Color.BLACK);
            setLayout(new BorderLayout());
            setBorder(new LineBorder(Color.GRAY, 1));
            setPreferredSize(new Dimension(80, 65));

            // 상단 표시기 (미성년자 표시)
            indicatorPanel = new JPanel();
            indicatorPanel.setPreferredSize(new Dimension(0, 8));
            indicatorPanel.setOpaque(false);
            add(indicatorPanel, BorderLayout.NORTH);

            // 중앙 정보 패널
            JPanel infoPanel = new JPanel(new GridLayout(3, 1, 0, 0));
            infoPanel.setOpaque(false);

            numberLabel = new JLabel("PC " + seatNumber, JLabel.CENTER);
            numberLabel.setFont(new Font("맑은 고딕", Font.BOLD, 11));

            statusLabel = new JLabel("", JLabel.CENTER);
            statusLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 9));

            timeLabel = new JLabel("", JLabel.CENTER);
            timeLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 9));

            infoPanel.add(numberLabel);
            infoPanel.add(statusLabel);
            infoPanel.add(timeLabel);

            add(infoPanel, BorderLayout.CENTER);

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    selectSeat();
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    setCursor(new Cursor(Cursor.HAND_CURSOR));
                    if (!selected) {
                        setBorder(new LineBorder(Color.BLUE, 2));
                    }
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                    if (!selected) {
                        setBorder(new LineBorder(Color.GRAY, 1));
                    }
                }
            });

            updateAppearance();
        }

        public void selectSeat() {
            if (selectedSeat != null) {
                selectedSeat.setSelected(false);
            }
            selectedSeat = this;
            setSelected(true);
            StorePanel.this.statusLabel.setText("좌석 " + seatNumber + "번 선택됨");
        }

        public void setStatus(SeatStatus status) {
            this.status = status;
            updateAppearance();
        }

        public void setUserInfo(String userType, String time) {
            statusLabel.setText(userType);
            timeLabel.setText(time);
            currentTime = time;
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
            if (selected) {
                setBorder(new LineBorder(Color.BLUE, 3));
            } else {
                setBorder(new LineBorder(Color.GRAY, 1));
            }
            updateAppearance();
        }

        public void updateTime() {
            if (!currentTime.isEmpty() && !currentTime.equals("무제한")) {
                // 시간 업데이트 로직
                String[] parts = currentTime.split(":");
                int hours = Integer.parseInt(parts[0]);
                int minutes = Integer.parseInt(parts[1]);
                minutes++;
                if (minutes >= 60) {
                    hours++;
                    minutes = 0;
                }
                currentTime = String.format("%02d:%02d", hours, minutes);
                timeLabel.setText(currentTime);
            }
        }

        public SeatStatus getStatus() {
            return status;
        }

        public int getSeatNumber() {
            return seatNumber;
        }

        public String getTimeLabel() {
            return currentTime;
        }

        private void updateAppearance() {
            indicatorPanel.setOpaque(false);

            if (selected) {
                setBackground(COLOR_SELECTED);
            } else {
                switch (status) {
                    case AVAILABLE:
                        setBackground(COLOR_AVAILABLE);
                        statusLabel.setText("이용가능");
                        timeLabel.setText("");
                        break;
                    case OCCUPIED_CHILD:
                        setBackground(COLOR_CHILD_USER);
                        indicatorPanel.setOpaque(true);
                        indicatorPanel.setBackground(Color.YELLOW);
                        break;
                    case OCCUPIED_ADULT:
                        setBackground(COLOR_ADULT_USER);
                        break;
                    case PREMIUM:
                        setBackground(COLOR_PREMIUM);
                        statusLabel.setText("프리미엄");
                        timeLabel.setText("");
                        break;
                    case MAINTENANCE:
                        setBackground(COLOR_MAINTENANCE);
                        statusLabel.setText("점검중");
                        timeLabel.setText("");
                        break;
                }
            }
        }
    }

    // 좌석 상태 열거형
    enum SeatStatus {
        AVAILABLE, OCCUPIED_CHILD, OCCUPIED_ADULT, PREMIUM, MAINTENANCE
    }
}
