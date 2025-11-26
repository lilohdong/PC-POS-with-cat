package client.order;

import javax.swing.*;
import java.awt.*;
import util.*;

//상품판매 페이지 - 상단 패널 
public class OrderHeader extends JPanel{
    private JButton orderBtn;
    
    public OrderHeader() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(Sizes.PANEL_WIDTH, 80));

        //주문하기 텍스트
        JLabel title = new JLabel("주문 목록");
        title.setFont(new Font("맑은 고딕", Font.BOLD, 40));
        //주문하기 버튼
        orderBtn = new JButton("주문하기");

        
        add(title, BorderLayout.WEST);
        add(orderBtn, BorderLayout.EAST);
    }
}