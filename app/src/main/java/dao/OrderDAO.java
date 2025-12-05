package dao;

import db.DBConnection;
import dto.MenuDTO;
import dto.OrderDataDTO;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public String insertNewOrder(OrderDataDTO orderDTO, List<MenuDTO> selectedMenus, List<Integer> quantities) {
        Connection conn = null;
        PreparedStatement ordersPstmt = null;
        PreparedStatement orderMenuPstmt = null;

        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            // 1. 다음 o_id 생성 (O00001 형식)
            int nextOIdInt = getNextOIdInt(conn);
            String newOId = String.format("O%05d", nextOIdInt);  // varchar(6)에 맞춤, O00001

            // 2. orders 테이블 insert (o_id 직접 설정)
            String ordersSql = "INSERT INTO orders (o_id, m_id, seat_num, requestment, pay_method) VALUES (?, ?, ?, ?, ?)";
            ordersPstmt = conn.prepareStatement(ordersSql);
            ordersPstmt.setString(1, newOId);
            if ("NO_MEMBER".equals(orderDTO.getMId())) {
                ordersPstmt.setNull(2, Types.VARCHAR);
            } else {
                ordersPstmt.setString(2, orderDTO.getMId());
            }
            ordersPstmt.setInt(3, orderDTO.getSeatNum());
            ordersPstmt.setString(4, orderDTO.getRequestment());
            ordersPstmt.setString(5, orderDTO.getPayMethod());
            ordersPstmt.executeUpdate();

            // 3. order_menu insert
            int nextOrderMenuIdInt = getNextOrderMenuIdInt(conn);
            int totalOrderAmount = 0;
            String orderMenuSql = "INSERT INTO order_menu (order_menu_id, o_id, menu_id, quantity, unit_price) VALUES (?, ?, ?, ?, ?)";
            orderMenuPstmt = conn.prepareStatement(orderMenuSql);

            for (int i = 0; i < quantities.size(); i++) {
                MenuDTO menu = selectedMenus.get(i);
                int quantity = quantities.get(i);

                String orderMenuId = String.format("OM%05d", nextOrderMenuIdInt++);  // OM00001 형식, varchar(7)

                orderMenuPstmt.setString(1, orderMenuId);
                orderMenuPstmt.setString(2, newOId);
                orderMenuPstmt.setString(3, menu.getMenuId());
                orderMenuPstmt.setInt(4, quantity);
                orderMenuPstmt.setInt(5, menu.getMPrice());
                orderMenuPstmt.executeUpdate();

                totalOrderAmount += (menu.getMPrice() * quantity);
            }
            orderDTO.setTotalAmount(totalOrderAmount);

            // 4. sales insert/update
            insertOrUpdateSales(conn, orderDTO);

            conn.commit();
            return newOId;

        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    rollbackEx.printStackTrace();
                }
            }
            return null;
        } finally {
            DBConnection.close(ordersPstmt, orderMenuPstmt, conn);
        }
    }

    // 신규 메서드: 다음 o_id int max 얻기
    private int getNextOIdInt(Connection conn) throws SQLException {
        String sql = "SELECT MAX(CAST(SUBSTRING(o_id, 2) AS UNSIGNED)) FROM orders";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                Object result = rs.getObject(1);
                return (result == null) ? 0 : rs.getInt(1) + 1;
            }
            return 1;
        }
    }

    // 수정: int maxId 반환
    private int getNextOrderMenuIdInt(Connection conn) throws SQLException {
        String sql = "SELECT MAX(CAST(SUBSTRING(order_menu_id, 3) AS UNSIGNED)) FROM order_menu";  // OM00001 -> 00001
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                Object result = rs.getObject(1);
                return (result == null) ? 0 : rs.getInt(1) + 1;
            }
            return 1;
        }
    }

    /**
     * 주문 상태별 목록 조회 (Orders + Order_Menu 총액)
     * @param status 'PREPARING' 또는 'COMPLETED'
     * @return 주문 목록
     */
    public List<OrderDataDTO> getOrdersByStatus(String status) {
        List<OrderDataDTO> orderList = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        String sql = "SELECT o.o_id, o.seat_num, o.o_time, o.complete_time, o.o_status, o.requestment, o.pay_method, SUM(om.total_price) as total_amount " +
                "FROM orders o JOIN order_menu om ON o.o_id = om.o_id " +
                "WHERE o.o_status = ? " +
                "GROUP BY o.o_id, o.seat_num, o.o_time, o.complete_time, o.o_status , o.requestment, o.pay_method " +
                "ORDER BY o.o_time ASC";

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, status);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                LocalDateTime oTime = rs.getTimestamp("o_time").toLocalDateTime();
                Timestamp completeTs = rs.getTimestamp("complete_time");
                LocalDateTime completeTime = completeTs != null ? completeTs.toLocalDateTime() : null;

                OrderDataDTO order = new OrderDataDTO(
                        rs.getString("o_id"),
                        rs.getInt("seat_num"),
                        oTime,
                        rs.getString("o_status"),
                        rs.getInt("total_amount"),
                        rs.getString("requestment"),  // 추가
                        rs.getString("pay_method")    // 추가
                );
                order.setCompleteTime(completeTime);
                orderList.add(order);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(rs, pstmt, conn);
        }
        return orderList;
    }

    /**
     * 주문 상세 내역 조회 (주문 메뉴 목록)
     */
    public String getOrderDetails(String oId) {
        StringBuilder details = new StringBuilder();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        String sql = "SELECT m.m_name, om.quantity FROM order_menu om JOIN menu m ON om.menu_id = m.menu_id WHERE om.o_id = ?";

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, oId);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                details.append(rs.getString("m_name"))
                        .append(" (")
                        .append(rs.getInt("quantity"))
                        .append("개), ");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(rs, pstmt, conn);
        }

        if (details.length() > 0) {
            details.setLength(details.length() - 2); // 마지막 ", " 제거
        } else {
            details.append("주문 내역 없음");
        }
        return details.toString();
    }

    /**
     * 주문 상태 변경 (준비 완료/취소)
     */
    public boolean updateOrderStatus(String oId, String newStatus) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        // 'COMPLETED' 상태일 때만 complete_time 업데이트
        String sql = "UPDATE orders SET o_status = ?, complete_time = ? WHERE o_id = ?";

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, newStatus);

            if (newStatus.equals("COMPLETED")) {
                pstmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            } else {
                pstmt.setNull(2, Types.TIMESTAMP); // CANCELED/REFUNDED 시 complete_time 초기화
            }
            pstmt.setString(3, oId);

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DBConnection.close(pstmt, conn);
        }
    }

    // 환불 처리 (Refund 테이블 기록 및 Sales 테이블 업데이트)
    public boolean processRefund(OrderDataDTO orderDTO) {
        Connection conn = null;
        PreparedStatement refundPstmt = null;

        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            // 1. orders 상태를 'REFUNDED'로 변경
            updateOrderStatus(orderDTO.getOId(), "REFUNDED");

            // 2. refund 테이블에 기록
            int nextRefundIdInt = getNextRefundIdInt(conn);
            String refundId = String.format("R%04d", nextRefundIdInt);  // R0001 형식 (varchar(5))

            String refundSql = "INSERT INTO refund (r_id, o_id, r_amount) VALUES (?, ?, ?)";
            refundPstmt = conn.prepareStatement(refundSql);
            refundPstmt.setString(1, refundId);
            refundPstmt.setString(2, orderDTO.getOId());
            refundPstmt.setInt(3, orderDTO.getTotalAmount());
            refundPstmt.executeUpdate();

            // 3. sales 테이블에서 정산 금액 차감
            deductSales(conn, orderDTO);

            conn.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    rollbackEx.printStackTrace();
                }
            }
            return false;
        } finally {
            DBConnection.close(refundPstmt, conn);
        }
    }

    // sales 테이블에 일별 매출 추가/업데이트
    private void insertOrUpdateSales(Connection conn, OrderDataDTO orderDTO) throws SQLException {
        int amount = orderDTO.getTotalAmount();
        String payMethodField = orderDTO.getPayMethod().equals("CASH") ? "cash_sales" : "card_sales";

        String updateSql = "UPDATE sales SET total_sales = total_sales + ?, " + payMethodField + " = " + payMethodField + " + ? WHERE s_date = CURDATE()";

        try (PreparedStatement updatePstmt = conn.prepareStatement(updateSql)) {
            updatePstmt.setInt(1, amount);
            updatePstmt.setInt(2, amount);

            if (updatePstmt.executeUpdate() == 0) {
                // 해당 날짜의 기록이 없으면 새로 삽입
                int nextSalesIdInt = getNextSalesIdInt(conn);
                String nextSalesId = String.format("S%04d", nextSalesIdInt);  // S0001 형식 (varchar(5))

                String insertSql = "INSERT INTO sales (s_id, s_date, total_sales, card_sales, cash_sales) VALUES (?, CURDATE(), ?, ?, ?)";
                try (PreparedStatement insertPstmt = conn.prepareStatement(insertSql)) {
                    insertPstmt.setString(1, nextSalesId);
                    insertPstmt.setInt(2, amount);
                    insertPstmt.setInt(3, payMethodField.equals("card_sales") ? amount : 0);
                    insertPstmt.setInt(4, payMethodField.equals("cash_sales") ? amount : 0);
                    insertPstmt.executeUpdate();
                }
            }
        }
    }

    // sales 테이블에서 정산 금액 차감 (환불 시)
    private void deductSales(Connection conn, OrderDataDTO orderDTO) throws SQLException {
        int amount = orderDTO.getTotalAmount();
        String payMethodField = orderDTO.getPayMethod().equals("CASH") ? "cash_sales" : "card_sales";

        String updateSql = "UPDATE sales SET total_sales = total_sales - ?, " + payMethodField + " = " + payMethodField + " - ? WHERE s_date = DATE(?)";

        try (PreparedStatement updatePstmt = conn.prepareStatement(updateSql)) {
            updatePstmt.setInt(1, amount);
            updatePstmt.setInt(2, amount);
            // 주문이 발생한 날짜에서 차감 (o_time은 DTO에 없음. DB에서 조회 필요)
            // 실제 구현에서는 DB에서 해당 주문의 o_time을 가져와야 함. 여기서는 임시로 오늘 날짜로 가정.
            updatePstmt.setString(3, LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE));

            updatePstmt.executeUpdate();
        }
    }

    // refund 테이블의 다음 r_id를 가져오는 int 메서드
    private int getNextRefundIdInt(Connection conn) throws SQLException {
        String sql = "SELECT MAX(CAST(SUBSTRING(r_id, 2) AS UNSIGNED)) FROM refund";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                Object result = rs.getObject(1);
                return (result == null) ? 0 : rs.getInt(1) + 1;
            }
            return 1;
        }
    }

    // sales 테이블의 다음 s_id를 가져오는 int 메서드
    private int getNextSalesIdInt(Connection conn) throws SQLException {
        String sql = "SELECT MAX(CAST(SUBSTRING(s_id, 2) AS UNSIGNED)) FROM sales";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                Object result = rs.getObject(1);
                return (result == null) ? 0 : rs.getInt(1) + 1;
            }
            return 1;
        }
    }
}