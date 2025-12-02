package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import db.DBConnection;
import dto.SeatDTO;

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
}
