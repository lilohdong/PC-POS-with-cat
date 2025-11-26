package client.order.view;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import util.*;


//상품판매 페이지 - 주문 목록(조리중/조리완료) 패널
public class OrderList extends JPanel {
    private JPanel listAreaPanel; //주문 목록 표시 구역
    //조리중, 완료 구분
    public static final int COOKING_MODE = 0;
    public static final int DONE_MODE = 1;
    private int currentMode = COOKING_MODE;

    public OrderList() {
        setLayout(getLayout());
        setPreferredSize(new Dimension(Sizes.PANEL_WIDTH, 675));
        
        listAreaPanel = new JPanel();
        listAreaPanel.setLayout(new BoxLayout(listAreaPanel, ABORT));
        add(new JScrollPane(listAreaPanel), BorderLayout.CENTER);
    }

    
}
