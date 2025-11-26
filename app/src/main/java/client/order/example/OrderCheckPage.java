package client.order.example;

import client.component.SideBar;
import client.order.Order;

import javax.swing.*;
import java.awt.*;
import util.*;

public class OrderCheckPage extends JFrame {
    private OrderCheckPage() {
        SideBar sb = new SideBar();
        Order order = new Order();

        add(sb, BorderLayout.WEST);
        add(order, BorderLayout.CENTER);
        setSize(Sizes.FRAME_WIDTH, Sizes.FRAME_HEIGHT);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static void main(String[] args) {
        new OrderCheckPage();
    }
}
