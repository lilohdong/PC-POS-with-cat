package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import db.DBConnection;
import dto.IngredientDTO;
import dto.StockInfoDTO;
import dto.StockInDTO;

import util.DBUtil;

public class StockDAO {
    
    private Connection conn;

    public StockDAO() {
        try {
            conn = DBConnection.getConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 1. 전체 재고 조회
    public List<IngredientDTO> getAllStock() {
        List<IngredientDTO> list = new ArrayList<>();

        String sql =
                "SELECT i.i_id, i.i_name, c.c_name, " +
                "       (SELECT unit_price FROM stock_in WHERE i_id = i.i_id ORDER BY in_time DESC LIMIT 1) AS unit_price, " +
                "       i.total_quantity, i.min_quantity, i.store_location " +
                "FROM ingredient i " +
                "LEFT JOIN ingredient_category c ON i.c_id = c.c_id";

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                IngredientDTO dto = new IngredientDTO(
                        rs.getString("i_id"),
                        rs.getString("i_name"),
                        rs.getString("c_name"),
                        rs.getInt("unit_price"),
                        rs.getInt("total_quantity"),
                        rs.getInt("min_quantity"),
                        rs.getString("store_location")
                );

                list.add(dto);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // 2. 입고 처리
    public boolean stockIn(String ingredientId, String stockInfoId, int inQuantity, int unitPrice) {
        String sqlIn =
                "INSERT INTO stock_in(stock_in_id, i_id, stock_info_id, in_quantity, unit_price) " +
                "VALUES(?, ?, ?, ?, ?)";

        String sqlUpdate =
                "UPDATE ingredient SET total_quantity = total_quantity + ? WHERE i_id = ?";

        try {
            conn.setAutoCommit(false);

            String newId = generateId("SI");

            PreparedStatement ps1 = conn.prepareStatement(sqlIn);
            ps1.setString(1, newId);
            ps1.setString(2, ingredientId);
            ps1.setString(3, stockInfoId);
            ps1.setInt(4, inQuantity);
            ps1.setInt(5, unitPrice);
            ps1.executeUpdate();

            PreparedStatement ps2 = conn.prepareStatement(sqlUpdate);
            ps2.setInt(1, inQuantity);
            ps2.setString(2, ingredientId);
            ps2.executeUpdate();

            conn.commit();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            try { conn.rollback(); } catch (Exception ignored) {}
        }

        return false;
    }

    // 3. 출고 처리
    public boolean stockOut(String ingredientId, int outQuantity) {
        String sql =
                "UPDATE ingredient SET total_quantity = total_quantity - ? WHERE i_id = ? AND total_quantity >= ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, outQuantity);
            ps.setString(2, ingredientId);
            ps.setInt(3, outQuantity); // 재고 부족 방지

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // PK 자동 생성기
    private String generateId(String prefix) throws SQLException {
        String sql = "SELECT LPAD(IFNULL(MAX(CAST(SUBSTRING(stock_in_id, 3) AS UNSIGNED)), 0) + 1, 3, '0') AS newId FROM stock_in";

        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            return prefix + rs.getString("newId");
        }
        return null;
    }

    //ingredient 수량 증가
    public boolean addStock(String ingredientId, int qty) {
        String sql = "UPDATE ingredient SET total_quantity = total_quantity + ? WHERE i_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, qty);
            pstmt.setString(2, ingredientId);
            return pstmt.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    //ingredient 수량 감소
    public boolean subtractStock(String ingredientId, int qty) {
        String sql = "UPDATE ingredient SET total_quantity = total_quantity - ? WHERE i_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, qty);
            pstmt.setString(2, ingredientId);
            return pstmt.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    //stock_info 등록
    public String insertStockInfo(StockInfoDTO dto) {
        String id = createId("SI"); // 예: SI001
        String sql = "INSERT INTO stock_info(stock_info_id, i_id, unit_name, unit_quantity) VALUES(?, ?, ?, ?)";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, id);
            pstmt.setString(2, dto.getIngredientId());
            pstmt.setString(3, dto.getUnitName());
            pstmt.setInt(4, dto.getUnitQuantity());
            pstmt.executeUpdate();
            return id;

        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    //stock_in 등록
    public boolean insertStockIn(StockInDTO dto) {
        String sqlGetIid = "SELECT i_id, unit_quantity FROM stock_info WHERE stock_info_id = ?";
        String sqlInsert = "INSERT INTO stock_in (in_id, i_id, stock_info_id, in_quantity, unit_price, unit_quantity) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = util.DBUtil.getConnection()) {
            // 1) stock_info에서 i_id, unit_quantity 조회
            String ingredientId = null;
            int unitQuantity = 0;
            try (PreparedStatement psGet = conn.prepareStatement(sqlGetIid)) {
                psGet.setString(1, dto.getStockCode()); //dto.getStockCode() == stock_info_id
                try (ResultSet rs = psGet.executeQuery()) {
                    if (rs.next()) {
                        ingredientId = rs.getString("i_id");
                        unitQuantity = rs.getInt("unit_quantity");
                    } else {
                        // 해당 stock_info_id가 없으면 실패 처리
                        System.err.println("insertStockIn: stock_info_id not found -> " + dto.getStockCode());
                        return false;
                    }
                }
            }

            // 2) INSERT
            String newInId = createInId(); // 너의 ID 생성 로직을 호출
            try (PreparedStatement psIns = conn.prepareStatement(sqlInsert)) {
                psIns.setString(1, newInId);                      // in_id
                psIns.setString(2, ingredientId);                 // i_id
                psIns.setString(3, dto.getStockCode());          // stock_info_id (dto.getStockCode())
                psIns.setInt(4, dto.getAmount());                // in_quantity (dto.getAmount())
                psIns.setInt(5, 0);                              // unit_price (기본 0 혹은 UI에서 받도록 변경)
                psIns.setInt(6, unitQuantity);                   // unit_quantity (from stock_info)
                int cnt = psIns.executeUpdate();
                return cnt > 0;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private String createInId() {
        String sql = "SELECT LPAD(IFNULL(MAX(CAST(SUBSTRING(in_id, 3) AS UNSIGNED)), 0) + 1, 3, '0') AS newId FROM stock_in";
        Connection tmpConn = this.conn;
        boolean createdLocal = false;
        try {
            if (tmpConn == null || tmpConn.isClosed()) {
                tmpConn = DBConnection.getConnection();
                createdLocal = true;
            }
            try (PreparedStatement ps = tmpConn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return "IN" + rs.getString("newId");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (createdLocal && tmpConn != null) {
                try { tmpConn.close(); } catch (Exception ignored) {}
            }
        }
        //최후수단: 랜덤 ID
        return "IN" + (int)(Math.random() * 900 + 100);
    }

    //ID 자동 생성
    private String createId(String prefix) {
        return prefix + (int)(Math.random()*900 + 100);
    }
}
