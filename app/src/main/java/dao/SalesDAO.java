package dao;

import db.DBConnection;
import dto.SalesDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SalesDAO {
    private SalesDAO(){}
    private static SalesDAO instance;

    public static SalesDAO getInstance(){
        if (instance == null){
            instance = new SalesDAO();
        }
        return instance;
    }
    public List<SalesDTO> getSalesListAll(){
        List<SalesDTO> list = new ArrayList<>();
        String sql = "select * " +
                "from sales_search";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()){
            while(rs.next()){
                SalesDTO dto = new SalesDTO();
                dto.setSalesId(rs.getString("sales_id"));
                dto.setMemberId(rs.getString("member_id"));
                dto.setSalesDate(rs.getString("sales_date"));
                dto.setSalesTime(rs.getString("sales_time").substring(11, 16)); // 시, 분만 짜름
                dto.setProduct(rs.getString("p_name"));
                dto.setPrice(rs.getInt("price"));

                list.add(dto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<SalesDTO> getSalesList(String startDate, String endDate,String startTime,String endTime){
        List<SalesDTO> list = new ArrayList<>();
        String sql = "select * " +
                "from sales_search " +
                "where (sales_date BETWEEN ? AND ?) " +
                "AND (TIME(sales_time) BETWEEN ? AND ?)";
        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);

            ps.setString(1, startDate);
            ps.setString(2, endDate);
            ps.setString(3, startTime+ ":00");
            ps.setString(4, endTime+":00");
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                SalesDTO dto = new SalesDTO();
                dto.setSalesId(rs.getString("sales_id"));
                dto.setMemberId(rs.getString("member_id"));
                dto.setSalesDate(rs.getString("sales_date"));
                dto.setSalesTime(rs.getString("sales_time").substring(11, 16));
                dto.setProduct(rs.getString("p_name"));
                dto.setPrice(rs.getInt("price"));
                list.add(dto);
            }
            rs.close();
            ps.close();
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
