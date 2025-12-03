package dao;

import db.DBConnection;
import dto.MemberDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MemberDAO {
    // 싱글톤 패턴 적용
    private static MemberDAO instance;
    public static MemberDAO getInstance(){
        if(instance == null){
            instance = new MemberDAO();
        }
        return instance;
    }
    private MemberDAO() {}

    // 회원 전체 조회 (JTable 출력용)
    public List<MemberDTO> getAllMembers() {
        List<MemberDTO> list = new ArrayList<>();
        String sql = "SELECT * FROM member ORDER BY join_date DESC"; // 가입일 역순 정렬 예시

        try(Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()){

            while(rs.next()){
                MemberDTO dto = new MemberDTO();
                dto.setmId(rs.getString("m_id"));
                dto.setPasswd(rs.getString("passwd"));
                dto.setName(rs.getString("name"));
                dto.setBirth(rs.getDate("birth"));
                dto.setSex(rs.getString("sex"));
                dto.setRemainTime(rs.getInt("remain_time"));
                dto.setPhone(rs.getString("phone"));
                dto.setJoinDate(rs.getTimestamp("join_date"));
                list.add(dto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    public MemberDTO getMemberById(String id) {
        // 1. 여기서 초기화를 null로 할지, 빈 객체로 할지 결정해야 합니다.
        // 보통은 데이터가 없으면 null을 리턴해서 호출하는 쪽에서 알게 하는 게 좋습니다.
        MemberDTO dto = null;

        String sql = "SELECT * FROM member WHERE m_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            // [중요] 물음표(?)에 파라미터 바인딩
            ps.setString(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // 데이터가 있을 때만 객체 생성
                    dto = new MemberDTO();

                    dto.setmId(rs.getString("m_id"));
                    dto.setPasswd(rs.getString("passwd"));
                    dto.setName(rs.getString("name"));
                    dto.setBirth(rs.getDate("birth"));
                    dto.setSex(rs.getString("sex"));
                    dto.setRemainTime(rs.getInt("remain_time"));
                    dto.setPhone(rs.getString("phone"));
                    dto.setJoinDate(rs.getTimestamp("join_date"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace(); // 에러 로그를 꼭 확인하세요
        }

        return dto; // 회원이 없으면 null 반환
    }

    public List<MemberDTO> getMembersByName(String name){
        List<MemberDTO> list = new ArrayList<>();
        String sql = "SELECT * FROM member WHERE name LIKE ?";
        try(Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            )  {

            ps.setString(1, "%"+name+"%");
            try(ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    MemberDTO dto = new MemberDTO();
                    dto.setmId(rs.getString("m_id"));
                    dto.setPasswd(rs.getString("passwd"));
                    dto.setName(rs.getString("name"));
                    dto.setBirth(rs.getDate("birth"));
                    dto.setSex(rs.getString("sex"));
                    dto.setRemainTime(rs.getInt("remain_time"));
                    dto.setPhone(rs.getString("phone"));
                    dto.setJoinDate(rs.getTimestamp("join_date"));
                    list.add(dto);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    // 회원 가입 (INSERT)
    public boolean insertMember(MemberDTO dto) {
        String sql = "INSERT INTO member (m_id, passwd, name, birth, sex, remain_time, phone) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try(Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, dto.getmId());
            ps.setString(2, dto.getPasswd());
            ps.setString(3, dto.getName());
            ps.setDate(4, dto.getBirth());
            ps.setString(5, dto.getSex());
            ps.setInt(6, dto.getRemainTime());
            ps.setString(7, dto.getPhone());
            // join_date는 default current_timestamp이므로 생략 가능

            int result = ps.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 회원 정보 수정 (UPDATE) - ID 기준으로 나머지 정보 수정
    public boolean updateMember(MemberDTO dto) {
        String sql = "UPDATE member SET passwd=?, name=?, birth=?, sex=?, remain_time=?, phone=? WHERE m_id=?";

        try(Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, dto.getPasswd());
            ps.setString(2, dto.getName());
            ps.setDate(3, dto.getBirth());
            ps.setString(4, dto.getSex());
            ps.setInt(5, dto.getRemainTime());
            ps.setString(6, dto.getPhone());
            ps.setString(7, dto.getmId()); // WHERE 조건

            int result = ps.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    //  회원 삭제 (DELETE)
    public boolean deleteMember(String mId) {
        String sql = "DELETE FROM member WHERE m_id=?";

        try(Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, mId);

            int result = ps.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    // 유효성 검사
    public static boolean isMemberIdValid(String mId) {
        String sql = "SELECT m_id FROM member WHERE m_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, mId);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            // DB 연결 오류 또는 SQL 실행 오류 시
            e.printStackTrace();
            return false;
        }
    }
}
