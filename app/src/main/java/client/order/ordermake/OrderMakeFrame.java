package client.order.ordermake;

import javax.swing.*;


public class OrderMakeFrame extends JFrame{
    private OrderMakePanel orderMakePanel;

    public OrderMakeFrame() {

        setTitle("주문하기");
        setSize(1300, 800);
        setLocationRelativeTo(null);
        setResizable(false);

        orderMakePanel = new OrderMakePanel();
        add(new OrderMakePanel());

        setVisible(true);
    }

    public OrderMakePanel getOrderMakePanel() {
        return orderMakePanel;
    }
}
