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

        try (Connection conn = DBConnection.getConnection();     // DB 커넥션 유틸
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                list.add(
                        new SeatDTO(
                                rs.getInt("seat_no"),
                                rs.getBoolean("is_used"),
                                rs.getBoolean("is_unavailable"),
                                rs.getString("m_id"),
                                rs.getString("login_time"),
                                rs.getString("end_time")
                        )
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // 특정 좌석의 상세 정보 조회 (View 사용)
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

    // [요구사항 4] 요금제 목록 가져오기
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
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    // 시간 충전 트랜잭션 (로그 저장 + 시간 충전)
    public boolean chargeTimeTransaction(String mId, int seatNo, PricePlanDTO plan) {
        Connection conn = null;
        PreparedStatement pstmtLog = null;
        PreparedStatement pstmtMember = null;

        String sqlLog = "INSERT INTO time_payment_log (m_id, seat_no, plan_id, amount) VALUES (?, ?, ?, ?)";
        String sqlMember = "UPDATE member SET remain_time = remain_time + ? WHERE m_id = ?";

        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // 트랜잭션 시작

            // 1. 로그 저장
            pstmtLog = conn.prepareStatement(sqlLog);
            pstmtLog.setString(1, mId);
            pstmtLog.setInt(2, seatNo);
            pstmtLog.setInt(3, plan.getPlanId());
            pstmtLog.setInt(4, plan.getPrice());
            pstmtLog.executeUpdate();

            // 2. 회원 시간 추가
            pstmtMember = conn.prepareStatement(sqlMember);
            pstmtMember.setInt(1, plan.getDurationMin());
            pstmtMember.setString(2, mId);
            pstmtMember.executeUpdate();

            conn.commit(); // 커밋
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            try { if(conn != null) conn.rollback(); } catch(SQLException ex) {}
            return false;
        } finally {
            // 자원 해제
            try { if(pstmtLog != null) pstmtLog.close(); } catch(Exception e) {}
            try { if(pstmtMember != null) pstmtMember.close(); } catch(Exception e) {}
            try { if(conn != null) conn.setAutoCommit(true); conn.close(); } catch(Exception e) {}
        }
    }
}
