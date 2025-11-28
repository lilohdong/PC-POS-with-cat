package service;

import dao.SalesDAO;
import dto.SalesDTO;

import javax.swing.table.DefaultTableModel;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class SalesTableService {
    // 싱글톤
    private static SalesTableService instance;
    public static SalesTableService getInstance() {
        if (instance == null) {
            instance = new SalesTableService();
        }
        return instance;
    }

    public void updateTable(List<SalesDTO> salesList, DefaultTableModel tm) {
        tm.setRowCount(0);
        if (salesList == null || salesList.isEmpty()) {
            return;
        }
        for (SalesDTO dto : salesList) {
            Object[] rowData = {
                    dto.getSalesId(),
                    dto.getMemberId(),
                    dto.getSalesDate(),
                    dto.getSalesTime(),
                    dto.getProduct(), // setProduct(rs.getString("p_name"))로 설정된 값
                    dto.getQuantity(),
                    dto.getPrice()
            };
            // 모델에 행 추가
            tm.addRow(rowData);
        }
    }

    public void initTable(DefaultTableModel tm) {
        tm.setRowCount(0);
        SalesDAO dao = SalesDAO.getInstance();
        List<SalesDTO> list = dao.getSalesListAll();

        if (list == null || list.isEmpty()) {
            return;
        }
        for (SalesDTO dto : list) {
            Object[] rowData = {
                    dto.getSalesId(),
                    dto.getMemberId(),
                    dto.getSalesDate(),
                    dto.getSalesTime(),
                    dto.getProduct(),
                    dto.getQuantity(),
                    dto.getPrice()
            };
            tm.addRow(rowData);
        }
    }

    public String calculateTotalSales(DefaultTableModel tm) {
        int sum = 0;
        for(int i = 0; i < tm.getRowCount(); i++) {
            sum += (Integer)tm.getValueAt(i, 6);
        }
        NumberFormat numberFormat = NumberFormat.getInstance(Locale.KOREA);
        return "기간 매출액 : "+numberFormat.format(sum);
    }
}
