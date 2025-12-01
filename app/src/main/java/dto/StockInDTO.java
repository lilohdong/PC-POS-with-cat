package dto;

import java.sql.Date;

public class StockInDTO {
    private String stock_info_id;  // stock_info 외래키
    private int in_quantity;        // 입고 수량
    private Date in_time;     // 선택: 직접 입력하지 않을 경우 null 가능

    public StockInDTO() {}

    public StockInDTO(String stock_info_id, int in_quantity) {
        this.stock_info_id = stock_info_id;
        this.in_quantity = in_quantity;
    }

    public String getStockCode() {
        return stock_info_id;
    }
    public void setStockCode(String stock_info_id) {
        this.stock_info_id = stock_info_id;
    }

    public int getAmount() {
        return in_quantity;
    }
    public void setAmount(int in_quantity) {
        this.in_quantity = in_quantity;
    }

    public Date getInDate() {
        return in_time;
    }
    public void setInDate(Date in_time) {
        this.in_time = in_time;
    }
}
