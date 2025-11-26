package service;

import dao.SalesDAO;
import dto.SalesDTO;

import javax.swing.table.DefaultTableModel;
import java.util.List;

public class SalesTableService {
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
            // 데이터가 없는 경우, 빈 테이블만 남깁니다.
            return;
        }
        for (SalesDTO dto : salesList) {
            // DTO의 필드 순서대로 Object 배열 생성
            Object[] rowData = {
                    dto.getSalesId(),
                    dto.getMemberId(),
                    dto.getSalesDate(),
                    dto.getSalesTime(),
                    dto.getProduct(), // setProduct(rs.getString("p_name"))로 설정된 값
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
                    dto.getPrice()
            };
            tm.addRow(rowData);
        }
    }
}
