package client.order;

import java.awt.BorderLayout;

import javax.swing.*;

public class Order extends JPanel {
    public Order(){
        initUI();
    }

    private void initUI() {
        setSize(1016, 832);
        setLayout(new BorderLayout());

        OrderHeader header = new OrderHeader();
        add(header, BorderLayout.NORTH);
    }
}