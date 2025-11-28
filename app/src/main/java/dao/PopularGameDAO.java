package dao;

import db.DBConnection;
import dto.PopularGameDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PopularGameDAO {
    //싱글톤
    private static PopularGameDAO instance;
    public static PopularGameDAO getInstance(){
        if(instance == null){
            instance = new PopularGameDAO();
        }
        return instance;
    }
    private PopularGameDAO() {}
    private final String popularSql = "select * from popular_game_view";
    public List<PopularGameDTO> getAllPopularGames() {
        List<PopularGameDTO> list = new ArrayList<>();
        try(Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(popularSql);
            ResultSet rs = ps.executeQuery()){

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
