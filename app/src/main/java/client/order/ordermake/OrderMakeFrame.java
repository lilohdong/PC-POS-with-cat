package client.order.ordermake;

import javax.swing.*;


public class OrderMakeFrame extends JFrame{
    public OrderMakeFrame() {

        setTitle("주문하기");
        setSize(1300, 800);
        setLocationRelativeTo(null);
        setResizable(false);

        add(new OrderMakePanel());

        setVisible(true);
    }
}
