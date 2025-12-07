package client.order.view;

import client.order.controller.OrderController;
import util.Sizes;

import javax.swing.*;
import java.awt.*;

/*
조리 상태 탭 전환 패널

"조리중" 버튼: 조리 중인 주문만 표시
"조리완료" 버튼: 조리 완료된 주문 표시
오른쪽 "+" 버튼: 추후 메뉴 추가 기능 예정 (미구현)
*/
public class OrderState extends JPanel {
    private JButton cookingBtn;
    private JButton doneBtn;
    private JButton addMenu;

    public OrderState() {
        setLayout(getLayout());
        setPreferredSize(new Dimension(Sizes.PANEL_WIDTH, 60));

        //탭 버튼 생성
        cookingBtn = new JButton("조리중");
        doneBtn = new JButton("조리완료");
        addMenu = new JButton("+"); //기능 구현 예정

        // 조리중 버튼 클릭 -> OrderList를 COOKING_MODE로 전환
        cookingBtn.addActionListener(e -> {
            OrderController.changeMode(OrderList.COOKING_MODE);
        });

        // 조리완료 버튼 클릭 -> OrderList를 DONE_MODE로 전환
        doneBtn.addActionListener(e -> {
            OrderController.changeMode(OrderList.DONE_MODE);
        });

        // 버튼 배치 (WEST, CENTER, EAST 활용)
        add(cookingBtn, BorderLayout.WEST);
        add(doneBtn, BorderLayout.CENTER);
        add(addMenu, BorderLayout.EAST);
    }
}