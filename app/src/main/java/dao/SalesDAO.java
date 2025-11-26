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
    public List<SalesDTO> getSalesListAll(){
        List<SalesDTO> list = new ArrayList<>();
        String sql = "select * " +
                "from sales";
        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                SalesDTO dto = new SalesDTO();
                dto.setSalesId(rs.getString("sales_id"));
                dto.setMemberId(rs.getString("member_id"));
                dto.setSalesDate(rs.getString("sales_date"));
                dto.setSalesTime(rs.getString("sales_time"));
                dto.setProduct(rs.getString("product"));
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

    public List<SalesDTO> getSalesList(String startDate, String endDate,String startTime,String endTime){
        List<SalesDTO> list = new ArrayList<>();
        String sql = "select * " +
                "from sales " +
                "where (sales_date BETWEEN ? AND ?)" +
                "AND (sales_time BETWEEN ? AND ?)";
        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);

            ps.setString(1, startDate);
            ps.setString(2, endDate);
            ps.setString(3, startTime);
            ps.setString(4, endTime);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                SalesDTO dto = new SalesDTO();
                dto.setSalesId(rs.getString("sales_id"));
                dto.setMemberId(rs.getString("member_id"));
                dto.setSalesDate(rs.getString("sales_date"));
                dto.setSalesTime(rs.getString("sales_time"));
                dto.setProduct(rs.getString("product"));
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
