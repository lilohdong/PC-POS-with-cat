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

        // 수정 2: '단가' 컬럼 제거
        String[] cols = new String[]{"코드", "이름", "카테고리", "현재 재고", "최소 재고", "위치"};
        this.stockModel = new DefaultTableModel(cols, 0);
        this.stockTable = new JTable(this.stockModel);

        refreshTable();

        JScrollPane scrollBar = new JScrollPane(this.stockTable);
        JPanel btnPanel = new JPanel(new FlowLayout(2));
        JButton btnIn = new JButton("입고");
        JButton btnOut = new JButton("출고");
        // 수정 2: '입고등록' 버튼 제거

        btnIn.setBackground(Color.BLUE);
        btnIn.setForeground(Color.WHITE);
        btnOut.setBackground(Color.RED);
        btnOut.setForeground(Color.WHITE);

        btnIn.addActionListener((e) -> this.handleStockIn());
        btnOut.addActionListener((e) -> this.handleStockOut());

        btnPanel.add(btnIn);
        btnPanel.add(btnOut);
        // btnPanel.add(btnAdd); // 제거됨

        this.add(scrollBar, "Center");
        this.add(btnPanel, "East");
    }

    public void performSearch(String name, String code, String category) {
        List<IngredientDTO> results = dao.getStocks(name, code, category, "ALL");
        updateTable(results);
    }

    public void performFilter(String filterType) {
        List<IngredientDTO> results = dao.getStocks(null, null, null, filterType);
        updateTable(results);
    }

    private void updateTable(List<IngredientDTO> list) {
        stockModel.setRowCount(0);
        for (IngredientDTO dto : list) {
            // 수정 2: 단가(dto.getUnitPrice()) 제외하고 Row 추가
            stockModel.addRow(new Object[]{
                    dto.getId(),
                    dto.getName(),
                    dto.getCategory(),
                    // dto.getUnitPrice(), // 제거됨
                    dto.getTotalQuantity(),
                    dto.getMinQuantity(),
                    dto.getLocation()
            });
        }
    }

    public void refreshTable() {
        updateTable(dao.getAllStock());
        bottom.refreshAllRecords();
    }

    private void handleStockIn() {
        int row = this.stockTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "입고할 항목을 선택하세요.");
            return;
        }

        String id = this.stockModel.getValueAt(row, 0).toString();
        String name = this.stockModel.getValueAt(row, 1).toString();
        String input = JOptionPane.showInputDialog(name + " 입고 수량 입력(재고 단위):");
        if (input != null && !input.trim().isEmpty()) {
            try {
                int inputQty = Integer.parseInt(input.trim());
                if (inputQty <= 0) throw new Exception();

                boolean result = dao.addStock(id, inputQty);

                if (result) {
                    // 이제 트리거가 처리하므로 실제 추가 갯수는 재조회해야 정확하지만,
                    // 사용자 피드백 용도로만 계산해서 보여줌
                    int unitQty = dao.getUnitQuantityByIngredientId(id);
                    int actualAdded = inputQty * unitQty;

                    JOptionPane.showMessageDialog(this,
                            name + " 입고 완료!\n입력: " + inputQty + "박스 → 실제 추가: " + actualAdded + "개");

                    bottom.addRecord("IN", id, name, inputQty);
                    refreshTable();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "올바른 숫자를 입력하세요.");
            }
        }
    }

    private void handleStockOut() {
        int row = this.stockTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "출고할 항목을 선택하세요.");
            return;
        }

        String id = this.stockModel.getValueAt(row, 0).toString();
        String name = this.stockModel.getValueAt(row, 1).toString();
        // 컬럼 인덱스가 바뀌었으므로(단가 삭제됨) 현재재고는 index 3
        int current = Integer.parseInt(this.stockModel.getValueAt(row, 3).toString());

        String input = JOptionPane.showInputDialog(name + " 출고 수량 입력(재고 단위):");

        if (input != null && !input.trim().isEmpty()) {
            try {
                int inputQty = Integer.parseInt(input.trim());
                if (inputQty <= 0) throw new Exception();

                int unitQty = dao.getUnitQuantityByIngredientId(id);
                int delta = inputQty * unitQty;

                if (delta > current) {
                    JOptionPane.showMessageDialog(this, "출고량이 현재 재고보다 많습니다.\n현재 재고: " + current + "개");
                    return;
                }

                boolean result = dao.subtractStock(id, delta);

                if (result) {
                    JOptionPane.showMessageDialog(this,
                            name + " 출고 완료!\n입력: " + inputQty + "박스 → 실제 차감: " + delta + "개");
                    bottom.addRecord("OUT", id, name, inputQty);
                    refreshTable();
                } else {
                    JOptionPane.showMessageDialog(this, "출고 실패!");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "올바른 숫자를 입력하세요.");
            }
        }
    }
}