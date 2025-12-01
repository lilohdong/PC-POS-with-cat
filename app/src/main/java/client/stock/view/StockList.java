package client.stock.view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

import dao.StockDAO;
import dto.IngredientDTO;

public class StockList extends JPanel{
    private JTable stockTable;
    private DefaultTableModel stockModel;

    public StockList(){
        setLayout(new BorderLayout());

        String[] cols = {"코드", "이름", "카테고리", "단가", "현재 재고", "최소 재고", "위치"};

        stockModel = new DefaultTableModel(cols, 0);
        stockTable = new JTable(stockModel);

        /*
        예시 데이터 제거
        DAO로부터 DB데이터 가져와 테이블에 addRow
        */
        StockDAO dao = new StockDAO();
        List<IngredientDTO> stockList = dao.getAllStock();

        for (IngredientDTO dto : stockList) {
            stockModel.addRow(new Object[]{
                    dto.getId(),
                    dto.getName(),
                    dto.getCategory(),
                    dto.getUnitPrice(),
                    dto.getTotalQuantity(),
                    dto.getMinQuantity(),
                    dto.getLocation()
            });
        }

        
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
        
        //입출고 버튼 - 재고 증가,감소
        btnIn.addActionListener(e -> handleStockIn());
        btnOut.addActionListener(e -> handleStockOut());

        btnPanel.add(btnIn);
        btnPanel.add(btnOut);
        btnPanel.add(btnAdd);

        add(scrollBar, BorderLayout.CENTER);
        add(btnPanel, BorderLayout.EAST);
    }

    //메서드: handleStockIn(), handleStockOut()
    private void handleStockIn() {
            int row = stockTable.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "입고할 항목을 선택하세요.");
                return;
            }

            String id = stockModel.getValueAt(row, 0).toString();
            String name = stockModel.getValueAt(row, 1).toString();

            String inputQty = JOptionPane.showInputDialog(name + " 입고 수량 입력:");
            if (inputQty == null || inputQty.isEmpty()) return;

            int qty = Integer.parseInt(inputQty);

            StockDAO dao = new StockDAO();
            boolean result = dao.addStock(id, qty);

            if (result) {
                JOptionPane.showMessageDialog(this, "입고 완료!");
                refreshTable();
            } else {
                JOptionPane.showMessageDialog(this, "입고 실패!");
            }
        }
    private void handleStockOut() {
        int row = stockTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "출고할 항목을 선택하세요.");
            return;
        }

        String id = stockModel.getValueAt(row, 0).toString();
        String name = stockModel.getValueAt(row, 1).toString();
        int current = Integer.parseInt(stockModel.getValueAt(row, 4).toString());

        String inputQty = JOptionPane.showInputDialog(name + " 출고 수량 입력:");
        if (inputQty == null || inputQty.isEmpty()) return;

        int qty = Integer.parseInt(inputQty);

        if (qty > current) {
            JOptionPane.showMessageDialog(this, "출고량이 현재 재고보다 많습니다.");
            return;
        }

        StockDAO dao = new StockDAO();
        boolean result = dao.subtractStock(id, qty);

        if (result) {
            JOptionPane.showMessageDialog(this, "출고 완료!");
            refreshTable();
        } else {
            JOptionPane.showMessageDialog(this, "출고 실패!");
        }
    }

    private void refreshTable() {
        stockModel.setRowCount(0); // 전체 초기화

        StockDAO dao = new StockDAO();
        List<IngredientDTO> list = dao.getAllStock();

        for (IngredientDTO dto : list) {
            stockModel.addRow(new Object[]{
                    dto.getId(),
                    dto.getName(),
                    dto.getCategory(),
                    dto.getUnitPrice(),
                    dto.getTotalQuantity(),
                    dto.getMinQuantity(),
                    dto.getLocation()
            });
        }
    }
}
