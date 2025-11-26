package client.order.view;

import javax.swing.*;
import java.awt.*;
import java.util.List;
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
        listAreaPanel.setLayout(new BoxLayout(listAreaPanel, BoxLayout.Y_AXIS));
        add(new JScrollPane(listAreaPanel), BorderLayout.CENTER);
    }

    //OrderData라는 객체를 담을 수 있는 
    /*public void displayOrder(List<OrderData> cookingList, List<OrderData> doneList) {
        listAreaPanel.removeAll();

        List<OrderData> targetList;
        if (currentMode == COOKING_MODE) {
            targetList = cookingList;            
        } else {
            targetList = doneList;
        }
    }*/
}
