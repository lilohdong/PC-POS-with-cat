package client.order.model;

import java.time.*;

/*
주문 정보 저장하는 데이터 모델 클래스(DB연동 필요)
좌석 번호, 주문 시간, 완료 시간, 주문한 상품 등을 저장
OrderList에서 조리중/조리완료 목록을 구성할 때 사용
*/
public class OrderData {
    public int seatNum;                 //좌석번호
    public LocalDateTime orderTime;     //주문 시간
    public LocalDateTime completeTime;  //주문 조리 완료 시간
    public String item;                 //주문 내역


    //생성자
    public OrderData(int seatNum, String item){
        this.seatNum = seatNum;
        this.item = item;
        this.orderTime = LocalDateTime.now();   //주문 시간 기록(현재 시간으로)
    }

    /*
    현재 시점 시준 조리 경과 시간을 계산 (분 단위)
    return 경과 시간(분)
    */
    public long getCookingTime() {
        return java.time.Duration.between(orderTime, LocalDateTime.now()).toMinutes();
    }

    /*
    주문 조리 완료 기준 조리 시간을 계산 (분 단위)
    completeTime이 null일 경우 0을 반환(왜?)
    */
    public long getFinishCookingTime() {
        if (completeTime == null) return 0;
        return java.time.Duration.between(orderTime, completeTime).toMinutes();
    }
}
