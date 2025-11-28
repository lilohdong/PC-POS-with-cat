package dao;

import db.DBConnection;
import dto.GameStatisticDTO;
import dto.PopularGameDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

    public List<PopularGameDTO> getAllPopularGames() {
        List<PopularGameDTO> list = new ArrayList<>();
        String popularSql = "select * from popular_game_view";
        try(Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(popularSql);
            ResultSet rs = ps.executeQuery()){
            while(rs.next()){
                PopularGameDTO dto = new PopularGameDTO();
                dto.setRank(rs.getInt("ranking"));
                dto.setGameName(rs.getString("game_name"));
                dto.setShare(rs.getDouble("share_percent"));
                list.add(dto);
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
}
