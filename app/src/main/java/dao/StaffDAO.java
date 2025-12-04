package dao;

import db.DBConnection;
import dto.StaffDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StaffDAO {
    // 싱글톤 패턴
    private static StaffDAO instance;

    public static StaffDAO getInstance() {
        if (instance == null) {
            instance = new StaffDAO();
        }
        return instance;
    }

    private StaffDAO() {}

    // 전체 직원 조회 (재직 중인 직원만)
    public List<StaffDTO> getAllStaff() {
        List<StaffDTO> list = new ArrayList<>();
        String sql = "SELECT * FROM staff WHERE is_active = true ORDER BY hire_date DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                StaffDTO dto = new StaffDTO();
                dto.setStaffId(rs.getInt("staff_id"));
                dto.setStaffName(rs.getString("staff_name"));
                dto.setBirth(rs.getDate("birth"));
                dto.setGender(rs.getString("gender"));
                dto.setSalary(rs.getInt("salary"));
                dto.setHireDate(rs.getTimestamp("hire_date"));
                dto.setActive(rs.getBoolean("is_active"));
                dto.setPhone(rs.getString("phone"));
                list.add(dto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // ID로 직원 조회
    public StaffDTO getStaffById(int staffId) {
        StaffDTO dto = null;
        String sql = "SELECT * FROM staff WHERE staff_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, staffId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    dto = new StaffDTO();
                    dto.setStaffId(rs.getInt("staff_id"));
                    dto.setStaffName(rs.getString("staff_name"));
                    dto.setBirth(rs.getDate("birth"));
                    dto.setGender(rs.getString("gender"));
                    dto.setSalary(rs.getInt("salary"));
                    dto.setHireDate(rs.getTimestamp("hire_date"));
                    dto.setActive(rs.getBoolean("is_active"));
                    dto.setPhone(rs.getString("phone"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return dto;
    }

    // 이름으로 직원 검색
    public List<StaffDTO> getStaffByName(String name) {
        List<StaffDTO> list = new ArrayList<>();
        String sql = "SELECT * FROM staff WHERE staff_name LIKE ? AND is_active = true";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "%" + name + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    StaffDTO dto = new StaffDTO();
                    dto.setStaffId(rs.getInt("staff_id"));
                    dto.setStaffName(rs.getString("staff_name"));
                    dto.setBirth(rs.getDate("birth"));
                    dto.setGender(rs.getString("gender"));
                    dto.setSalary(rs.getInt("salary"));
                    dto.setHireDate(rs.getTimestamp("hire_date"));
                    dto.setActive(rs.getBoolean("is_active"));
                    dto.setPhone(rs.getString("phone"));
                    list.add(dto);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // 직원 추가
    public boolean insertStaff(StaffDTO dto) {
        String sql = "INSERT INTO staff (staff_name, birth, gender, salary, phone) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, dto.getStaffName());
            ps.setDate(2, dto.getBirth());
            ps.setString(3, dto.getGender());
            ps.setInt(4, dto.getSalary());
            ps.setString(5, dto.getPhone());

            int result = ps.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 전체 직원의 월급 합계 조회
    public int getTotalSalary() {
        String sql = "SELECT SUM(salary) FROM staff WHERE is_active = true";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // 직원 정보 수정
    public boolean updateStaff(StaffDTO dto) {
        String sql = "UPDATE staff SET staff_name=?, birth=?, gender=?, salary=?, phone=? WHERE staff_id=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, dto.getStaffName());
            ps.setDate(2, dto.getBirth());
            ps.setString(3, dto.getGender());
            ps.setInt(4, dto.getSalary());
            ps.setString(5, dto.getPhone());
            ps.setInt(6, dto.getStaffId());

            int result = ps.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 직원 삭제 (실제로는 is_active를 false로 변경 - 퇴사 처리)
    public boolean deleteStaff(int staffId) {
        String sql = "UPDATE staff SET is_active = false WHERE staff_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, staffId);

            int result = ps.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 직원 완전 삭제 (물리적 삭제)
    public boolean deleteStaffPermanently(int staffId) {
        String sql = "DELETE FROM staff WHERE staff_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, staffId);

            int result = ps.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 재직 중인 직원 수 조회
    public int getActiveStaffCount() {
        String sql = "SELECT COUNT(*) FROM staff WHERE is_active = true";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    public StaffDTO getLoginStaff(String name) {
        String sql = "SELECT * FROM staff WHERE staff_name = ?";
        StaffDTO dto = null;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                dto = new StaffDTO();
                dto.setStaffName(rs.getString("staff_name"));
                dto.setPasswd(rs.getString("passwd"));
            }
        }catch(Exception e) {
            System.out.println("DB 연동 실패");
            e.printStackTrace();
        }
        return dto;
    }
}