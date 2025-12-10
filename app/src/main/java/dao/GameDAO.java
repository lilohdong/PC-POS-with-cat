package dao;

import db.DBConnection;
import dto.GameDTO;
import dto.GameStatisticDTO;
import dto.PopularGameDTO;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class GameDAO {
    //싱글톤
    private static GameDAO instance;
    public static GameDAO getInstance(){
        if(instance == null){
            instance = new GameDAO();
        }
        return instance;
    }
    private GameDAO() {}

    public List<PopularGameDTO> getPopularGamesByDate(LocalDate date) {
        List<PopularGameDTO> list = new ArrayList<>();

        // SQL이 아주 깔끔해졌습니다.
        String sql = "SELECT ranking, game_name, share_percent " +
                "FROM popular_game_view " +
                "WHERE play_date = ? " +
                "ORDER BY ranking ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDate(1, java.sql.Date.valueOf(date));

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    PopularGameDTO dto = new PopularGameDTO();
                    dto.setRank(rs.getInt("ranking"));
                    dto.setGameName(rs.getString("game_name"));
                    dto.setShare(rs.getDouble("share_percent"));
                    list.add(dto);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<GameStatisticDTO> getStatistics() {
        List<GameStatisticDTO> list = new ArrayList<>();
        String statisticsSql = "Select * from statistics_view";
        try(Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(statisticsSql);
            ResultSet rs = ps.executeQuery()){

            while(rs.next()){
                GameStatisticDTO dto = new GameStatisticDTO();
                dto.setRank(rs.getInt("ranking"));
                dto.setGameName(rs.getString("game_name"));
                // 뷰에서 이미 포맷된 문자열을 가져옴
                dto.setTotalTime(rs.getString("total_time_formatted"));
                dto.setUsers(rs.getInt("current_users")); //

                list.add(dto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    private String generateNextGameId(Connection conn) throws SQLException {
        String query = "SELECT g_id FROM game ORDER BY g_id DESC LIMIT 1";
        String lastId = null;

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            if (rs.next()) {
                lastId = rs.getString("g_id"); // 예: "G003"
            }
        }

        if (lastId == null) {
            return "G001"; // 데이터가 없으면 G001부터 시작
        }

        // 숫자 부분 추출 (예: "G003"에서 3을 추출)
        String numberPartStr = lastId.substring(1); // "003"
        int numberPart = Integer.parseInt(numberPartStr); // 3

        // 다음 숫자 생성 후 포맷팅 (예: 4 -> "004")
        int nextNumber = numberPart + 1;

        // String.format을 사용하여 'G'와 세 자리 숫자로 포맷팅
        return String.format("G%03d", nextNumber);
    }
    public GameDTO selectGame(String g_id) {
        // 반환할 GameDTO 객체를 null로 초기화합니다.
        // 데이터를 찾지 못하면 null을 반환하게 됩니다.
        GameDTO gameResult = null;

        String sql = "SELECT g_id, title, publisher FROM game where g_id = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection(); // DB 연결
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, g_id);

            rs = pstmt.executeQuery();

            if (rs.next()) {
                // 1. 데이터를 찾았을 때만 GameDTO 객체 생성
                gameResult = new GameDTO();

                // 2. ResultSet에서 값을 가져와 DTO에 설정
                gameResult.setG_id(rs.getString("g_id"));
                gameResult.setTitle(rs.getString("title"));
                gameResult.setPublisher(rs.getString("publisher"));
            }

        } catch (SQLException e) {
            System.err.println("DB 조회 오류: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // 자원 해제
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        // DB에서 값을 찾았다면 값이 채워진 gameResult 객체를,
        // 못 찾았다면 초기값인 null을 반환합니다.
        return gameResult;
    }
    public void insertGame(String title, String publisher) {
        String sql = "INSERT INTO game (g_id, title, publisher) VALUES (?, ?, ?)";
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBConnection.getConnection();
            // 1. 새로운 ID 생성
            String newId = generateNextGameId(conn);

            pstmt = conn.prepareStatement(sql);

            // 2. 파라미터 바인딩
            pstmt.setString(1, newId);
            pstmt.setString(2, title);
            pstmt.setString(3, publisher);

            // 3. 쿼리 실행
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("[삽입 성공] 새 게임이 성공적으로 추가되었습니다. ID: " + newId);
            } else {
                System.out.println("[삽입 실패] 게임 추가 중 오류가 발생했습니다.");
            }

        } catch (SQLException e) {
            System.err.println("DB 삽입 오류: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // 4. 자원 해제
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    public boolean updateGame(String id, String title, String publisher) {
        String sql = "UPDATE game SET title = ?, publisher = ? WHERE g_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);

            // 1. 파라미터 바인딩 (수정할 값)
            pstmt.setString(1, title);
            pstmt.setString(2, publisher);

            // 2. WHERE 조건 (수정할 대상 ID)
            pstmt.setString(3, id);

            // 3. 쿼리 실행
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("[수정 성공] 게임 ID " + id + "의 정보가 업데이트되었습니다.");
            } else {
                System.out.println("[수정 실패] 해당 ID의 게임을 찾을 수 없거나 변경된 내용이 없습니다. ID: " + id);
            }

        } catch (SQLException e) {
            System.err.println("DB 수정 오류: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // 4. 자원 해제
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return true;
    }
    public boolean deleteGame(String id) {
        String sql = "DELETE FROM game WHERE g_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);

            // 1. WHERE 조건 (삭제할 대상 ID)
            pstmt.setString(1, id);

            // 2. 쿼리 실행
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("[삭제 성공] 게임 ID " + id + "가 데이터베이스에서 삭제되었습니다.");
            } else {
                System.out.println("[삭제 실패] 해당 ID의 게임을 찾을 수 없습니다. ID: " + id);
            }

        } catch (SQLException e) {
            System.err.println("DB 삭제 오류: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // 3. 자원 해제
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return true;
    }
}
