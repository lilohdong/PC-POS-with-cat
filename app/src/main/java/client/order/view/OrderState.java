package client.order.view;

import javax.swing.*;
import java.awt.*;
import util.*;

import client.order.controller.OrderController;

/*
조리 상태 변경하는 패널
"조리중" 버튼: 조리중 주문목록 표시
"조리완료" 버튼: 완료된 주문목록 표시
오른쪽의 "+" 버튼: 메뉴 추가(구현 예정)

버튼 클릭 시 OrderController.changeMode()를 호출 -> OrderList 패널 UI 갱신
*/
public class OrderState extends JPanel {
    private JButton cookingBtn; //조리중 목록 보기
    private JButton doneBtn;    //조리완료 목록 보기
    private JButton addMenu;    //메뉴 추가(구현 예정)

    public OrderState() {
        setLayout(getLayout());
        setPreferredSize(new Dimension(Sizes.PANEL_WIDTH, 60));

        //버튼 생성
        cookingBtn = new JButton("조리중");
        doneBtn = new JButton("조리완료");
        addMenu = new JButton("+"); //기능 구현 예정

        //조리중 버튼 기능: 클릭 시 모드(UI) 변경
        cookingBtn.addActionListener(e -> {
            OrderController.changeMode(OrderList.COOKING_MODE);
        });
        
        //조리완료 버튼 기능: 클릭 시 모드(UI) 변경
        doneBtn.addActionListener(e -> {
            OrderController.changeMode(OrderList.DONE_MODE);
        });

        //버튼 배치
        add(cookingBtn, BorderLayout.WEST);
        add(doneBtn, BorderLayout.CENTER);
        add(addMenu, BorderLayout.EAST);
    }
}