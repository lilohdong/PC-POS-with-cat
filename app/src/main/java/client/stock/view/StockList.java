package client.stock.view;

import dao.StockDAO;
import dto.IngredientDTO;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class StockList extends JPanel {
    private JTable stockTable;
    private DefaultTableModel stockModel;
    private StockDAO dao;
    private StockBottom bottom;

    public StockList(StockBottom bottom) {
        this.bottom = bottom;
        this.dao = new StockDAO();

        this.setLayout(new BorderLayout());
        String[] cols = new String[]{"코드", "이름", "카테고리", "단가", "현재 재고", "최소 재고", "위치"};
        this.stockModel = new DefaultTableModel(cols, 0);
        this.stockTable = new JTable(this.stockModel);

        // 초기 로드
        refreshTable();

        JScrollPane scrollBar = new JScrollPane(this.stockTable);
        JPanel btnPanel = new JPanel(new FlowLayout(2));
        JButton btnIn = new JButton("입고");
        JButton btnOut = new JButton("출고");
        JButton btnAdd = new JButton("입고등록");
        btnIn.setBackground(Color.BLUE);
        btnIn.setForeground(Color.WHITE);
        btnOut.setBackground(Color.RED);
        btnOut.setForeground(Color.WHITE);
        btnAdd.addActionListener((e) -> new AddStockFrame(this));
        btnIn.addActionListener((e) -> this.handleStockIn());
        btnOut.addActionListener((e) -> this.handleStockOut());
        btnPanel.add(btnIn);
        btnPanel.add(btnOut);
        btnPanel.add(btnAdd);
        this.add(scrollBar, "Center");
        this.add(btnPanel, "East");
    }

    // 외부(StockSearch)에서 호출
    public void performSearch(String name, String code, String category) {
        // empty filterType = ALL
        List<IngredientDTO> results = dao.getStocks(name, code, category, "ALL");
        updateTable(results);
    }

    public void performFilter(String filterType) {
        // filterType은 "NORMAL","LACK","SOLDOUT"
        List<IngredientDTO> results = dao.getStocks(null, null, null, filterType);
        updateTable(results);
    }

    private void updateTable(List<IngredientDTO> list) {
        stockModel.setRowCount(0);
        for (IngredientDTO dto : list) {
            stockModel.addRow(new Object[]{dto.getId(), dto.getName(), dto.getCategory(), dto.getUnitPrice(), dto.getTotalQuantity(), dto.getMinQuantity(), dto.getLocation()});
        }
    }

    public void refreshTable() {
        updateTable(dao.getAllStock());
        // 그리고 Bottom 도 새로고침 (모든 입출고 기록 로드)
        bottom.refreshAllRecords();
    }

    private void handleStockIn() {
        int row = this.stockTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "입고할 항목을 선택하세요.");
        } else {
            String id = this.stockModel.getValueAt(row, 0).toString();
            String name = this.stockModel.getValueAt(row, 1).toString();
            String inputQty = JOptionPane.showInputDialog(name + " 입고 수량 입력:");
            if (inputQty != null && !inputQty.isEmpty()) {
                int qty = Integer.parseInt(inputQty);
                boolean result = dao.addStock(id, qty);
                if (result) {
                    JOptionPane.showMessageDialog(this, "입고 완료!");
                    // Bottom에 입고 기록 추가 (연두색)
                    bottom.addRecord("IN", id, name, qty);
                    this.refreshTable();
                } else {
                    JOptionPane.showMessageDialog(this, "입고 실패!");
                }

            }
        }
    }

    private void handleStockOut() {
        int row = this.stockTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "출고할 항목을 선택하세요.");
        } else {
            String id = this.stockModel.getValueAt(row, 0).toString();
            String name = this.stockModel.getValueAt(row, 1).toString();
            int current = Integer.parseInt(this.stockModel.getValueAt(row, 4).toString());
            String inputQty = JOptionPane.showInputDialog(name + " 출고 수량 입력:");
            if (inputQty != null && !inputQty.isEmpty()) {
                int qty = Integer.parseInt(inputQty);
                if (qty > current) {
                    JOptionPane.showMessageDialog(this, "출고량이 현재 재고보다 많습니다.");
                } else {
                    boolean result = dao.subtractStock(id, qty);
                    if (result) {
                        JOptionPane.showMessageDialog(this, "출고 완료!");
                        // Bottom에 출고 기록 추가 (주황색)
                        bottom.addRecord("OUT", id, name, qty);
                        this.refreshTable();

                        // 만약 출고 후 부족 상태가 되면 Bottom에 부족 알림 추가
                        // (bottom.addAlert handled by refreshAllRecords based on ingredient state)
                    } else {
                        JOptionPane.showMessageDialog(this, "출고 실패!");
                    }
                }
            }
        }
    }
}