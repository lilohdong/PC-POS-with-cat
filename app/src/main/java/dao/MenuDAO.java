package dao;

import db.DBConnection;
import dto.MenuDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


/*
메뉴 관련 데이터베이스 접근을 담당하는 DAO 클래스

기능:
카테고리별 메뉴 조회
메뉴명 검색
*/
public class MenuDAO {

    /* UI에서 사용하는 카테고리 이름(예: "라면")을 DB의 c_id(예: "C001")로 매핑
    추후에는 DB에 카테고리 이름 테이블을 두고 JOIN으로 처리하는 것이 더 좋음
    */
    private static String getCIdByName(String cName){
        return switch (cName){
            case "라면" -> "C001";
            case "볶음밥" -> "C002";
            case "덮밥" -> "C003";
            case "분식" -> "C004";
            case "사이드" -> "C005";
            case "음료" -> "C006";
            case "과자" -> "C007";
            default -> null;    // "전체", "인기메뉴", "기타/요청"은 DB 필터링 없음, Java에서 처리
        };
    }

    /*
    카테고리별로 판매 중인 메뉴 목록 조회
    @param categoryName UI에서 선택된 카테고리 이름
    @return 해당 카테고리의 메뉴 리스트 (판매 중인 것만)
    */
    public List<MenuDTO> getMenusByCategory(String categoryName){
        List<MenuDTO> menuList = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        String cId = getCIdByName(categoryName);
        String sql = "select menu_id, m_name, m_price, c_id from menu where is_soldout = false";

        //특정 카테고리 선택 시 c_id 조건 추가
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
    메뉴명 일부 입력으로 검색 (LIKE 검색)
    @param keyword 검색어 (공백 제거 후 사용)
    @return 일치하는 판매 중인 메뉴 목록
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