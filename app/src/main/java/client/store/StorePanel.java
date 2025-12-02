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
import java.util.List;
import java.sql.*;

import db.DBConnection;
import dao.SeatDAO;
import dto.SeatDTO;

public class StorePanel extends JPanel {
    private static final int GRID_ROWS = 8;
    private static final int GRID_COLS = 10;
    private static final Color COLOR_AVAILABLE = new Color(200, 200, 200);
    private static final Color COLOR_UNAVAILABLE = new Color(150, 150, 150);
    private static final Color COLOR_CHILD_USER = new Color(255, 180, 180);
    private static final Color COLOR_ADULT_USER = new Color(180, 180, 255);
    private static final Color COLOR_SELECTED = new Color(255, 255, 150);

    private JPanel seatGridPanel;
    private Map<String, SeatPanel> seats;
    private JLabel statusLabel;
    private JLabel totalSeatsLabel;
    private JLabel availableSeatsLabel;
    private JLabel occupiedSeatsLabel;
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

        JLabel titleLabel = new JLabel("매장 관리", JLabel.LEFT);
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

        totalSeatsLabel.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        availableSeatsLabel.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        occupiedSeatsLabel.setFont(new Font("맑은 고딕", Font.BOLD, 14));

        totalSeatsLabel.setForeground(Color.BLACK);
        availableSeatsLabel.setForeground(new Color(0, 150, 0));
        occupiedSeatsLabel.setForeground(new Color(200, 0, 0));

        statsPanel.add(totalSeatsLabel);
        statsPanel.add(new JSeparator(JSeparator.VERTICAL));
        statsPanel.add(availableSeatsLabel);
        statsPanel.add(new JSeparator(JSeparator.VERTICAL));
        statsPanel.add(occupiedSeatsLabel);

        headerPanel.add(statsPanel, BorderLayout.CENTER);

        // 범례 패널
        JPanel legendPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 5));
        legendPanel.add(createLegendItem("이용가능", COLOR_AVAILABLE));
        legendPanel.add(createLegendItem("미성년자", COLOR_CHILD_USER));
        legendPanel.add(createLegendItem("성인", COLOR_ADULT_USER));
        legendPanel.add(createLegendItem("이용불가", COLOR_UNAVAILABLE));
        legendPanel.add(createLegendItem("선택됨", COLOR_SELECTED));

        headerPanel.add(legendPanel, BorderLayout.SOUTH);
        add(headerPanel, BorderLayout.NORTH);

        // 중앙 좌석 배치 패널
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

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
        JButton chargeButton = createButton("시간 충전", new Color(255, 200, 100));
        JButton availableButton = createButton("이용 불가", new Color(200, 200, 200));
        JButton refreshButton = createButton("새로고침", new Color(180, 180, 180));

        startButton.addActionListener(e -> startUsingSeat());
        endButton.addActionListener(e -> endUsingSeat());
        chargeButton.addActionListener(e -> chargeTime());
        availableButton.addActionListener(e -> toggleAvailable());
        refreshButton.addActionListener(e -> refreshSeats());

        buttonPanel.add(startButton);
        buttonPanel.add(endButton);
        buttonPanel.add(chargeButton);
        buttonPanel.add(availableButton);
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

        List<SeatDTO> seatList = SeatDAO.getInstance().getAllSeats();

        for (SeatDTO dto : seatList) {
            SeatPanel panel = new SeatPanel(dto.getSeatNo(), 0, 0);

            if (dto.isUnavailable()) {
                panel.setStatus(SeatStatus.UNAVAILABLE);
            } else if (!dto.isUsed()) {
                panel.setStatus(SeatStatus.AVAILABLE);
            } else {
                // 사용 중
                panel.setStatus(SeatStatus.OCCUPIED_ADULT);  // 기본값 (미성년 정보 DB에 없다면)
                panel.setUserInfo(dto.getMemberId(), convertTime(dto.getStartTime()));
            }

            seats.put(String.valueOf(dto.getSeatNo()), panel);
            seatGridPanel.add(panel);
        }

        seatGridPanel.revalidate();
        seatGridPanel.repaint();
    }

    private void updateStatistics() {
        int total = 0;
        int available = 0;
        int occupied = 0;

        for (SeatPanel seat : seats.values()) {
            total++;
            SeatStatus status = seat.getStatus();

            if (status == SeatStatus.AVAILABLE) {
                available++;
            } else if (status == SeatStatus.OCCUPIED_CHILD ||
                    status == SeatStatus.OCCUPIED_ADULT) {
                occupied++;
            }
        }

        totalSeatsLabel.setText("전체: " + total + "석");
        availableSeatsLabel.setText("이용가능: " + available + "석");
        occupiedSeatsLabel.setText("사용중: " + occupied + "석");
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

    private String convertTime(String dateTime) {
        if (dateTime == null) return "";
        return dateTime.substring(11, 16); // "YYYY-MM-DD HH:MM:SS" → "HH:MM"
    }

    private boolean checkSelection() {
        if (selectedSeat == null) {
            JOptionPane.showMessageDialog(this, "좌석을 선택하세요!");
            return false;
        }
        return true;
    }

    private void startUsingSeat() {
        if (!checkSelection()) return;

        // --- 회원 ID 입력받기 ---
        String memberId = JOptionPane.showInputDialog(
                this,
                "회원 ID를 입력하세요",
                "좌석 사용 시작",
                JOptionPane.QUESTION_MESSAGE
        );

        if (memberId == null || memberId.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "회원 ID를 입력해야 합니다.");
            return;
        }

        int seatNo = selectedSeat.getSeatNumber();

        String sql = "UPDATE seat SET is_used = 1, is_unavailable = 0, m_id = ?, login_time = NOW(), end_time = NULL WHERE seat_no = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, memberId);
            pstmt.setInt(2, seatNo);
            pstmt.executeUpdate();

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        setupSeats();
        updateStatistics();
    }

    private int calcUsedMinutes(String startTime, String endTime) {
        try {
            LocalDateTime start = LocalDateTime.parse(startTime.replace(" ", "T"));
            LocalDateTime end = LocalDateTime.parse(endTime.replace(" ", "T"));
            return (int) java.time.Duration.between(start, end).toMinutes();
        } catch (Exception e) {
            return 0;
        }
    }

    private void endUsingSeat() {
        if (!checkSelection()) return;

        int seatNo = selectedSeat.getSeatNumber();

        String sql = "UPDATE seat SET is_used = 0, end_time = NOW(), m_id = NULL WHERE seat_no = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, seatNo);
            pstmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }

        setupSeats();
        updateStatistics();
    }

    private int calculateFee(String time) {
        // 간단한 요금 계산 (실제로는 더 복잡한 로직 필요)
        return (int)(Math.random() * 10000) + 1000;
    }

    private void chargeTime() {
        if (!checkSelection()) return;

        if (selectedSeat.getStatus() == SeatStatus.OCCUPIED_CHILD ||
                        selectedSeat.getStatus() == SeatStatus.OCCUPIED_ADULT) {

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

    private void toggleAvailable() {
        if (!checkSelection()) return;

        boolean unavailable = (selectedSeat.getStatus() != SeatStatus.UNAVAILABLE);
        int seatNo = selectedSeat.getSeatNumber();

        String sql = "UPDATE seat SET is_unavailable = ? WHERE seat_no = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setBoolean(1, unavailable);
            pstmt.setInt(2, seatNo);
            pstmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }

        selectedSeat.setSelected(false);

        selectedSeat.setStatus(unavailable ? SeatStatus.UNAVAILABLE : SeatStatus.AVAILABLE);
        selectedSeat = null;

        statusLabel.setText(unavailable ?
                "좌석 " + seatNo + "번 점검 등록됨" :
                "좌석 " + seatNo + "번 점검 해제됨");

        updateStatistics();
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

            // 중앙 정보 패널
            JPanel infoPanel = new JPanel(new GridLayout(3, 1, 0, 0));
            infoPanel.setOpaque(false);

            numberLabel = new JLabel("PC " + seatNumber, JLabel.CENTER);
            numberLabel.setFont(new Font("맑은 고딕", Font.BOLD, 11));
            numberLabel.setForeground(Color.BLACK);

            statusLabel = new JLabel("", JLabel.CENTER);
            statusLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 9));
            statusLabel.setForeground(Color.BLACK);

            timeLabel = new JLabel("", JLabel.CENTER);
            timeLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 9));
            timeLabel.setForeground(Color.BLACK);

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
            // 이미 선택된 좌석을 다시 클릭 → 선택 해제
            if (selectedSeat == this) {
                setSelected(false);
                selectedSeat = null;
                StorePanel.this.statusLabel.setText("좌석 선택 해제됨");
                return;
            }

            // 다른 좌석 선택 중이면 선택 해제
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
                        break;
                    case OCCUPIED_ADULT:
                        setBackground(COLOR_ADULT_USER);
                        break;
                    case UNAVAILABLE:
                        setBackground(COLOR_UNAVAILABLE);
                        statusLabel.setText("이용불가");
                        timeLabel.setText("");
                        break;
                }
            }
        }
    }

    // 좌석 상태 열거형
    enum SeatStatus {
        AVAILABLE, UNAVAILABLE, OCCUPIED_CHILD, OCCUPIED_ADULT
    }
}
