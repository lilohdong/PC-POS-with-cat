package client.sales.example;

import client.sales.SalesMainPanel;

import javax.swing.*;
import java.awt.*;
import client.game.example.SideBar;
import util.Sizes;

public class SalesExample extends JFrame {
    public SalesExample() {
        setTitle("Sales Example");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setSize(new Dimension(Sizes.FRAME_WIDTH, Sizes.FRAME_HEIGHT));
        SideBar sidebar = new SideBar();
        add(sidebar, BorderLayout.WEST);

        SalesMainPanel stp = new SalesMainPanel();
        add(stp, BorderLayout.CENTER);
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
           SalesExample frame = new SalesExample();
           frame.setVisible(true);
        });
    }
}
