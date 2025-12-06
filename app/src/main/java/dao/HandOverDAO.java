package dao;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

import dto.HandOverDTO;
import db.DBConnection;

// DB 처리 클래스
public class HandOverDAO {

    // 마지막 인수인계 기록 조회 (현재 근무자 파악용)
    public HandOverDTO getLastHandoverData() {
        String sql = "SELECT * FROM handover ORDER BY ho_id DESC LIMIT 1";
        HandOverDTO dto = null;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                dto = new HandOverDTO();
                dto.setReceiverId(rs.getString("receiver_id")); // 전 타임 인수자가 현 타임 인계자
                dto.setEndTime(rs.getTimestamp("end_time"));    // 전 타임 끝난 시간이 현 타임 시작 시간
            }
        } catch (Exception e) { e.printStackTrace(); }
        return dto;
    }

    // 매출 집계
    public Map<String, Integer> getSalesData(Timestamp start, Timestamp end) {
        Map<String, Integer> result = new HashMap<>();
        result.put("pc", 0);
        result.put("cash", 0);
        result.put("card", 0);
        result.put("total", 0);

        try (Connection conn = DBConnection.getConnection()) {

            // PC 사용료 계산 (amount = 결제 금액)
            String pcSql =
                    "SELECT tpl.amount AS paid_amount " +
                            "FROM time_payment_log tpl " +
                            "WHERE tpl.pay_time >= ? AND tpl.pay_time <= ?";

            try (PreparedStatement ps = conn.prepareStatement(pcSql)) {
                ps.setTimestamp(1, start);
                ps.setTimestamp(2, end);

                try (ResultSet rs = ps.executeQuery()) {
                    int pcTotal = 0;
                    while (rs.next()) {
                        int paidAmount = rs.getInt("paid_amount");
                        pcTotal += paidAmount;  // 결제 금액 그대로 합산
                    }
                    result.put("pc", pcTotal);
                }
            }

            // 상품 매출 (CASH / CARD)
            String prodSql =
                    "SELECT o.pay_method, SUM(om.total_price) AS total " +
                            "FROM orders o " +
                            "JOIN order_menu om ON o.o_id = om.o_id " +
                            "WHERE o.o_status = 'COMPLETED' " +
                            "  AND o.o_time >= ? " +
                            "  AND o.o_time <= ? " +
                            "GROUP BY o.pay_method";

            try (PreparedStatement ps = conn.prepareStatement(prodSql)) {
                ps.setTimestamp(1, start);
                ps.setTimestamp(2, end);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        String method = rs.getString("pay_method");
                        int val = rs.getInt("total");
                        if ("CASH".equalsIgnoreCase(method)) {
                            result.put("cash", val);
                        } else if ("CARD".equalsIgnoreCase(method)) {
                            result.put("card", val);
                        }
                    }
                }
            }

            // 총 매출 계산
            int total =
                    result.get("pc") +
                            result.get("cash") +
                            result.get("card");

            result.put("total", total);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }


    // 인수인계 저장
    public boolean insertHandover(HandOverDTO dto) {
        String sql = "INSERT INTO handover(giver_id, receiver_id, start_time, end_time, total_sales, cash_sales, card_sales, cash_reserve, memo) VALUES(?,?,?,?,?,?,?,?,?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, dto.getGiverId());
            ps.setString(2, dto.getReceiverId());
            ps.setTimestamp(3, dto.getStartTime());
            ps.setTimestamp(4, dto.getEndTime());
            ps.setInt(5, dto.getTotalSales());
            ps.setInt(6, dto.getCashSales());
            ps.setInt(7, dto.getCardSales());
            ps.setInt(8, dto.getCashReserve());
            ps.setString(9, dto.getMemo());
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }


    // 금고 유지
    public int getCashSafe() {
        String sql = "SELECT amount FROM cash_safe WHERE id = 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("amount");
            } else {
                return 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }


    // 금고 금액 + 누적 차액 저장 (차액 덮어쓰기 방식)
    public void updateCashSafe(int amount, int accumulatedDiff) {

        String sql = "UPDATE cash_safe SET amount = ?, diff_accumulate = ? WHERE id = 1";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, amount);
            ps.setInt(2, accumulatedDiff);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 금고 금액과 누적 업무 차액 조회
    public int[] getCashSafeDetail() {
        String sql = "SELECT amount, diff_accumulate FROM cash_safe WHERE id = 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                int amount = rs.getInt("amount");
                int diffAcc = rs.getInt("diff_accumulate");
                return new int[]{amount, diffAcc};
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // 없으면 0,0
        return new int[]{0, 0};
    }

    // 직원 목록 가져오기
    public List<String> getStaffNames() {
        List<String> list = new ArrayList<>();
        String sql = "SELECT staff_name FROM staff WHERE is_active = 1";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(rs.getString("staff_name"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // 직원 비밀번호 검증

    public boolean verifyStaffPassword(String name, String password) {
        String sql = "SELECT passwd FROM staff WHERE staff_name = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getString("passwd").equals(password);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
