package client.stock.view;

import javax.swing.*;
import java.awt.*;

import dao.StockDAO;
import dao.StockInDAO;
import dto.StockInfoDTO;
import dto.StockInDTO;

public class AddStockFrame extends JFrame {

    private StockList parentStockList;

    public AddStockFrame(StockList parentStockList) {
        this.parentStockList = parentStockList;

        setTitle("입고 등록");
        setSize(700, 350);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(6, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTextField tfStockInfoId = new JTextField();  // stock_info_id
        JTextField tfIngredientId = new JTextField(); // i_id
        JTextField tfUnitName = new JTextField();     // 단위
        JTextField tfUnitQuantity = new JTextField(); // 단위당 수량
        JTextField tfAmount = new JTextField();       // in_quantity

        panel.add(new JLabel("Stock Info ID:"));
        panel.add(tfStockInfoId);

        panel.add(new JLabel("식자재 ID:"));
        panel.add(tfIngredientId);

        panel.add(new JLabel("단위명:"));
        panel.add(tfUnitName);

        panel.add(new JLabel("단위단 수량:"));
        panel.add(tfUnitQuantity);

        panel.add(new JLabel("입고 수량:"));
        panel.add(tfAmount);

        JButton btnRegister = new JButton("등록");

        btnRegister.addActionListener(e -> {
            try {
                String stockInfoId = tfStockInfoId.getText().trim();
                String ingredientId = tfIngredientId.getText().trim();
                String unitName = tfUnitName.getText().trim();
                String unitQtyText = tfUnitQuantity.getText().trim();
                String amountText = tfAmount.getText().trim();

                if (ingredientId.isEmpty() || unitName.isEmpty() || unitQtyText.isEmpty() || amountText.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "필수 항목을 모두 입력해주세요.", "경고", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                int unitQtyValue = Integer.parseInt(unitQtyText);
                int amount = Integer.parseInt(amountText);

                StockDAO stockDAO = new StockDAO();

                // 1) stock_info 등록
                StockInfoDTO stockDTO = new StockInfoDTO();
                stockDTO.setIngredientId(ingredientId);
                stockDTO.setUnitName(unitName);
                stockDTO.setUnitQuantity(unitQtyValue);

                //stockInfold가 비어 있으면 DAO 내부에서 새로운 ID 생성
                String finalStockInfoId;
                if (stockInfoId.isEmpty()) {
                    finalStockInfoId = stockDAO.insertStockInfo(stockDTO);
                }else {
                    stockDTO.setStockInfoId(stockInfoId);
                    finalStockInfoId = stockDAO.insertStockInfo(stockDTO);
                }
                if (finalStockInfoId == null) {
                    throw new Exception("재고 단위 정보 등록에 실패했습니다.");
                }

                // 2) stock_in 입고 기록 등록
                StockInDTO inDTO = new StockInDTO(finalStockInfoId, amount);

                StockInDAO inDAO = new StockInDAO();
                //int result = inDAO.insertStockIn(inDTO);

                boolean success = stockDAO.insertStockIn(inDTO);

                if (success){
                    JOptionPane.showMessageDialog(this, "입고 등록 및 재고 갱신 완료!");
                    parentStockList.refreshTable();

                    dispose();
                }else {
                    JOptionPane.showMessageDialog(this, "재고 기록 등록 중 오류 발생!", "오류", JOptionPane.ERROR_MESSAGE);
                }

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "수량은 숫자로 입력해야 합니다.", "입력 오류", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "오류 발생: " + ex.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });

        JPanel bottom = new JPanel();
        bottom.add(btnRegister);

        add(panel, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);

        setVisible(true);
    }
}