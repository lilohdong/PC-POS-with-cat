package client.stock.view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.*;

public class StockList extends JPanel{
    private JTable stockTable;
    private DefaultTableModel stockModel;

    public StockList(){
        setLayout(new BorderLayout());

        String[] cols = {"코드", "이름", "카테고리", "단가", "현재 재고", "최소 재고", "위치"};

        stockModel = new DefaultTableModel(cols, 0);
        stockTable = new JTable(stockModel);

        /*
        예시 데이터(DB 연동시 삭제)
        추가하지 않으며 했으나 추가하지 않을 시 코드 오류
        */
        stockModel.addRow(new Object[]{"1001", "신라면(봉지)", "라면", 3500, 6, 3, "진열대"});
        stockModel.addRow(new Object[]{"1002", "콜라(박스)", "음료", 6000, 3, 1, "냉장고"});
        stockModel.addRow(new Object[]{"1004", "치즈(팩)", "토핑", 3000, 1, 3, "냉장고"});

        JScrollPane scrollBar = new JScrollPane(stockTable);

        //버튼패널 -> 입고, 출고, 재고등록 버튼 input
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnIn = new JButton("입고");
        JButton btnOut = new JButton("출고");
        JButton btnAdd = new JButton("입고등록");

        //입출고 버튼 색상 커스텀
        btnIn.setBackground(Color.BLUE);
        btnIn.setForeground(Color.WHITE);
        btnOut.setBackground(Color.RED);
        btnOut.setForeground(Color.WHITE);

        //입고등록 버튼 클릭 시 새 프레임 띄우는 이벤트: 만들면 해당 라인 주석풀기
        btnAdd.addActionListener(e -> new AddStockFrame());

        btnPanel.add(btnIn);
        btnPanel.add(btnOut);
        btnPanel.add(btnAdd);

        add(scrollBar, BorderLayout.CENTER);
        add(btnPanel, BorderLayout.EAST);
    }
}
