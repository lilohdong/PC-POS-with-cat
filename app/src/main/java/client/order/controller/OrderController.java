package client.order.controller;

import java.util.ArrayList;
import java.util.List;

import client.order.model.OrderData;
import client.order.view.OrderList;

/*
주문 데이터들 관리하는 컨트롤러

조리중/조리완료 목록 저장 기능
새로운 주문 추가 기능
조리완료 처리 기능
주문 취소 처리 기능
OrderList UI 패널 업데이트 호출 기능
*/
public class OrderController {
    public static List<OrderData> cookingList = new ArrayList<>();  //조리중 목옥
    public static List<OrderData> doneList = new ArrayList<>();     //조리완료 목록

    //UI 갱신: OrderList 패널 참조
    public static OrderList listPanel;

    //화면의 OrderList 패널 연결
    public static void init(OrderList panel) {
        listPanel = panel;
    }

    //모드 전환 + UI갱신
    public static void changeMode(int mode) {
        listPanel.setMode(mode);
        listPanel.displayOrders(cookingList, doneList);
    }

    //새로운 주문을 조리중 목록에 추가 + 화면 갱신
    public static void addNewOrder(OrderData od) {
        cookingList.add(od);
        listPanel.displayOrders(cookingList, doneList);
    }

    //조리중 -> 조리완료 이동
    public static void markAsDone(OrderData od) {
        od.completeTime = java.time.LocalDateTime.now();
        cookingList.remove(od);
        doneList.add(od);

        listPanel.displayOrders(cookingList, doneList);
    }

    //조리완료 목록에서 주문 제거(취소 처리)
    public static void removeOrder(OrderData od) {
        doneList.remove(od);
        listPanel.displayOrders(cookingList, doneList);
    }
}
