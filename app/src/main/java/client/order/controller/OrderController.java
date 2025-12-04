package client.order.controller;

//import client.order.model.OrderData;
import client.order.view.OrderList;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/*
주문 데이터들 관리하는 컨트롤러

조리중/조리완료 목록 저장 기능
새로운 주문 추가 기능
조리완료 처리 기능
주문 취소 처리 기능
OrderList UI 패널 업데이트 호출 기능
*/
public class OrderController {
    public static List<String> cookingList = new ArrayList<>();  //조리중 목옥
    public static List<String> doneList = new ArrayList<>();     //조리완료 목록

    //UI 갱신: OrderList 패널 참조
    public static OrderList listPanel;

    //화면의 OrderList 패널 연결
    public static void init(OrderList panel) {
        listPanel = panel;
        //테스트용 임시 Data
        String nowTime = LocalDateTime.now().toString().substring(11, 16);
        String before5Min =  LocalDateTime.now().minusMinutes(5).toString().substring(11, 16);
        cookingList.add("좌석: 101, 주문내역: 라면, 시간: " + before5Min);
        cookingList.add("좌석: 102, 주문내역: 라면, 시간: " + nowTime);

        //초기화면 호출
        changeMode(OrderList.COOKING_MODE);
    }

    //모드 전환 + UI갱신
    public static void changeMode(int mode) {
        listPanel.setMode(mode);
        listPanel.displayOrdersString(cookingList, doneList);
    }

    //새로운 주문을 조리중 목록에 추가 + 화면 갱신
    public static void addNewOrder(String orderInfo) {
        cookingList.add(orderInfo);
        listPanel.displayOrdersString(cookingList, doneList);
    }

    //조리중 -> 조리완료 이동
    public static void markAsDone(String orderInfo) {
        cookingList.remove(orderInfo);
        //완료시간 임시 추가
        String[] parts = orderInfo.split(", 시간: ");
        String baseInfo = parts[0];
        String orderTime = parts.length > 1 ? parts[1] : "?";

        String doneInfo = baseInfo + ", 완료시간: " + LocalDateTime.now().toString().substring(11, 16) + ", 시간: " + orderTime;
        doneList.add(doneInfo);

        listPanel.displayOrdersString(cookingList, doneList);
    }

    //조리완료 목록에서 주문 제거(취소 처리)
    public static void removeOrder(String orderInfo) {
        doneList.remove(orderInfo);
        listPanel.displayOrdersString(cookingList, doneList);
    }
}
