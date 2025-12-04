package client.stock.view;

import dao.StockDAO;
import dto.IngredientDTO;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.Vector;

import db.DBConnection;

public class StockBottom extends JPanel {
    private JTable alarmTable;
    private DefaultTableModel alarmModel;
    private JButton btnAlarm;
    private JButton btnOutHistory;
    private JButton btnInHistory;
    private JButton btnLack;

    public StockBottom() {
        this.setLayout(new BorderLayout());
        this.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        JPanel topPanel = new JPanel(new FlowLayout(0));
        btnAlarm = new JButton("알림");
        btnInHistory = new JButton("입고내역");
        btnOutHistory = new JButton("출고내역");
        btnLack = new JButton("부족");

        topPanel.add(btnAlarm);
        topPanel.add(btnInHistory);
        topPanel.add(btnOutHistory);
        topPanel.add(btnLack);

        // 테이블 컬럼에 TYPE 추가 (IN, OUT, ALARM)
        String[] cols = new String[]{"TYPE", "코드", "이름", "수량", "시간"};
        this.alarmModel = new DefaultTableModel(cols, 0) {
            // 생성 이후 TYPE 컬럼이 보이지 않게 할 것
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        this.alarmTable = new JTable(this.alarmModel);

        // TYPE 컬럼은 숨김 (폭 0)
        this.alarmTable.getColumnModel().getColumn(0).setMinWidth(0);
        this.alarmTable.getColumnModel().getColumn(0).setMaxWidth(0);
        this.alarmTable.getColumnModel().getColumn(0).setWidth(0);

        // 커스텀 렌더러로 행 배경색 설정
        this.alarmTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table,Object value,boolean isSelected,boolean hasFocus,int row,int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                Object type = table.getModel().getValueAt(row, 0); // TYPE
                if (!isSelected) {
                    if ("IN".equals(type)) {
                        c.setBackground(new Color(200, 255, 200)); // 연두
                    } else if ("OUT".equals(type)) {
                        c.setBackground(new Color(255, 230, 180)); // 주황
                    } else if ("ALARM".equals(type)) {
                        c.setBackground(new Color(255, 200, 200)); // 빨강
                    } else {
                        c.setBackground(Color.WHITE);
                    }
                }
                return c;
            }
        });

        JScrollPane scrollbar = new JScrollPane(this.alarmTable);
        this.add(topPanel, "North");
        this.add(scrollbar, "Center");

        // 버튼 리스너
        btnAlarm.addActionListener(e -> refreshAllRecords());
        btnInHistory.addActionListener(e -> refreshInRecords());
        btnOutHistory.addActionListener(e -> refreshOutRecords());
        btnLack.addActionListener(e -> refreshAlarmRecords());

        // 초기 로드
        refreshAllRecords();
    }

    // Bottom에 새로운 기록 추가 (UI에서 호출)
    // type: "IN" or "OUT"
    public void addRecord(String type, String code, String name, int qty) {
        // 시간은 현재 시간 문자열로 표기
        String time = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());
        this.alarmModel.addRow(new Object[]{type, code, name, qty, time});
    }

    // DB에서 입출고/부족 기록을 로드하는 함수들
    public void refreshAllRecords() {
        // 전체: 입고(in) + 출고(out) + 부족 알람
        alarmModel.setRowCount(0);
        loadHistoryRecords(null);
        loadAlarmRecords();
    }

    public void refreshInRecords() {
        alarmModel.setRowCount(0);
        loadHistoryRecords("IN");
    }

    public void refreshOutRecords() {
        alarmModel.setRowCount(0);
        loadHistoryRecords("OUT");
    }

    public void refreshAlarmRecords() {
        alarmModel.setRowCount(0);
        loadAlarmRecords();
    }

    private void loadHistoryRecords(String filter) {

        String baseSql = "SELECT * FROM (" +
                "SELECT in_time AS time, 'IN' AS type, i_id, in_quantity AS quantity FROM stock_in " +
                "UNION ALL " +
                "SELECT out_time AS time, 'OUT' AS type, i_id, out_quantity AS quantity FROM stock_out" +
                ") AS history_records ";

        StringBuilder sqlBuilder = new StringBuilder(baseSql);

        if (filter != null) {
            sqlBuilder.append(" WHERE type = ?");
        }

        sqlBuilder.append("ORDER BY time DESC LIMIT 100");

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sqlBuilder.toString())){

            if (filter != null){
                ps.setString(1, filter);
            }

            try(ResultSet rs = ps.executeQuery()){
                while (rs.next()){
                    String type = rs.getString("type");
                    String code = rs.getString("i_id");
                    int qty = rs.getInt("quantity");
                    Timestamp t = rs.getTimestamp("time");
                    String time = t == null ? "": t.toString();
                    String name = getIngredientName(code);
                    alarmModel.addRow(new Object[]{type, code, qty, name, time});
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /* 사용 중단 - 쿼리를 나누지 않고 하나로 명확하게 구성
    private static String getString(String filter) {
        String unionSql = "SELECT * FROM (" +
                            "SELECT in_time AS time, 'IN' AS type, i_id, in_quantity AS quantity FROM stock_in " +
                            "UNION ALL " +
                            "SELECT out_time AS time, 'OUT' AS type, i_id, out_quantity AS quantity FROM stock_out" +
                            ") AS history_records ";

        if (filter == null){
            unionSql += "WHERE type = ? ";
        }
        unionSql += "ORDER BY time DESC LIMIT 100";
        return unionSql;
    } */

    private void loadAlarmRecords() {
        String sql = "SELECT i_id, i_name, total_quantity, min_quantity FROM ingredient WHERE total_quantity < min_quantity ORDER BY total_quantity ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String code = rs.getString("i_id");
                String name = rs.getString("i_name");
                int qty = rs.getInt("total_quantity");
                alarmModel.addRow(new Object[]{"ALARM", code, name, qty, ""});
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getIngredientName(String code) {
        String sql = "SELECT i_name FROM ingredient WHERE i_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, code);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getString("i_name");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}