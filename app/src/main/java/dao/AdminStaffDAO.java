package dao;

import db.DBConnection;
import dto.AdminStaffDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AdminStaffDAO {
    private AdminStaffDAO() {}
    private static AdminStaffDAO instance;
    public static AdminStaffDAO getInstance() {
        if (instance == null) {
            instance = new AdminStaffDAO();
        }
        return instance;
    }

    public AdminStaffDTO getLoginStaff(String name) {
        String sql = "SELECT * FROM staff WHERE staff_name = ?";
        AdminStaffDTO dto = null;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                dto = new AdminStaffDTO();
                dto.setName(rs.getString("staff_name"));
                dto.setPasswd(rs.getString("passwd"));
            }
        }catch(Exception e) {
            System.out.println("DB 연동 실패");
            e.printStackTrace();
        }
        return dto;
    }
}
