package client.order;

import javax.swing.*;

import client.order.view.OrderHeader;
import client.order.view.OrderList;
import client.order.view.OrderState;

import java.awt.*;
import util.*;

/*
주문 화면 전체를 구성하는 패널 클래스

상단: OrderHeader
중앙: OrderState
하단: OrderList
*/
public class Order extends JPanel {

    public Order(){
        initUI();
    }

    //UI 초기설정 + 패널 배치
    private void initUI() {
        setSize(Sizes.PANEL_WIDTH, Sizes.PANEL_HEIGHT);
        setLayout(new BorderLayout());

        //최상단 헤더 패널
        OrderHeader header = new OrderHeader();
        add(header, BorderLayout.NORTH);
        
        //상태 패널(조리중/조리완료)
        OrderState state = new OrderState();
        state.setBackground(Color.CYAN);
        add(state, BorderLayout.CENTER);

        //메인 패널(주문 목록 표시)
        OrderList list = new OrderList();
        list.setBackground(Color.WHITE);
        add(list, BorderLayout.SOUTH);
    }
}