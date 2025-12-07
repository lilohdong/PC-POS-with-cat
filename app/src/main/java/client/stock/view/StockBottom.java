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

/*
재고 관리 화면 하단 영역

기능:
입고/출고 실시간 기록 표시
재고 부족 알림 표시
버튼으로 입고내역, 출고내역, 부족재고 필터링 가능
*/
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

        //상단 버튼 패널
        JPanel topPanel = new JPanel(new FlowLayout(0));
        btnAlarm = new JButton("알림");
        btnInHistory = new JButton("입고내역");
        btnOutHistory = new JButton("출고내역");
        btnLack = new JButton("부족");

        topPanel.add(btnAlarm);
        topPanel.add(btnInHistory);
        topPanel.add(btnOutHistory);
        topPanel.add(btnLack);

        //기록 테이블: TYPE, 코드, 이름, 수량, 시간
        String[] cols = new String[]{"TYPE", "코드", "이름", "수량", "시간"};
        this.alarmModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;   //편집 불가
            }
        };
        this.alarmTable = new JTable(this.alarmModel);

        //TYPE 컬럼 숨기기
        this.alarmTable.getColumnModel().getColumn(0).setMinWidth(0);
        this.alarmTable.getColumnModel().getColumn(0).setMaxWidth(0);
        this.alarmTable.getColumnModel().getColumn(0).setWidth(0);

        //행 배경색을 TYPE에 따라 다르게 표시
        this.alarmTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table,Object value,boolean isSelected,boolean hasFocus,int row,int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                Object type = table.getModel().getValueAt(row, 0);
                if (!isSelected) {
                    if ("IN".equals(type)) {
                        c.setBackground(new Color(200, 255, 200));
                    } else if ("OUT".equals(type)) {
                        c.setBackground(new Color(255, 230, 180));
                    } else if ("ALARM".equals(type)) {
                        c.setBackground(new Color(255, 200, 200));
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

        //버튼 액션 연결
        btnAlarm.addActionListener(e -> refreshAllRecords());
        btnInHistory.addActionListener(e -> refreshInRecords());
        btnOutHistory.addActionListener(e -> refreshOutRecords());
        btnLack.addActionListener(e -> refreshAlarmRecords());

        refreshAllRecords();    //초기 로드
    }

    //StockList에서 입출고 발생 시 호출 -> 실시간 기록 추가
    public void addRecord(String type, String code, String name, int qty) {
        String time = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());
        this.alarmModel.addRow(new Object[]{type, code, name, qty, time});
    }

    public void refreshAllRecords() {
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

    //입고/출고 내역을 DB에서 가져와 테이블에 추가
    private void loadHistoryRecords(String filter) {
        // 기존 쿼리: stock_in + stock_out 전체 UNION
        // 문제: 주문으로 인한 stock_out 기록도 포함됨

        // 수정된 쿼리: 수동 출고만 가져오고, 주문 출고는 제외
        String baseSql = """
        SELECT 'IN' AS type, si.in_time AS time, si.i_id, si.in_quantity AS quantity, i.i_name
        FROM stock_in si
        JOIN ingredient i ON si.i_id = i.i_id
        
        UNION ALL
        
        -- 수동 출고만 포함 (주문 출고는 제외!)
        SELECT 'OUT' AS type, so.out_time AS time, so.i_id, so.out_quantity AS quantity, i.i_name
        FROM stock_out so
        JOIN ingredient i ON so.i_id = i.i_id
        WHERE so.out_id LIKE 'OUT%'  -- 수동 출고는 OUT001 형식
          AND so.out_id NOT LIKE 'ORDER_%'  -- 주문 출고는 ORDER_ 접두사로 구분 (추후 권장)
        """;

        StringBuilder sqlBuilder = new StringBuilder(baseSql);
        if (filter != null) {
            sqlBuilder.append(" WHERE type = ?");
        }
        sqlBuilder.append(" ORDER BY time DESC LIMIT 100");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sqlBuilder.toString())) {

            if (filter != null) {
                ps.setString(1, filter);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String type = rs.getString("type");
                    String code = rs.getString("i_id");
                    String name = rs.getString("i_name");
                    int qty = rs.getInt("quantity");
                    Timestamp t = rs.getTimestamp("time");
                    String time = t != null ? t.toString().substring(0, 19) : "";

                    alarmModel.addRow(new Object[]{type, code, name, qty, time});
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //재고 부족 항목만 가져와 표시
    private void loadAlarmRecords() {
        String sql = "SELECT i_id, i_name, total_quantity, min_quantity FROM ingredient WHERE total_quantity < min_quantity ORDER BY total_quantity ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String code = rs.getString("i_id");
                String name = rs.getString("i_name");
                int qty = rs.getInt("total_quantity");
                // 여기도 순서 맞춤
                alarmModel.addRow(new Object[]{"ALARM", code, name, qty, ""});
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //재료 코드로 이름 조회
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