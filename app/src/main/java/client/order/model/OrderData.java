package client.order.model;

import java.time.*;

public class OrderData {
    public int seatNum;
    public LocalDateTime orderTime;
    public LocalDateTime completeTime;
    public String item;

    public OrderData(int seatNum, String item){
        this.seatNum = seatNum;
        this.item = item;
        this.orderTime = LocalDateTime.now();
    }

    public long getCookingTime() {
        return java.time.Duration.between(orderTime, LocalDateTime.now()).toMinutes();
    }
    public long getFinishCookingTime() {
        if (completeTime == null) return 0;
        return java.time.Duration.between(orderTime, completeTime).toMinutes();
    }
}
