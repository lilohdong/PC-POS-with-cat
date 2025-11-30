package client.stock.view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class StockBottom extends JPanel{
    private JTable alarmTable;
    private DefaultTableModel alarmModel;

    public StockBottom() {

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        JPanel table = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnAlarm = new JButton("알림");
        JButton btnOutHistory = new JButton("출고내역");
        JButton btnLack = new JButton("부족");

        String[] cols = {"코드", "이름", "카테고리", "현재 재고", "최소 재고", "위치"};
        alarmModel = new DefaultTableModel(cols, 0);
        alarmTable = new JTable(alarmModel);

        //예시 데이터(DB 연동시 삭제)
        alarmModel.addRow(new Object[]{"1004", "치즈(팩)", "토핑", 1, 3, "냉장고"});
        JScrollPane scrollbar = new JScrollPane(alarmTable);



        table.add(btnAlarm);
        table.add(btnOutHistory);
        table.add(btnLack);

        add(table, BorderLayout.NORTH);
        add(scrollbar, BorderLayout.CENTER);
    }
}
