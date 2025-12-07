package client.order;

import client.order.controller.OrderController;
import client.order.view.OrderHeader;
import client.order.view.OrderList;
import client.order.view.OrderState;
import util.Sizes;

import javax.swing.*;
import java.awt.*;

/*
주문 관리 메인 화면을 구성하는 최상위 패널

전체 화면을 3개의 영역으로 나눔:
상단: OrderHeader (제목 + 주문하기 버튼)
중간: OrderState (조리중/조리완료 패널 전환 버튼)
하단: OrderList (실제 주문 목록 표시 영역)
*/
public class Order extends JPanel {

    public Order(){
        initUI();
    }

    /*
    UI 초기화 및 레이아웃 구성
    BorderLayout을 사용하여 상/중/하로 깔끔하게 배치
    */
    private void initUI() {
        setSize(Sizes.PANEL_WIDTH, Sizes.PANEL_HEIGHT);
        setLayout(new BorderLayout());

        //상단 패널: "주문 목록" 제목과 "주문하기" 버튼
        OrderHeader header = new OrderHeader();
        add(header, BorderLayout.NORTH);

        //중간 패널: 조리 상태 탭 (조리중 <-> 조리완료)
        OrderState state = new OrderState();
        state.setBackground(Color.LIGHT_GRAY);
        add(state, BorderLayout.CENTER);

        //하단 패널: 실제 주문 리스트가 표시되는 메인 영역
        OrderList list = new OrderList();
        list.setBackground(Color.WHITE);
        add(list, BorderLayout.SOUTH);

        //OrderController에 OrderList 패널을 연결하여 이후 갱신 가능하게 설정
        OrderController.init(list);
    }
}