package client.game;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class GameStatisticsUI extends JFrame {

    private JTable statisticsTable;
    private JTable gameTable;
    private DefaultTableModel statisticsModel;
    private DefaultTableModel gameModel;

    public GameStatisticsUI() {
        setTitle("게임 통계");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // 왼쪽 사이드바 생성
        JPanel sidebar = createSidebar();
        add(sidebar, BorderLayout.WEST);

        // 메인 컨텐츠 영역
        JPanel mainPanel = createMainPanel();
        add(mainPanel, BorderLayout.CENTER);

        setLocationRelativeTo(null);
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(200, 0));
        sidebar.setBackground(new Color(240, 240, 240));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 관리자 모드 변경 버튼
        JButton adminModeBtn = createSidebarButton("관리자 모드 변경");
        adminModeBtn.setBackground(Color.WHITE);
        sidebar.add(adminModeBtn);
        sidebar.add(Box.createRigidArea(new Dimension(0, 5)));

        // 메뉴 버튼들
        String[] menuItems = {"매장 관리", "상품관리", "재고관리", "회원관리",
                "인수인계", "매출관리", "직원관리", "게임통계"};
        String[] icons = {"▦", "👥", "📋", "📁", "🔄", "💰", "👤", "🎮"};

        for (int i = 0; i < menuItems.length; i++) {
            JButton btn = createSidebarButton(icons[i] + "  " + menuItems[i]);
            if (menuItems[i].equals("게임통계")) {
                btn.setBackground(new Color(200, 220, 255));
            } else {
                btn.setBackground(Color.WHITE);
            }
            sidebar.add(btn);
            sidebar.add(Box.createRigidArea(new Dimension(0, 5)));
        }

        sidebar.add(Box.createVerticalGlue());

        return sidebar;
    }

    private JButton createSidebarButton(String text) {
        JButton button = new JButton(text);
        button.setMaximumSize(new Dimension(180, 50));
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        return button;
    }

    private JPanel createMainPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Color.WHITE);

        // 헤더 패널
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // 컨텐츠 패널 (통계 + 게임 테이블)
        JPanel contentPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        contentPanel.setBackground(Color.WHITE);

        // 왼쪽: 인기게임 순위
        JPanel leftPanel = createStatisticsPanel();
        contentPanel.add(leftPanel);

        // 오른쪽: 게임 이용 테이블
        JPanel rightPanel = createGameTablePanel();
        contentPanel.add(rightPanel);

        mainPanel.add(contentPanel, BorderLayout.CENTER);

        return mainPanel;
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("게임 통계");
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 32));
        headerPanel.add(titleLabel, BorderLayout.WEST);

        return headerPanel;
    }

    private JPanel createStatisticsPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        // 헤더
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);

        JComboBox<String> comboBox = new JComboBox<>(new String[]{"인기게임 순위"});
        comboBox.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        comboBox.setPreferredSize(new Dimension(200, 35));
        headerPanel.add(comboBox, BorderLayout.WEST);

        JLabel dateLabel = new JLabel("기준 : 2025.11.07");
        dateLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
        dateLabel.setForeground(Color.GRAY);
        headerPanel.add(dateLabel, BorderLayout.SOUTH);

        panel.add(headerPanel, BorderLayout.NORTH);

        // 통계 테이블
        String[] columns = {"게임 이름", "점유율"};
        statisticsModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // 데이터 추가
        Object[][] data = {
                {"1. League of Legend", "35%"},
                {"2. FC 온라인", "10.96%"},
                {"3. Valorant", "9.67%"},
                {"4. 배틀그라운드", "9.21%"},
                {"5. 오버워치", "4.15%"},
                {"6. 서든어택", "4.11%"},
                {"7. 로블록스", "4.05%"},
                {"8. 던전앤파이터", "2.59%"},
                {"9. 메이플스토리", "2.29%"},
                {"10. 스타크래프트", "1.59%"}
        };

        for (Object[] row : data) {
            statisticsModel.addRow(row);
        }

        statisticsTable = new JTable(statisticsModel);
        statisticsTable.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        statisticsTable.setRowHeight(35);
        statisticsTable.setShowGrid(true);
        statisticsTable.setGridColor(new Color(230, 230, 230));

        // 셀 렌더러
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setHorizontalAlignment(SwingConstants.LEFT);
        statisticsTable.getColumnModel().getColumn(0).setCellRenderer(renderer);

        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        statisticsTable.getColumnModel().getColumn(1).setCellRenderer(rightRenderer);

        JScrollPane scrollPane = new JScrollPane(statisticsTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createGameTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(Color.WHITE);

        // 헤더
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);

        JComboBox<String> dateComboBox = new JComboBox<>(new String[]{"일간"});
        dateComboBox.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        dateComboBox.setPreferredSize(new Dimension(150, 30));
        headerPanel.add(dateComboBox, BorderLayout.WEST);

        JLabel dateLabel = new JLabel("25/11/07");
        dateLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        dateLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        headerPanel.add(dateLabel, BorderLayout.EAST);

        panel.add(headerPanel, BorderLayout.NORTH);

        // 게임 테이블
        String[] columns = {"순위", "게임 이름", "총 사용 시간", "평균값"};
        gameModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // 데이터 추가
        Object[][] data = {
                {1, "League of Legend", "108:31", "-"},
                {2, "리니지 리마스터", "72:31", "-"},
                {3, "FC 온라인", "35:23", "-"},
                {4, "배스 오브 엑자일2", "20:47", "-"},
                {5, "발로란트", "20:45", "-"},
                {6, "로스트아크", "14:33", "-"},
                {7, "마비노기 모바일", "14:24", "-"},
                {8, "한게임 포커", "14:15", "-"},
                {9, "MapleStory Wolds", "13:59", "-"},
                {10, "Roblox", "12:22", "-"},
                {11, "검은사막", "10:08", "-"},
                {12, "스타크래프트", "07:54", "-"},
                {13, "메이플스토리", "07:34", "-"},
                {14, "월드 오브 워크래프트 클래식", "07:03", "-"},
                {15, "오로바독", "05:56", "-"},
                {16, "DIABLO II", "04:43", "-"},
                {17, "넷마블 바둑", "04:29", "-"},
                {18, "한게임 맞고", "04:11", "-"},
                {19, "기성", "03:49", "-"},
                {20, "스페셜포스", "03:40", "-"}
        };

        for (Object[] row : data) {
            gameModel.addRow(row);
        }

        gameTable = new JTable(gameModel);
        gameTable.setFont(new Font("맑은 고딕", Font.PLAIN, 13));
        gameTable.setRowHeight(30);
        gameTable.setShowGrid(true);
        gameTable.setGridColor(new Color(230, 230, 230));

        // 헤더 스타일
        gameTable.getTableHeader().setBackground(new Color(220, 235, 255));
        gameTable.getTableHeader().setFont(new Font("맑은 고딕", Font.BOLD, 13));
        gameTable.getTableHeader().setPreferredSize(new Dimension(0, 35));

        // 컬럼 너비 설정
        gameTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        gameTable.getColumnModel().getColumn(1).setPreferredWidth(250);
        gameTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        gameTable.getColumnModel().getColumn(3).setPreferredWidth(80);

        // 셀 렌더러
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        gameTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        gameTable.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        gameTable.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);

        JScrollPane scrollPane = new JScrollPane(gameTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GameStatisticsUI frame = new GameStatisticsUI();
            frame.setVisible(true);
        });
    }
}
