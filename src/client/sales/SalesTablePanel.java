package client.sales;

import util.Sizes;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;

public class SalesTablePanel extends JPanel {
    private JTable mainTable;
    private JComboBox<String> selectD;
    private String[] duration = {
            "일간",
            "주간",
            "연간"
    };
    private final String[] column = {"매출번호", "아이디", "매출발생일", "매출발생시간", "상품", "매출액"};
    private DefaultTableModel tm;

    private JPanel dateAppearance;

    public SalesTablePanel() {
        initUI();
    }

    private void initUI() {
        setPreferredSize(new Dimension(Sizes.PANEL_WIDTH, Sizes.SALES_TABLE_HEIGHT));
        setLayout(new BorderLayout());

        // 메인 테이블 부분
        tm = new DefaultTableModel(column, 0);
        mainTable = new JTable(tm);
        mainTable.setPreferredSize(new Dimension(Sizes.PANEL_WIDTH, Sizes.SALES_TABLE_HEIGHT));
        mainTable.setBackground(Color.white);


        JTableHeader header = mainTable.getTableHeader();
        header.setFont(new Font("맑은 고딕", Font.BOLD, 12));
        header.setBackground(Color.gray);
        header.setPreferredSize(new Dimension(0, 35));

        JScrollPane sp = new JScrollPane(mainTable);
        sp.setBorder(null);
        add(sp);
    }

    private JPanel createMainHeaderPanel() {
        JPanel jp = new JPanel();
        jp.setPreferredSize(new Dimension(Sizes.PANEL_WIDTH, 40));
        jp.setBackground(Color.white);
        jp.setLayout(new FlowLayout(FlowLayout.LEFT));
        // 콤보박스
        selectD = new JComboBox<>(duration);
        selectD.setPreferredSize(new Dimension(248, 40));


        JLabel period = new JLabel("");
        jp.add(selectD);
        return jp;
    }

}
