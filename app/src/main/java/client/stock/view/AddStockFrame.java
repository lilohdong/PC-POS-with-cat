package client.stock.view;

import javax.swing.*;
import java.awt.*;

import dao.StockDAO;
import dao.StockInDAO;
import dto.StockInfoDTO;
import dto.StockInDTO;

public class AddStockFrame extends JFrame {

    public AddStockFrame() {
        setTitle("입고 등록");
        setSize(700, 350);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTextField tfStockInfoId = new JTextField();  // stock_info_id
        JTextField tfIngredientId = new JTextField(); // i_id
        JTextField tfAmount = new JTextField();       // in_quantity
        JTextField tfUnitName = new JTextField();     // 단위

        panel.add(new JLabel("Stock Info ID:"));
        panel.add(tfStockInfoId);

        panel.add(new JLabel("Ingredient ID(식자재 코드):"));
        panel.add(tfIngredientId);

        panel.add(new JLabel("단위명(unit_name):"));
        panel.add(tfUnitName);

        panel.add(new JLabel("입고 수량:"));
        panel.add(tfAmount);

        JButton btnRegister = new JButton("등록");

        btnRegister.addActionListener(e -> {
            try {
                String stockInfoId = tfStockInfoId.getText().trim();
                String ingredientId = tfIngredientId.getText().trim();
                String unitName = tfUnitName.getText().trim();
                int amount = Integer.parseInt(tfAmount.getText().trim());

                // 1) stock_info 등록
                StockInfoDTO stockDTO = new StockInfoDTO();
                stockDTO.setStockInfoId(stockInfoId);
                stockDTO.setIngredientId(ingredientId);
                stockDTO.setUnitName(unitName);

                // stock_info insert
                StockDAO stockDAO = new StockDAO();
                stockDAO.insertStockInfo(stockDTO);

                // 2) stock_in 입고 기록 등록
                StockInDTO inDTO = new StockInDTO(stockInfoId, amount);

                StockInDAO inDAO = new StockInDAO();
                inDAO.insertStockIn(inDTO);

                JOptionPane.showMessageDialog(this, "입고 등록 완료!");
                dispose();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "오류 발생: " + ex.getMessage());
            }
        });

        JPanel bottom = new JPanel();
        bottom.add(btnRegister);

        add(panel, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);

        setVisible(true);
    }
}