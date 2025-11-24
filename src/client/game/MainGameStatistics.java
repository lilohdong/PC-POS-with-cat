package client.game;

import util.GameStatisticsService;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.time.LocalTime;
import java.util.Vector;

public class MainGameStatistics extends JPanel {
    private final String[] columnNames = {"순위", "게임 이름", "총 사용 시간", "현재 이용자 수"};
    private DefaultTableModel tm;

    private Vector<Vector<String>> datas;
    private final String[] years = {
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

        JLabel dateLabel = new JLabel("현재 시간 : " + LocalTime.now());
        dateLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 14));

        headerPanel.add(dateLabel);
        add(headerPanel, BorderLayout.NORTH);

        // 테이블 생성
        tm = new DefaultTableModel(columnNames, 0);
        initData();

        JTable table = new JTable(tm);
        table.setColumnSelectionAllowed(false);
        table.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
        table.setRowHeight(40);
        table.setGridColor(Color.LIGHT_GRAY);
        table.setShowGrid(true);

        // 테이블 헤더 스타일
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("맑은 고딕", Font.BOLD, 12));
        header.setBackground(Color.gray);
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

    private void initData() {
        GameStatisticsService gss = new GameStatisticsService();
        datas = gss.loadData();
        for (Vector<String> data : datas) {
            tm.addRow(data);
        }
    }

    public void refreshPanel() {
        removeAll();
        initUI();
        revalidate();
        repaint();
    }
}
