package dao;

import db.DBConnection;
import dto.IngredientDTO;
import dto.StockInDTO;
import dto.StockInfoDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/*
재고 관리의 핵심 DAO

전체 재고 조회
검색 및 상태 필터링 (정상/부족/품절)
입고/출고 처리 (트리거 기반 + 수동 처리 혼용)
재고 단위 변환 (박스 → 개수) 처리
*/
public class StockDAO {
    private Connection conn;

    public StockDAO() {
        try {
            this.conn = DBConnection.getConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<IngredientDTO> getAllStock() {
        return getStocks(null, null, null, "ALL");
    }

    public List<IngredientDTO> getStocks(String name, String code, String category, String filterType) {
        List<IngredientDTO> list = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT i.i_id, i.i_name, c.c_name, ");
        sb.append("(SELECT unit_price FROM stock_in WHERE i_id = i.i_id ORDER BY in_time DESC LIMIT 1) AS unit_price, ");
        sb.append("i.total_quantity, i.min_quantity, i.store_location, i.is_out ");
        sb.append("FROM ingredient i LEFT JOIN ingredient_category c ON i.c_id = c.c_id ");
        sb.append("WHERE 1=1 ");

        List<Object> params = new ArrayList<>();

        if (name != null && !name.trim().isEmpty()) {
            sb.append("AND i.i_name LIKE ? ");
            params.add("%" + name.trim() + "%");
        }
        if (code != null && !code.trim().isEmpty()) {
            sb.append("AND i.i_id LIKE ? ");
            params.add("%" + code.trim() + "%");
        }
        if (category != null && !category.trim().isEmpty() && !"전체".equals(category)) {
            sb.append("AND c.c_name = ? ");
            params.add(category);
        }

        if (filterType != null) {
            switch (filterType) {
                case "NORMAL":
                    sb.append("AND i.total_quantity > i.min_quantity ");
                    break;
                case "LACK":
                    sb.append("AND i.total_quantity < i.min_quantity ");
                    break;
                case "SOLDOUT":
                    // 수정 1: 품절 버튼 클릭 시 재고가 0 이하인 것만 조회
                    sb.append("AND i.total_quantity <= 0 ");
                    break;
                default:
            }
        }

        String sql = sb.toString();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
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
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
    public boolean stockIn(String ingredientId, String stockInfoId, int inQuantity, int unitPrice) {
        String sqlInsertIn = "INSERT INTO stock_in(in_id, i_id, stock_info_id, in_quantity, unit_price) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection()) {

            String newInId = generateInId(conn);

            try (PreparedStatement psIns = conn.prepareStatement(sqlInsertIn)) {
                psIns.setString(1, newInId);
                psIns.setString(2, ingredientId);
                psIns.setString(3, stockInfoId);
                psIns.setInt(4, inQuantity);
                psIns.setInt(5, unitPrice);
                psIns.executeUpdate();
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean addStock(String ingredientId, int inputQty) {
        String sqlGetInfo = "SELECT stock_info_id FROM stock_info WHERE i_id = ? LIMIT 1";
        String sqlInsertIn = "INSERT INTO stock_in(in_id, i_id, stock_info_id, in_quantity, unit_price) VALUES (?, ?, ?, ?, 0)";

        try (Connection conn = DBConnection.getConnection()) {
            String stockInfoId = null;
            try (PreparedStatement ps = conn.prepareStatement(sqlGetInfo)) {
                ps.setString(1, ingredientId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        stockInfoId = rs.getString("stock_info_id");
                    }
                }
            }

            String newInId = generateInId(conn);
            try (PreparedStatement psIns = conn.prepareStatement(sqlInsertIn)) {
                psIns.setString(1, newInId);
                psIns.setString(2, ingredientId);
                psIns.setString(3, stockInfoId);
                psIns.setInt(4, inputQty);
                psIns.executeUpdate();
            }
            // UPDATE ingredient 쿼리 제거됨 (DB Trigger가 수행)
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean subtractStock(String ingredientId, int delta) {
        String sqlCheck = "SELECT total_quantity FROM ingredient WHERE i_id = ?";
        String sqlUpdate = "UPDATE ingredient SET total_quantity = total_quantity - ?, is_out = CASE WHEN total_quantity - ? <= 0 THEN true ELSE false END WHERE i_id = ? AND total_quantity >= ?";
        String sqlInsertOut = "INSERT INTO stock_out(out_id, i_id, out_quantity) VALUES (?, ?, ?)";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            int current = 0;
            try (PreparedStatement psCheck = conn.prepareStatement(sqlCheck)) {
                psCheck.setString(1, ingredientId);
                try (ResultSet rs = psCheck.executeQuery()) {
                    if (rs.next()) current = rs.getInt("total_quantity");
                    else {
                        conn.rollback();
                        return false;
                    }
                }
            }

            if (current < delta) {
                conn.rollback();
                return false;
            }

            int unitQty = getUnitQuantityByIngredientId(ingredientId);
            int inputQty = (unitQty > 0) ? delta / unitQty : delta;

            String outId = generateOutId(conn);
            try (PreparedStatement psOut = conn.prepareStatement(sqlInsertOut)) {
                psOut.setString(1, outId);
                psOut.setString(2, ingredientId);
                psOut.setInt(3, inputQty);
                psOut.executeUpdate();
            }

            try (PreparedStatement psUpd = conn.prepareStatement(sqlUpdate)) {
                psUpd.setInt(1, delta);
                psUpd.setInt(2, delta);
                psUpd.setString(3, ingredientId);
                psUpd.setInt(4, delta);
                int updated = psUpd.executeUpdate();
                if (updated == 0) {
                    conn.rollback();
                    return false;
                }
            }

            conn.commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public int getUnitQuantityByIngredientId(String iId){
        String sql = "SELECT unit_quantity FROM stock_info WHERE i_id = ? LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setString(1, iId);
            try (ResultSet rs = ps.executeQuery()){
                if (rs.next()){
                    return rs.getInt("unit_quantity");
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return 1;
    }

    public String insertStockInfo(StockInfoDTO dto) {
        String id = this.createId("SI");
        String sql = "INSERT INTO stock_info(stock_info_id, i_id, unit_name, unit_quantity) VALUES(?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            pstmt.setString(2, dto.getIngredientId());
            pstmt.setString(3, dto.getUnitName());
            pstmt.setInt(4, dto.getUnitQuantity());
            pstmt.executeUpdate();
            return id;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean insertStockIn(StockInDTO dto) {
        String sqlGetIid = "SELECT i_id FROM stock_info WHERE stock_info_id = ?";
        String sqlInsert = "INSERT INTO stock_in (in_id, i_id, stock_info_id, in_quantity, unit_price) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection()) {
            String ingredientId = null;

            try (PreparedStatement psGet = conn.prepareStatement(sqlGetIid)) {
                psGet.setString(1, dto.getStockCode());
                try (ResultSet rs = psGet.executeQuery()) {
                    if (!rs.next()) return false;
                    ingredientId = rs.getString("i_id");
                }
            }

            String newInId = this.createInId(conn);

            try (PreparedStatement psIns = conn.prepareStatement(sqlInsert)) {
                psIns.setString(1, newInId);
                psIns.setString(2, ingredientId);
                psIns.setString(3, dto.getStockCode());
                psIns.setInt(4, dto.getAmount());
                psIns.setInt(5, 0);
                // unit_price나 unit_quantity 계산은 Trigger에서 처리됨
                return psIns.executeUpdate() > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private String generateInId(Connection conn) throws SQLException {
        String sql = "SELECT LPAD(IFNULL(MAX(CAST(SUBSTRING(in_id, 3) AS UNSIGNED)), 0) + 1, 3, '0') AS newId FROM stock_in";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return "IN" + rs.getString("newId");
            }
        }
        return "IN" + System.currentTimeMillis() % 1000;
    }

    private String createInId(Connection conn) throws SQLException {
        return generateInId(conn);
    }

    private String generateOutId(Connection conn) throws SQLException {
        String sql = "SELECT LPAD(IFNULL(MAX(CAST(SUBSTRING(out_id, 4) AS UNSIGNED)), 0) + 1, 3, '0') AS newId FROM stock_out";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return "OUT" + rs.getString("newId");
            }
        }
        return "OUT" + System.currentTimeMillis() % 1000;
    }

    private String createId(String prefix) {
        return prefix + (int)(Math.random() * 900.0F + 100.0F);
    }
}