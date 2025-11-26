package client.order.view;

import javax.swing.*;
import java.awt.*;
import util.*;
import java.util.List;

import client.order.controller.OrderController;
import client.order.model.OrderData;

/*
주문 목록을 표시하는 패널
조리중 / 조리완료 목록을 모드에 따라 전환하여 표시
각 주문을 개변 컴포넌트로 화면에 나타냄
조리중 모드에서 "준비완료" 버튼 생성 -> 클릭 시 조리완료로 이동
조리완료 모드에서 "취소" 버튼 생성 -> 클릭 시 삭제(환불 처리)
*/
public class OrderList extends JPanel {
    private JPanel listAreaPanel; //주문 목록 표시되는 영역
    
    //표시 모드
    public static final int COOKING_MODE = 0;
    public static final int DONE_MODE = 1;
    private int currentMode = COOKING_MODE; //기본값 = 조리중

    public OrderList() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(Sizes.PANEL_WIDTH, 675));
        
        //주문목록 표시하는 영역 패널 설정
        listAreaPanel = new JPanel();
        listAreaPanel.setLayout(new BoxLayout(listAreaPanel, BoxLayout.Y_AXIS));
        //스크롤 추가
        add(new JScrollPane(listAreaPanel), BorderLayout.CENTER);
    }

    //모드에 따라 적절한 OrderData객체(주문 정보)를 표시
    public void displayOrders(List<OrderData> cookingList, List<OrderData> doneList) {
        listAreaPanel.removeAll();  //목록 초기화

        List<OrderData> targetList;
        //모드에 따라 표시할 목록 결정하는 조건
        if (currentMode == COOKING_MODE) {
            targetList = cookingList;            
        } else {
            targetList = doneList;
        }

        /*
        목록의 각 주문 데이터를 패널(UI)로 변환해서 추가
        targetlist가 null일 가능성을 위해 안전하게 처리
        */
        for (OrderData od : targetList) {
            listAreaPanel.add(createOrderPanel(od));
        }

        /*
        UI 처리 갱신
        revalidate: 컨테이너의 레이아웃 다시 계산하여 컴포넌트를 재배치
        repaint: 컴포넌트를 다시 페인팅하도록 요청
        */
        revalidate();
        repaint();
    }

    //주문을 화면에 표시하는 패널 생성(단일)
    private JPanel createOrderPanel(OrderData order) {
        JPanel menu = new JPanel();
        menu.setLayout(new GridLayout(0, 1));   //새로로 정보나열
        menu.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        menu.add(new JLabel("좌석: " + order.seatNum));
        menu.add(new JLabel("주문시간: " + order.orderTime));
        menu.add(new JLabel("경과시간: " + order.getCookingTime() + "분"));
        menu.add(new JLabel("주문내역: " + order.item));


        JButton finishBtn = new JButton("준비완료");
        JButton cancelBtn = new JButton("취소");
        if (currentMode == COOKING_MODE) {
            //조리중 목록: 조리완료 버튼 표시
            finishBtn.addActionListener(e -> {
                OrderController.markAsDone(order);
            });
            menu.add(finishBtn);
        }
        else {  //DONE_MODE일 때
            //조리완료 목록: 완료된 주문에만 표시되는 요소
            cancelBtn.addActionListener(e -> {
                OrderController.removeOrder(order);
            });
            menu.add(cancelBtn);
            menu.add(new JLabel("완료시간: " + order.completeTime));
            menu.add(new JLabel("걸린시간: " + order.getFinishCookingTime() + "분"));
        }

        return menu;
    }

    //표시모드 변경
    public void setMode(int mode) {
        currentMode = mode;
    }
}
