package client.order.controller;

//import client.order.model.OrderData;
import client.order.view.OrderList;
import dao.OrderDAO;
import dto.MenuDTO;
import dto.OrderDataDTO;

import java.util.List;

/*
주문 데이터들 관리하는 컨트롤러

조리중/조리완료 목록 저장 기능
새로운 주문 추가 기능
조리완료 처리 기능
주문 취소 처리 기능
OrderList UI 패널 업데이트 호출 기능

OrderList 뷰와 OrderDAO(데이터베이스) 사이의 중개자 역할
주문 상태 변경, 추가, 취소, 조회 등 모든 비즈니스 로직을 담당
정적 메서드 중심으로 설계되어 있어, 어디서든 쉽게 호출 가능
*/
public class OrderController {

    // OrderList 패널의 참조를 정적으로 보관 -> 모든 메서드에서 UI 갱신 가능
    public static OrderList listPanel;
    private static OrderDAO orderDAO = new OrderDAO();

    /*
    OrderList 패널을 컨트롤러에 연결하고 초기 화면을 조리중 탭으로 설정
    프로그램 시작 시 반드시 한 번 호출되어야 함
    */
    public static void init(OrderList panel) {
        listPanel = panel;
        changeMode(OrderList.COOKING_MODE);
    }

    /*
    조리중 ↔ 조리완료 탭 전환 시 호출
    DB에서 해당 상태의 주문만 조회하여 OrderList에 표시
    */
    public static void changeMode(int mode) {
        listPanel.setMode(mode);

        String status = (mode == OrderList.COOKING_MODE) ? "PREPARING" : "COMPLETED";
        List<OrderDataDTO> orders = orderDAO.getOrdersByStatus(status);

        listPanel.displayOrdersDTO(orders);
    }

    /*
    새로운 주문을 DB에 저장하고, 조리중 목록에 자동 추가
    return 성공 시 생성된 주문번호(oId), 실패 시 null
    */
    public static String addNewOrder(OrderDataDTO orderDTO, List<MenuDTO> selectedMenus, List<Integer> quantities) {
        String newOId = orderDAO.insertNewOrder(orderDTO, selectedMenus, quantities);

        if (newOId != null) {
            changeMode(OrderList.COOKING_MODE); // 조리중 탭으로 자동 이동 및 갱신
            return newOId;
        }
        // 실패 시 null 반환
        return null;
    }

    //조리중인 주문을 "준비완료" 처리 -> 조리완료 탭으로 이동
    public static void markAsDone(String oId) {
        orderDAO.updateOrderStatus(oId, "COMPLETED");
        changeMode(listPanel.getCurrentMode()); // 현재 보고 있던 탭 유지하면서 갱신
    }

    /*
    주문 취소 또는 환불 처리
    조리중 주문: 취소
    조리완료 주문: 환불
    실제로는 OrderDAO에서 매출 차감 및 상태 변경 처리
    */
    public static void processCancel(OrderDataDTO orderDTO) {
        orderDAO.processRefund(orderDTO);       //상태를 REFUNDED로 변경 + 매출 차감
        changeMode(listPanel.getCurrentMode()); // 현재 탭 유지하면서 목록 UI 갱신
    }

    /*
    주문번호로 해당 주문의 상세 메뉴 내역을 문자열로 반환
    예: "김치볶음밥 1개, 콜라 2개"
    */
    public static String getOrderDetails(String oId) {
        return orderDAO.getOrderDetails(oId);
    }
}
