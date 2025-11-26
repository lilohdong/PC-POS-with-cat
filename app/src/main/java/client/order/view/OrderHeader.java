package client.order.view;

import javax.swing.*;
import java.awt.*;
import util.*;

/*
상품판매 페이지: 주문 화면의 최상단 패널
"주문 목록" 제목 표시
"주문하기" 버튼 추가(메뉴선택 UI 구현 예정)
*/
public class OrderHeader extends JPanel{
    private JButton orderBtn;
    
    public OrderHeader() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(Sizes.PANEL_WIDTH, 80));

        //헤더 제목
        JLabel title = new JLabel("주문 목록");
        title.setFont(new Font("맑은 고딕", Font.BOLD, 40));
        //주문 접수 버튼(구현 예정)
        orderBtn = new JButton("주문하기");

        
        add(title, BorderLayout.WEST);
        add(orderBtn, BorderLayout.EAST);
    }
}