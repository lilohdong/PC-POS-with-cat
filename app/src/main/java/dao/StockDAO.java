package dao;

import db.DBConnection;
import dto.IngredientDTO;
import dto.StockInDTO;
import dto.StockInfoDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StockDAO {
    private Connection conn;

    public StockDAO() {
        try {
            this.conn = DBConnection.getConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 기존 getAllStock을 더 일반화된 검색 함수로 대체(호출 편의 위해 유지)
    public List<IngredientDTO> getAllStock() {
        return getStocks(null, null, null, "ALL");
    }

    /**
     * 검색/필터 기능 지원
     * name - 상품명 부분검색 (null이면 무시)
     * code - 상품코드(정확검색 또는 부분) (null이면 무시)
     * category - 카테고리 이름(예: "라면") (null 또는 "전체"이면 무시)
     * filterType - "ALL", "NORMAL" (total>min), "LACK" (total<min), "SOLDOUT" (is_out=true)
     */
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
                    sb.append("AND i.is_out = true ");
                    break;
                default:
                    // ALL: no extra where
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

    // 기존 메서드들과 용어 통일
    public boolean stockIn(String ingredientId, String stockInfoId, int inQuantity, int unitPrice) {
        // 기존과는 별도로 트랜잭션을 강제하여 입고 로그와 재고 수량 동기화
        String sqlInsertIn = "INSERT INTO stock_in(in_id, i_id, stock_info_id, in_quantity, unit_price) VALUES (?, ?, ?, ?, ?)";
        String sqlUpdateQty = "UPDATE ingredient SET total_quantity = total_quantity + ?, is_out = CASE WHEN total_quantity + ? > 0 THEN false ELSE true END WHERE i_id = ?";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            // 조회하여 unit_quantity를 stock_info에서 가져오자
            int unitQuantity = 1;
            String getUnitSql = "SELECT unit_quantity FROM stock_info WHERE stock_info_id = ?";
            try (PreparedStatement psGet = conn.prepareStatement(getUnitSql)) {
                psGet.setString(1, stockInfoId);
                try (ResultSet rs = psGet.executeQuery()) {
                    if (rs.next()) unitQuantity = rs.getInt("unit_quantity");
                }
            }

            String newInId = generateInId(conn);

            try (PreparedStatement psIns = conn.prepareStatement(sqlInsertIn)) {
                psIns.setString(1, newInId);
                psIns.setString(2, ingredientId);
                psIns.setString(3, stockInfoId);
                psIns.setInt(4, inQuantity);
                psIns.setInt(5, unitPrice);
                psIns.setInt(6, unitQuantity);
                psIns.executeUpdate();
            }

            try (PreparedStatement psUpd = conn.prepareStatement(sqlUpdateQty)) {
                int delta = inQuantity * unitQuantity;
                psUpd.setInt(1, delta);
                psUpd.setInt(2, delta);
                psUpd.setString(3, ingredientId);
                psUpd.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            // rollback handled by try-with-resources? do manual below
            return false;
        }
    }

    // addStock: 단순 단위(재고 수량) 추가 (UI에서 사용되는 빠른 입고)
    public boolean addStock(String ingredientId, int qty) {
        String sqlGetInfoId = "SELECT stock_info_id FROM stock_info WHERE i_id = ? LIMIT 1";
        String sqlInsertIn = "INSERT INTO stock_in(in_id, i_id, stock_info_id, in_quantity, unit_price) VALUES (?, ?, ?, ?, 0)";
        String sqlUpdate = "UPDATE ingredient SET total_quantity = total_quantity + ?, is_out = CASE WHEN total_quantity + ? > 0 THEN false ELSE true END WHERE i_id = ?";

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            String validStockInfoId = null;
            try(PreparedStatement psGet = conn.prepareStatement(sqlGetInfoId)){
                psGet.setString(1, ingredientId);
                try(ResultSet rs = psGet.executeQuery()){
                    if (rs.next()){
                        validStockInfoId = rs.getString("stock_info_id");
                    }
                }
            }

            if (validStockInfoId == null) {
                System.err.println("addStock failed: No valid stock_info found for i_id: " + ingredientId);
                conn.rollback();
                return false;
            }

            if (validStockInfoId == null){
                System.err.println("addStock failed: No valid stock_info found for i_id: " + ingredientId);
                conn.rollback();
                return false;
            }
            String newInId = generateInId(conn);

            //stock_in 기록
            try(PreparedStatement psIns = conn.prepareStatement(sqlInsertIn)){
                psIns.setString(1, newInId);
                psIns.setString(2, ingredientId);
                psIns.setString(3, validStockInfoId);
                psIns.setInt(4, qty);
                psIns.executeUpdate();
            }

            //ingredient 재고 갱신
            try(PreparedStatement psUpd = conn.prepareStatement(sqlUpdate)){
                psUpd.setInt(1, qty);
                psUpd.setInt(2, qty);
                psUpd.setString(3, ingredientId);
                psUpd.executeUpdate();
            }

            conn.commit();
            return true;
        }catch (Exception e){
            e.printStackTrace();
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException rollcackE) {
                rollcackE.printStackTrace();
            }
            return false;
        }finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                }catch (SQLException closeE){
                    closeE.printStackTrace();
                }
            }
        }
    }


    // subtractStock: 출고. 출고 기록을 stock_out에 남기고 ingredient.total_quantity 갱신
    public boolean subtractStock(String ingredientId, int qty) {
        String sqlCheck = "SELECT total_quantity FROM ingredient WHERE i_id = ?";
        String sqlUpdate = "UPDATE ingredient SET total_quantity = total_quantity - ?, is_out = CASE WHEN total_quantity - ? <= 0 THEN true ELSE false END WHERE i_id = ? AND total_quantity >= ?";
        String sqlInsertOut = "INSERT INTO stock_out(out_id, i_id, out_quantity) VALUES (?, ?, ?)";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            // 1) check current qty
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

            if (current < qty) {
                conn.rollback();
                return false;
            }

            // 2) insert out record
            String outId = generateOutId(conn);
            try (PreparedStatement psOut = conn.prepareStatement(sqlInsertOut)) {
                psOut.setString(1, outId);
                psOut.setString(2, ingredientId);
                psOut.setInt(3, qty);
                psOut.executeUpdate();
            }

            // 3) update ingredient
            try (PreparedStatement psUpd = conn.prepareStatement(sqlUpdate)) {
                psUpd.setInt(1, qty);
                psUpd.setInt(2, qty);
                psUpd.setString(3, ingredientId);
                psUpd.setInt(4, qty);
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

    // 기존 insertStockInfo 유지 (주로 AddStockFrame에서 사용됨)
    public String insertStockInfo(StockInfoDTO dto) {
        String id = this.createId("SI");
        String sql = "INSERT INTO stock_info(stock_info_id, i_id, unit_name, unit_quantity) VALUES(?, ?, ?, ?)";

        try (
            Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
        ) {
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
        String sqlGetIid = "SELECT i_id, unit_quantity FROM stock_info WHERE stock_info_id = ?";
        String sqlInsert = "INSERT INTO stock_in (in_id, i_id, stock_info_id, in_quantity, unit_price) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection()) {
            String ingredientId = null;
            int unitQuantity = 1;

            try (PreparedStatement psGet = conn.prepareStatement(sqlGetIid)) {
                psGet.setString(1, dto.getStockCode());
                try (ResultSet rs = psGet.executeQuery()) {
                    if (!rs.next()) {
                        System.err.println("insertStockIn: stock_info_id not found -> " + dto.getStockCode());
                        return false;
                    }
                    ingredientId = rs.getString("i_id");
                    unitQuantity = rs.getInt("unit_quantity");
                }
            }

            String newInId = this.createInId(conn);

            try (PreparedStatement psIns = conn.prepareStatement(sqlInsert)) {
                psIns.setString(1, newInId);
                psIns.setString(2, ingredientId);
                psIns.setString(3, dto.getStockCode());
                psIns.setInt(4, dto.getAmount());
                psIns.setInt(5, 0);
                psIns.setInt(6, unitQuantity);
                int cnt = psIns.executeUpdate();
                // 합산하여 ingredient.total_quantity를 업데이트
                String sqlUpdate = "UPDATE ingredient SET total_quantity = total_quantity + ?, is_out = CASE WHEN total_quantity + ? > 0 THEN false ELSE true END WHERE i_id = ?";
                try (PreparedStatement psUpd = conn.prepareStatement(sqlUpdate)) {
                    int delta = dto.getAmount() * unitQuantity;
                    psUpd.setInt(1, delta);
                    psUpd.setInt(2, delta);
                    psUpd.setString(3, ingredientId);
                    psUpd.executeUpdate();
                }
                return cnt > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // helper methods to generate ids (안정적인 DB기반 증가 방식 사용)
    private String generateInId(Connection conn) throws SQLException {
        String sql = "SELECT LPAD(IFNULL(MAX(CAST(SUBSTRING(in_id, 3) AS UNSIGNED)), 0) + 1, 3, '0') AS newId FROM stock_in";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return "IN" + rs.getString("newId");
            }
        }
        // fallback
        return "IN" + System.currentTimeMillis() % 1000;
    }

    private String createInId(Connection conn) throws SQLException {
        return generateInId(conn);
    }

    // DB 연결 없는 버전 (기존 호출 대응)
    private String createInId() {
        String sql = "SELECT LPAD(IFNULL(MAX(CAST(SUBSTRING(in_id, 3) AS UNSIGNED)), 0) + 1, 3, '0') AS newId FROM stock_in";
        try (Connection tmpConn = DBConnection.getConnection();
             PreparedStatement ps = tmpConn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return "IN" + rs.getString("newId");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "IN" + (int)(Math.random() * 900 + 100);
    }

    private String generateOutId(Connection conn) throws SQLException {
        String sql = "SELECT LPAD(IFNULL(MAX(CAST(SUBSTRING(out_id, 4) AS UNSIGNED)), 0) + 1, 3, '0') AS newId FROM stock_out";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return "OUT" + rs.getString("newId"); // OUT001 ...
            }
        }
        return "OUT" + System.currentTimeMillis() % 1000;
    }

    private String createId(String prefix) {
        return prefix + (int)(Math.random() * (double)900.0F + (double)100.0F);
    }
}