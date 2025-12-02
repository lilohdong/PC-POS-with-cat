package client.store.view;

import util.Sizes;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.sql.*;

import db.DBConnection;
import dao.SeatDAO;
import dto.SeatDTO;
import dao.MemberDAO;
import dto.SeatMemberInfoDTO;
import dto.PricePlanDTO;

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
        JButton infoButton = createButton("회원 정보", new Color(200, 230, 255));
        JButton chargeButton = createButton("시간 충전", new Color(255, 200, 100));
        JButton availableButton = createButton("이용 불가", new Color(200, 200, 200));
        JButton refreshButton = createButton("새로고침", new Color(180, 180, 180));

        startButton.addActionListener(e -> startUsingSeat());
        endButton.addActionListener(e -> endUsingSeat());
        infoButton.addActionListener(e -> showMemberInfoPopup());
        chargeButton.addActionListener(e -> chargeTime());
        availableButton.addActionListener(e -> toggleAvailable());
        refreshButton.addActionListener(e -> refreshSeats());

        buttonPanel.add(startButton);
        buttonPanel.add(endButton);
        buttonPanel.add(infoButton);
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
                // [요구사항 2 & 3] 사용 중: 상세 정보 조회하여 미성년자 색상 및 남은 시간 표시
                // SeatDTO에는 기본 정보만 있으므로, 상세 정보를 위해 SeatMemberInfoDTO를 조회
                // (성능상 한 번에 조인된 쿼리를 가져오는 것이 좋으나, 기존 구조 유지를 위해 여기서 호출)
                SeatMemberInfoDTO info = SeatDAO.getInstance().getSeatMemberInfo(dto.getSeatNo());

                if (info != null) {
                    // 미성년자 여부에 따른 색상 설정
                    if (info.isMinor()) {
                        panel.setStatus(SeatStatus.OCCUPIED_CHILD);
                    } else {
                        panel.setStatus(SeatStatus.OCCUPIED_ADULT);
                    }

                    // 좌석 패널에 정보 주입 (회원이름, 시작시간, DB저장 잔여시간)
                    panel.setUserInfo(info.getName(), dto.getStartTime(), info.getSavedRemainTime());
                } else {
                    // 예외 상황: 사용중인데 회원정보가 없는 경우 성인 처리
                    panel.setStatus(SeatStatus.OCCUPIED_ADULT);
                    panel.setUserInfo(dto.getMemberId(), dto.getStartTime(), 0);
                }
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

    // 분을 "HH:mm" 포맷 문자열로 변환하는 헬퍼 메서드
    private String formatDuration(long totalMinutes) {
        if (totalMinutes < 0) totalMinutes = 0; // 음수 방지
        long hours = totalMinutes / 60;
        long minutes = totalMinutes % 60;
        return String.format("%02d:%02d", hours, minutes);
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
            JOptionPane.showMessageDialog(this, "회원 ID를 입력해야 합니다 ");
            return;
        }

        if (!MemberDAO.isMemberIdValid(memberId)) {
            // 등록되지 않은 ID일 경우 알림창을 띄우고 함수 종료
            JOptionPane.showMessageDialog(
                    this,
                    "해당 ID (" + memberId + ")는 등록된 회원이 아닙니다 ",
                    "회원 정보 오류",
                    JOptionPane.WARNING_MESSAGE
            );
            return; // 유효하지 않으면 여기서 함수 실행을 중단
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

    private void showMemberInfoPopup() {
        if (!checkSelection()) return;

        // 사용 중인 좌석이 아니면 정보가 없음
        if (selectedSeat.getStatus() == SeatStatus.AVAILABLE || selectedSeat.getStatus() == SeatStatus.UNAVAILABLE) {
            JOptionPane.showMessageDialog(this, "사용 중인 좌석이 아닙니다 ");
            return;
        }

        // DB 뷰에서 정보 가져오기
        SeatMemberInfoDTO info = SeatDAO.getInstance().getSeatMemberInfo(selectedSeat.getSeatNumber());
        if (info == null) {
            JOptionPane.showMessageDialog(this, "정보를 불러올 수 없습니다 ");
            return;
        }

        // 시간 계산 로직
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = info.getLoginTime().toLocalDateTime();

        long usedMinutes = Duration.between(start, now).toMinutes(); // 사용 시간(분)
        int totalRemainInDb = info.getSavedRemainTime(); // DB 저장 잔여 시간(분)

        // 실제 남은 시간 = (보유 시간) - (현재까지 사용 시간)
        long currentRealRemain = totalRemainInDb - usedMinutes;

        // 종료 예정 시간 계산
        LocalDateTime endTime = now.plusMinutes(currentRealRemain);

        // 포맷터 설정
        DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("HH:mm");

        // [요구사항 1] 모든 시간 포맷 00:00 형태로 수정
        StringBuilder sb = new StringBuilder();
        sb.append("좌석 번호: ").append(info.getSeatNo()).append("\n");
        sb.append("회원 ID: ").append(info.getmId()).append("\n");
        sb.append("이름: ").append(info.getName()).append("\n");
        sb.append("구분: ").append(info.isMinor() ? "미성년자" : "성인").append("\n");
        sb.append("----------------------------\n");
        sb.append("시작 시간: ").append(start.format(timeFmt)).append("\n"); // 12:00
        sb.append("사용 시간: ").append(formatDuration(usedMinutes)).append("\n"); // 01:30
        sb.append("남은 시간: ").append(formatDuration(currentRealRemain)).append("\n"); // 00:45
        sb.append("종료 예정: ").append(endTime.format(timeFmt)); // 14:15

        JOptionPane.showMessageDialog(this, sb.toString(), "회원 상세 정보", JOptionPane.INFORMATION_MESSAGE);
    }

    private void chargeTime() {
        if (!checkSelection()) return;

        // 사용 중인 좌석인지 확인
        if (selectedSeat.getStatus() != SeatStatus.OCCUPIED_CHILD &&
                selectedSeat.getStatus() != SeatStatus.OCCUPIED_ADULT) {
            JOptionPane.showMessageDialog(this, "사용 중인 좌석만 충전 가능합니다 ");
            return;
        }

        // DB에서 현재 좌석 정보(회원ID 필요) 가져오기
        SeatMemberInfoDTO info = SeatDAO.getInstance().getSeatMemberInfo(selectedSeat.getSeatNumber());
        if (info == null) return;

        // DB에서 요금제 목록 가져오기
        List<PricePlanDTO> plans = SeatDAO.getInstance().getPricePlans();
        if (plans.isEmpty()) {
            JOptionPane.showMessageDialog(this, "등록된 요금제가 없습니다 ");
            return;
        }

        // 요금제 선택 콤보박스 팝업
        PricePlanDTO selectedPlan = (PricePlanDTO) JOptionPane.showInputDialog(
                this,
                "충전할 요금제를 선택하세요:",
                "시간 충전",
                JOptionPane.QUESTION_MESSAGE,
                null,
                plans.toArray(),
                plans.get(0)
        );

        if (selectedPlan != null) {
            // 트랜잭션 처리 (로그 저장 + 시간 충전)
            boolean success = SeatDAO.getInstance().chargeTimeTransaction(
                    info.getmId(),
                    info.getSeatNo(),
                    selectedPlan
            );

            if (success) {
                JOptionPane.showMessageDialog(this, selectedPlan.toString() + " 충전이 완료되었습니다 ");
                refreshSeats(); // 화면 갱신
            } else {
                JOptionPane.showMessageDialog(this, "충전 처리에 실패했습니다 ");
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
        setupSeats();
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
        private JLabel nameLabel; // 회원 이름 표시용으로 변경
        private JLabel timeLabel; // 남은 시간 표시용으로 변경
        private LocalDateTime startDateTime;
        private int dbSavedRemainTime; // DB에 저장되어 있던 총 잔여시간 (분)

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

            // [요구사항 2] 회원이름 표시 라벨
            nameLabel = new JLabel("", JLabel.CENTER);
            nameLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 10)); // 이름이 길 수 있으므로 폰트 약간 축소
            nameLabel.setForeground(Color.BLACK);

            // [요구사항 2] 남은 시간 표시 라벨
            timeLabel = new JLabel("", JLabel.CENTER);
            timeLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 10));
            timeLabel.setForeground(Color.BLACK);

            infoPanel.add(numberLabel);
            infoPanel.add(nameLabel);
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

        // [요구사항 2] 사용자 정보 세팅 (이름, 시작시간, DB저장 잔여시간)
        public void setUserInfo(String memberName, String startTimeStr, int remainTime) {
            this.dbSavedRemainTime = remainTime;
            nameLabel.setText(memberName); // 회원 이름 표시

            if (startTimeStr != null && !startTimeStr.isEmpty()) {
                try {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    if (startTimeStr.length() > 19) startTimeStr = startTimeStr.substring(0, 19);
                    this.startDateTime = LocalDateTime.parse(startTimeStr, formatter);
                    updateTime();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
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

        // [요구사항 2] 남은 시간 표시 로직
        public void updateTime() {
            if (this.startDateTime != null && (status == SeatStatus.OCCUPIED_CHILD || status == SeatStatus.OCCUPIED_ADULT)) {
                LocalDateTime now = LocalDateTime.now();

                // 사용한 시간(분)
                long usedMinutes = Duration.between(startDateTime, now).toMinutes();

                // 남은 시간 = DB저장값 - 사용시간
                long currentRemain = dbSavedRemainTime - usedMinutes;

                // 음수 처리 (시간 초과 시 마이너스 대신 00:00 혹은 붉은색 표시 등 정책 필요, 여기선 00:00)
                // if (currentRemain < 0) currentRemain = 0;

                // 00:00 형태로 변환하여 표시
                timeLabel.setText(formatDuration(currentRemain));

                // (선택사항) 시간이 다 되었으면 붉은색으로 텍스트 변경
                if (currentRemain <= 0) timeLabel.setForeground(Color.RED);
                else timeLabel.setForeground(Color.BLACK);

            } else {
                timeLabel.setText("");
                nameLabel.setText("");
            }
        }

        public SeatStatus getStatus() {
            return status;
        }

        public int getSeatNumber() {
            return seatNumber;
        }

        private void updateAppearance() {
            if (selected) {
                setBackground(COLOR_SELECTED);
            } else {
                switch (status) {
                    case AVAILABLE:
                        setBackground(COLOR_AVAILABLE);
                        nameLabel.setText("이용가능");
                        timeLabel.setText("");
                        break;
                    case OCCUPIED_CHILD:
                        setBackground(COLOR_CHILD_USER); // [요구사항 3] 미성년자 색상 적용
                        break;
                    case OCCUPIED_ADULT:
                        setBackground(COLOR_ADULT_USER); // [요구사항 3] 성인 색상 적용
                        break;
                    case UNAVAILABLE:
                        setBackground(COLOR_UNAVAILABLE);
                        nameLabel.setText("이용불가");
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
