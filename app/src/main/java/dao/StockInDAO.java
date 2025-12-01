package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;

import dto.StockInDTO;

import util.DBUtil;

public class StockInDAO {
    public int insertStockIn(StockInDTO dto) {
        String sql = "INSERT INTO stock_in (in_id, i_id, stock_info_id, in_quantity, unit_price) "
                   + "VALUES (?, ?, ?, ?, 0)";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, generateInId());  // 신규 입고번호 생성
            pstmt.setString(2, getIngredientId(dto.getStockCode()));  // FK 처리
            pstmt.setString(3, dto.getStockCode());
            pstmt.setInt(4, dto.getAmount());

            return pstmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private String generateInId() {
        // 실제로는 SELECT MAX(in_id) + 1 형태로 구현
        return String.valueOf(System.currentTimeMillis()).substring(8);
    }

    private String getIngredientId(String stockInfoId) {
        // stock_info → ingredient FK 조회 (SELECT i_id FROM stock_info WHERE stock_info_id = ?)
        return stockInfoId.substring(0, 3); // 예시: 코드 앞 3자리 → 실제 DB 조회로 변경해야 함
    }
}
