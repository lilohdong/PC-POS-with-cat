package client.component;

import util.Sizes;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    public MainFrame() {
        initUI();
        setVisible(true);
    }

    private void initUI() {
        setPreferredSize(new Dimension(Sizes.FRAME_WIDTH,Sizes.FRAME_HEIGHT));
        SideBar sb = new SideBar();
        add(sb, BorderLayout.WEST);

        MainUI mui = new MainUI();
        add(mui, BorderLayout.CENTER);

    }
}