package client.order;

import javax.swing.*;
import java.awt.*;
import util.*;

public class Order extends JPanel {
    public Order(){
        initUI();
    }

    private void initUI() {
        setSize(Sizes.PANEL_WIDTH, Sizes.PANEL_HEIGHT);
        setLayout(new BorderLayout());

        //헤더 패널
        OrderHeader header = new OrderHeader();
        header.setBackground(Color.WHITE);
        add(header, BorderLayout.NORTH);
        //상태 패널
        OrderState state = new OrderState();
        state.setBackground(Color.CYAN);
        add(state, BorderLayout.CENTER);
        //메인 패널(주문 목록)
        OrderList list = new OrderList();
        list.setBackground(Color.WHITE);
        add(list, BorderLayout.SOUTH);
    }
}