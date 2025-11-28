package dao;

import db.DBConnection;
import dto.SalesDTO;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class SalesDAO {
    private SalesDAO(){}
    private static SalesDAO instance;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
    public static SalesDAO getInstance(){
        if (instance == null){
            instance = new SalesDAO();
        }
        return instance;
    }
    public List<SalesDTO> getSalesListAll(){
        List<SalesDTO> list = new ArrayList<>();
        String sql = "select * " +
                "from sales_view";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()){
            while(rs.next()){
                SalesDTO dto = new SalesDTO();
                Timestamp timestamp = rs.getTimestamp("o_time");

                dto.setSalesId(rs.getString("sales_id"));
                dto.setMemberId(rs.getString("member_id"));
                dto.setSalesDate(dateFormat.format(timestamp));
                dto.setSalesTime(timeFormat.format(timestamp));
                dto.setProduct(rs.getString("m_name"));
                dto.setQuantity(rs.getInt("quantity"));
                dto.setPrice(rs.getInt("total_price"));

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
                "from sales_view " +
                "where (o_time BETWEEN ? AND ?) " +
                "AND (TIME(o_time) BETWEEN ? AND ?)";
        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);

            ps.setString(1, startDate);
            ps.setString(2, endDate);
            ps.setString(3, startTime+ ":00"); // 초 추가
            ps.setString(4, endTime+":00"); // 초 추가
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                SalesDTO dto = new SalesDTO();
                Timestamp timestamp = rs.getTimestamp("o_time");

                dto.setSalesId(rs.getString("sales_id"));
                dto.setMemberId(rs.getString("member_id"));
                dto.setSalesDate(dateFormat.format(timestamp));
                dto.setSalesTime(timeFormat.format(timestamp));
                dto.setProduct(rs.getString("m_name"));
                dto.setQuantity(rs.getInt("quantity"));
                dto.setPrice(rs.getInt("total_price"));

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
