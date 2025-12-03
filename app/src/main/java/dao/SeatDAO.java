package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import db.DBConnection;
import dto.SeatDTO;
import dto.SeatMemberInfoDTO;
import dto.PricePlanDTO;

public class SeatDAO {
    private static SeatDAO instance = new SeatDAO();
    private SeatDAO() {}
    public static SeatDAO getInstance() { return instance; }

    public List<SeatDTO> getAllSeats() {
        List<SeatDTO> list = new ArrayList<>();
        String sql = "SELECT seat_no, is_used, is_unavailable, m_id, login_time, end_time FROM seat ORDER BY seat_no ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                list.add(new SeatDTO(
                        rs.getInt("seat_no"),
                        rs.getBoolean("is_used"),
                        rs.getBoolean("is_unavailable"),
                        rs.getString("m_id"),
                        rs.getString("login_time"),
                        rs.getString("end_time")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public SeatMemberInfoDTO getSeatMemberInfo(int seatNo) {
        String sql = "SELECT * FROM seat_member_info_view WHERE seat_no = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, seatNo);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new SeatMemberInfoDTO(
                            rs.getInt("seat_no"),
                            rs.getString("m_id"),
                            rs.getString("name"),
                            rs.getTimestamp("birth"),
                            rs.getInt("remain_time"),
                            rs.getTimestamp("login_time")
                    );
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<PricePlanDTO> getPricePlans() {
        List<PricePlanDTO> list = new ArrayList<>();
        String sql = "SELECT * FROM price_plan ORDER BY price ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while(rs.next()) {
                list.add(new PricePlanDTO(
                        rs.getInt("plan_id"),
                        rs.getString("plan_name"),
                        rs.getInt("duration_min"),
                        rs.getInt("price")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean chargeTimeTransaction(String mId, int planId, int amount) {
        Connection conn = null;
        PreparedStatement pstmtLog = null;
        PreparedStatement pstmtMember = null;
        PreparedStatement pstmtPlan = null;

        String sqlLog = "INSERT INTO time_payment_log (m_id, plan_id, amount) VALUES (?, ?, ?)";
        String sqlMember = "UPDATE member SET remain_time = remain_time + ? WHERE m_id = ?";
        String sqlPlan = "SELECT duration_min FROM price_plan WHERE plan_id = ?";

        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            // 요금제 시간 조회
            pstmtPlan = conn.prepareStatement(sqlPlan);
            pstmtPlan.setInt(1, planId);
            ResultSet rs = pstmtPlan.executeQuery();

            int durationMin = 0;
            if (rs.next()) {
                durationMin = rs.getInt("duration_min");
            } else {
                throw new SQLException("Invalid plan_id");
            }
            rs.close();

            // 로그 저장
            pstmtLog = conn.prepareStatement(sqlLog);
            pstmtLog.setString(1, mId);
            pstmtLog.setInt(2, planId);
            pstmtLog.setInt(3, amount);
            pstmtLog.executeUpdate();

            // 회원 시간 추가
            pstmtMember = conn.prepareStatement(sqlMember);
            pstmtMember.setInt(1, durationMin);
            pstmtMember.setString(2, mId);
            pstmtMember.executeUpdate();

            conn.commit();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            try {
                if(conn != null) conn.rollback();
            } catch(SQLException ex) {
                ex.printStackTrace();
            }
            return false;
        } finally {
            try { if(pstmtLog != null) pstmtLog.close(); } catch(Exception e) {}
            try { if(pstmtMember != null) pstmtMember.close(); } catch(Exception e) {}
            try { if(pstmtPlan != null) pstmtPlan.close(); } catch(Exception e) {}
            try {
                if(conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch(Exception e) {}
        }
    }

    // 좌석 사용 시작
    public boolean startSeat(int seatNo, String memberId) {
        String sql = "UPDATE seat SET is_used = true, m_id = ?, login_time = CURRENT_TIMESTAMP, end_time = NULL WHERE seat_no = ? AND is_used = false AND is_unavailable = false";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, memberId);
            pstmt.setInt(2, seatNo);

            int result = pstmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 좌석 사용 종료
    public boolean endSeat(int seatNo) {
        String sql = "UPDATE seat SET is_used = false, m_id = NULL, login_time = NULL, end_time = CURRENT_TIMESTAMP WHERE seat_no = ? AND is_used = true";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, seatNo);

            int result = pstmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 좌석 이용 불가 토글
    public boolean toggleSeatAvailability(int seatNo, boolean makeUnavailable) {
        String sql = "UPDATE seat SET is_unavailable = ?, is_used = false, m_id = NULL, login_time = NULL, end_time = NULL WHERE seat_no = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setBoolean(1, makeUnavailable);
            pstmt.setInt(2, seatNo);

            int result = pstmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 회원의 사용 시간 차감 (사용 종료 시)
    public boolean deductUsedTime(String memberId, int usedMinutes) {
        String sql = "UPDATE member SET remain_time = GREATEST(remain_time - ?, 0) WHERE m_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, usedMinutes);
            pstmt.setString(2, memberId);

            int result = pstmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}