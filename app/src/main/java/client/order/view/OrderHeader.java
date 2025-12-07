package client.order.view;

import client.order.ordermake.OrderMakeFrame;
import util.Sizes;

import javax.swing.*;
import java.awt.*;

/*
주문 화면 상단 패널

왼쪽: "주문 목록" 큰 제목
오른쪽: "주문하기" 버튼 -> 클릭 시 메뉴 선택 창(OrderMakeFrame) 열림
*/
public class OrderHeader extends JPanel{
    private JButton orderBtn;
    
    public OrderHeader() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(Sizes.PANEL_WIDTH, 80));

        //제목 라벨
        JLabel title = new JLabel("주문 목록");
        title.setFont(new Font("맑은 고딕", Font.BOLD, 40));

        //주문 접수 버튼
        orderBtn = new JButton("주문하기");

        //여백 추가를 위해 패딩 역할의 빈 패널 사용 가능하나, 여기선 간단히 배치
        add(title, BorderLayout.WEST);
        add(orderBtn, BorderLayout.EAST);

        //"주문하기" 버튼 클릭 -> 메뉴 선택 창 띄우기
        orderBtn.addActionListener(e -> {
            new OrderMakeFrame();   // 주문 입력 전용 프레임 생성 및 표시
        });
    }
}