package client.sales.example;

import client.component.SideBar;
import client.sales.SalesMainPanel;
import util.Sizes;

import javax.swing.*;
import java.awt.*;

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
        try {
            UIManager.setLookAndFeel(new com.formdev.flatlaf.FlatLightLaf());
        } catch (Exception ex) {
            System.err.println("Failed to initialize LaF");
        }
        SwingUtilities.invokeLater(() -> {
            SalesExample frame = new SalesExample();
            frame.setVisible(true);
        });
    }
}
