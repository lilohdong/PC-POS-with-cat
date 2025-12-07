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

/*
재고 목록을 테이블 형태로 표시하는 패널

기능:
전체 재고 또는 검색/필터 결과 표시
선택한 항목 입고/출고 처리
입출고 기록을 StockBottom에 실시간 전달
*/
public class StockList extends JPanel {
    private JTable stockTable;
    private DefaultTableModel stockModel;
    private StockDAO dao;
    private StockBottom bottom;     // 입출고 기록을 추가하기 위한 참조

    public StockList(StockBottom bottom) {
        this.bottom = bottom;
        this.dao = new StockDAO();

        this.setLayout(new BorderLayout());

        //테이블 컬럼
        String[] cols = new String[]{"코드", "이름", "카테고리", "현재 재고", "최소 재고", "위치"};
        this.stockModel = new DefaultTableModel(cols, 0);
        this.stockTable = new JTable(this.stockModel);

        refreshTable(); // 초기 데이터 로드

        // 입고/출고 버튼 패널
        JScrollPane scrollBar = new JScrollPane(this.stockTable);
        JPanel btnPanel = new JPanel(new FlowLayout(2));
        JButton btnIn = new JButton("입고");
        JButton btnOut = new JButton("출고");

        btnIn.setBackground(Color.BLUE);
        btnIn.setForeground(Color.WHITE);
        btnOut.setBackground(Color.RED);
        btnOut.setForeground(Color.WHITE);

        btnIn.addActionListener((e) -> this.handleStockIn());
        btnOut.addActionListener((e) -> this.handleStockOut());

        btnPanel.add(btnIn);
        btnPanel.add(btnOut);

        this.add(scrollBar, "Center");
        this.add(btnPanel, "East");
    }

    //검색 조건에 맞는 재고 목록 표시
    public void performSearch(String name, String code, String category) {
        List<IngredientDTO> results = dao.getStocks(name, code, category, "ALL");
        updateTable(results);
    }

    //상태별 필터링 (NORMAL / LACK / SOLDOUT)
    public void performFilter(String filterType) {
        List<IngredientDTO> results = dao.getStocks(null, null, null, filterType);
        updateTable(results);
    }

    //테이블 데이터 갱신 공통 메서드
    private void updateTable(List<IngredientDTO> list) {
        stockModel.setRowCount(0);
        for (IngredientDTO dto : list) {
            stockModel.addRow(new Object[]{
                    dto.getId(),
                    dto.getName(),
                    dto.getCategory(),
                    // dto.getUnitPrice(), // 관련없음: 제거됨
                    dto.getTotalQuantity(),
                    dto.getMinQuantity(),
                    dto.getLocation()
            });
        }
    }

    //전체 재고 다시 불러오기 (초기화용)
    public void refreshTable() {
        updateTable(dao.getAllStock());
        bottom.refreshAllRecords();
    }

    //입고 처리: 선택한 재료에 박스 단위 입고 -> 개수로 변환되어 DB 반영
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

    //출고 처리: 박스, 팩 등등의 재고 단위 입력 -> 개수로 변환 후 재고 차감
    private void handleStockOut() {
        int row = this.stockTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "출고할 항목을 선택하세요.");
            return;
        }

        String id = this.stockModel.getValueAt(row, 0).toString();
        String name = this.stockModel.getValueAt(row, 1).toString();
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