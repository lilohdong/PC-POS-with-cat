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
*/
public class OrderController {
    public static OrderList listPanel;
    private static OrderDAO orderDAO = new OrderDAO();

    //화면의 OrderList 패널 연결
    public static void init(OrderList panel) {
        listPanel = panel;
        changeMode(OrderList.COOKING_MODE);
    }

    //모드 전환 + UI갱신
    public static void changeMode(int mode) {
        listPanel.setMode(mode);

        String status = (mode == OrderList.COOKING_MODE) ? "PREPARING" : "COMPLETED";
        List<OrderDataDTO> orders = orderDAO.getOrdersByStatus(status);

        listPanel.displayOrdersDTO(orders);
    }

    //새로운 주문을 조리중 목록에 추가 + 화면 갱신
    public static String addNewOrder(OrderDataDTO orderDTO, List<MenuDTO> selectedMenus, List<Integer> quantities) {
        String newOId = orderDAO.insertNewOrder(orderDTO, selectedMenus, quantities);

        if (newOId != null) {
            changeMode(OrderList.COOKING_MODE);
            // 성공 시 주문 ID 반환
            return newOId;
        }
        // 실패 시 null 반환
        return null;
    }

    //조리중 -> 조리완료 이동
    public static void markAsDone(String oId) {
        orderDAO.updateOrderStatus(oId, "COMPLETED");

        // [수정] ListPanel 대신 OrderController의 listPanel 정적 변수를 사용하여 현재 모드 가져옴
        changeMode(listPanel.getCurrentMode());
    }

    // 주문 취소/환불 처리 (요청 4, 5번)
    public static void processCancel(OrderDataDTO orderDTO) {
        // orders 테이블의 o_status를 'CANCELED' 또는 'REFUNDED'로 변경하고 sales 차감
        // 이미 완료된 주문(COMPLETED)은 환불 프로세스, 조리중 주문(PREPARING)은 취소 프로세스라고 가정

        // 현재는 'REFUNDED'로 통합 처리하고 Sales 테이블 차감 로직 사용
        orderDAO.processRefund(orderDTO);

        // 주문 목록 UI 갱신 (현재 모드 유지하며 갱신)
        changeMode(listPanel.getCurrentMode());
    }

    // 주문 상세 내역 조회
    public static String getOrderDetails(String oId) {
        return orderDAO.getOrderDetails(oId);
    }
}
