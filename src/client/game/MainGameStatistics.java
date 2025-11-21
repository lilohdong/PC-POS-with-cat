package client.game;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.time.LocalTime;

import util.*;
public class MainGameStatistics extends JPanel {
    private String[] columnNames = {"순위", "게임 이름", "총 사용 시간", "현재 이용자 수"};
    private Object[][] data = {
            {"1", "League of Legend", "108:31", "90"},
            {"2", "리니지 리마스터", "72:31", "82"},
            {"3", "FC 온라인", "35:23", "82"},
            {"4", "패스 오브 엑자일2", "20:47", "75"},
            {"5", "발로란트", "20:45", "75"},
            {"6", "로스트아크", "14:33", "69"},
            {"7", "마비노기 모바일", "14:24", "66"},
            {"8", "한게임 포커", "14:15", "41"},
            {"9", "MapleStory Wolds", "13:59", "37"},
            {"10", "Roblox", "12:22", "8"},
            {"11", "검은사막", "10:08", "84"},
            {"12", "스타크래프트", "07:54", "79"},
            {"13", "메이플스토리", "07:34", "76"},
            {"14", "월드 오브 워크래프트 클래식", "07:03", "76"},
            {"15", "오로바톡", "05:56", "55"},
            {"16", "DIABLO II", "04:43", "45"},
            {"17", "넷마블 바둑", "04:29", "42"},
            {"18", "한게임 맞고", "04:11", "37"},
            {"19", "거상", "03:49", "28"},
            {"20", "스페셜포스", "03:40", "14"}
    };
    private String[] years = {
            "2025년",
            "2024년",
            "2023년",
            "2022년"
    };
    public MainGameStatistics() {
        initUI();
    }

    private void initUI() {
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createMatteBorder(0, 0, 2, 2, Color.BLACK));
        setLayout(new BorderLayout());

        // 헤더 패널
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setBackground(new Color(220, 230, 240));
        headerPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY));

        JLabel dateLabel = new JLabel("현재 시간 : "+ LocalTime.now());
        dateLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 14));

        headerPanel.add(dateLabel);
        add(headerPanel, BorderLayout.NORTH);

        // 테이블 생성

        JTable table = new JTable(data, columnNames);
        table.setColumnSelectionAllowed(false);
        table.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
        table.setRowHeight(40);
        table.setGridColor(Color.LIGHT_GRAY);
        table.setShowGrid(true);

        // 테이블 헤더 스타일
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("맑은 고딕", Font.BOLD, 12));
        header.setBackground(new Color(240, 240, 240));
        header.setPreferredSize(new Dimension(0, 35));

        // 컬럼 너비 설정
        TableColumnModel columnModel = table.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(50);
        columnModel.getColumn(1).setPreferredWidth(250);
        columnModel.getColumn(2).setPreferredWidth(150);
        columnModel.getColumn(3).setPreferredWidth(150);
        // 중앙 정렬
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        columnModel.getColumn(0).setCellRenderer(centerRenderer);
        columnModel.getColumn(2).setCellRenderer(centerRenderer);
        columnModel.getColumn(3).setCellRenderer(centerRenderer);

        // 스크롤 패널에 테이블 추가
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);
    }
}
