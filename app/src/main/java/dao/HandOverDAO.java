package dao;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

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
                dto.setCashReserve(rs.getInt("cash_reserve"));  // 전 타임 마감 시재
            }
        } catch (Exception e) { e.printStackTrace(); }
        return dto;
    }

    // 매출 집계
    public Map<String, Integer> getSalesData(Timestamp start, Timestamp end) {
        Map<String, Integer> result = new HashMap<>();
        result.put("cash", 0); result.put("card", 0);

        // orders 테이블에서 pay_method에 따라 집계
        String sql = "SELECT pay_method, SUM(total_price) as total FROM orders " +
                "WHERE o_time >= ? AND o_time <= ? AND o_status = 'COMPLETED' GROUP BY pay_method";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setTimestamp(1, start);
            ps.setTimestamp(2, end);
            try (ResultSet rs = ps.executeQuery()) {
                while(rs.next()) {
                    String method = rs.getString("pay_method");
                    int val = rs.getInt("total");
                    // DB에 저장된 ENUM 값에 따라 분기
                    if ("CREDIT-CARD".equals(method) || "신용카드".equals(method)) {
                        result.put("card", result.get("card") + val);
                    } else {
                        result.put("cash", result.get("cash") + val);
                    }
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
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

    // 마지막 인수자(=다음 인계자) 또는 기본값 반환
    public String getInitialGiver() {
        String sql = "SELECT receiver_id FROM handover ORDER BY ho_id DESC LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                String lastReceiver = rs.getString("receiver_id");
                if (lastReceiver == null || lastReceiver.trim().isEmpty()) {
                    return "사장님";
                }
                return lastReceiver;
            } else {
                // 테이블이 비어있을 때 기본값
                return "사장님";
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "사장님";
        }
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

    public void updateCashSafe(int amount) {
        String sql = "UPDATE cash_safe SET amount = ? WHERE id = 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, amount);
            int updated = ps.executeUpdate();
            if (updated == 0) {
                // 만약 행이 없다면 insert 시도 (안정성)
                String ins = "INSERT INTO cash_safe(id, amount) VALUES(1, ?)";
                try (PreparedStatement ps2 = conn.prepareStatement(ins)) {
                    ps2.setInt(1, amount);
                    ps2.executeUpdate();
                } catch (Exception ex) { ex.printStackTrace(); }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
