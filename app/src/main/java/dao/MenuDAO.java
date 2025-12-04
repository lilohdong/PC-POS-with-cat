package dao;

import db.DBConnection;
import dto.MenuDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MenuDAO {
    // 카테고리 ID (Java 코드에서 필터링을 위한 임시 매핑)
    // 실제로는 DB에서 c_id를 조회하여 사용해야 하지만, UI 카테고리 이름과 매핑
    private static String getCIdByName(String cName){
        return switch (cName){
            case "라면" -> "C001";
            case "볶음밥" -> "C002";
            case "덮밥" -> "C003";
            case "분식" -> "C004";
            case "사이드" -> "C005";
            case "음료" -> "C006";
            case "과자" -> "C007";
            // "전체", "인기메뉴", "기타/요청"은 DB 필터링 X, Java에서 처리
            default -> null;
        };
    }

    /*
    카테고리별 또는 전체 메뉴를 조회
    @param categoryName 카테고리 이름 ("라면", "전체" 등)
    @return 해당 카테고리 메뉴 목록
    */
    public List<MenuDTO> getMenusByCategory(String categoryName){
        List<MenuDTO> menuList = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        String cId = getCIdByName(categoryName);
        String sql = "select menu_id, m_name, m_price, c_id from menu where is_soldout = false";

        if (cId != null){
            sql += " and c_id = ?";
        } else if (categoryName.equals("전체") || categoryName.equals("인기메뉴")) {
            // "전체"는 모든 메뉴, "인기메뉴"는 추후 로직 추가 (현재는 전체와 동일 처리)
        } else {
            // "기타/요청"과 같이 DB에 매핑되지 않는 카테고리는 빈 리스트 반환
            return menuList;
        }

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);

            if (cId != null){
                pstmt.setString(1, cId);
            }

            rs = pstmt.executeQuery();
            while (rs.next()){
                menuList.add(new MenuDTO(
                        rs.getString("menu_Id"),
                        rs.getString("m_name"),
                        rs.getInt("m_price"),
                        rs.getString("c_id")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(rs, pstmt, conn);
        }
        return menuList;
    }

    /*
    메뉴 이름으로 검색
    @param keyword 검색 키워드
    @return 검색된 메뉴 목록
    */
    public List<MenuDTO> searchMenus(String keyword){
        List<MenuDTO> menuList = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String sql = "select menu_id, m_name, m_price, c_id from menu where m_name like ? and is_soldout = false";

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, "%" + keyword + "%");

            rs = pstmt.executeQuery();
            while (rs.next()){
                menuList.add(new MenuDTO(
                        rs.getString("menu_id"),
                        rs.getString("m_name"),
                        rs.getInt("m_price"),
                        rs.getString("c_id")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(rs, pstmt, conn);
        }

        return menuList;
    }

    // (이하 기타 메뉴 관련 CRUD 메서드 추가 가능)
}